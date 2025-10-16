package es.uca.tfg.ceramic_affair_web.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import es.uca.tfg.ceramic_affair_web.DTOs.LoginDTO;
import es.uca.tfg.ceramic_affair_web.entities.Usuario;
import es.uca.tfg.ceramic_affair_web.exceptions.AuthException;
import es.uca.tfg.ceramic_affair_web.repositories.UsuarioRepo;
import es.uca.tfg.ceramic_affair_web.security.JwtUtils;
import es.uca.tfg.ceramic_affair_web.security.Rol;
import jakarta.transaction.Transactional;

/**
 * Clase de prueba para el servicio AuthService.
 * Proporciona pruebas de integración para las operaciones de autenticación y registro de usuarios.
 * 
 * @version 1.0
 */
@SpringBootTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UsuarioRepo usuarioRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    @MockitoBean
    private GmailEmailService emailService;

    @MockitoBean
    private RecaptchaService recaptchaService;

    @Test
    @DisplayName("Servicio - Login correcto (Usuario)")
    public void testLoginCorrectoUsuario() {
        // Crear un usuario de prueba
        Usuario usuario = new Usuario(
            "test@example.com", 
            passwordEncoder.encode("password"), 
            Set.of(Rol.USER)
        );
        usuario.setVerificado(true);

        usuarioRepo.save(usuario);

        // Intentar iniciar sesión con las credenciales correctas
        String token = authService.login(
            new LoginDTO("test@example.com", "password"), 
            Rol.USER
        );

        // Verificar que se ha generado un token
        assertNotNull(token);
        assertThat(jwtUtils.validateToken(token)).isTrue();
    }

    @Test
    @DisplayName("Servicio - Login correcto (Admin)")
    public void testLoginCorrectoAdmin() {
        // Crear un usuario de prueba con rol ADMIN
        Usuario usuario = new Usuario(
            "admin@example.com", 
            passwordEncoder.encode("password"), 
            Set.of(Rol.ADMIN)
        );
        usuario.setVerificado(true);

        usuarioRepo.save(usuario);

        // Intentar iniciar sesión con las credenciales correctas
        String token = authService.login(
            new LoginDTO("admin@example.com", "password"), 
            Rol.ADMIN
        );

        // Verificar que se ha generado un token
        assertNotNull(token);
        assertThat(jwtUtils.validateToken(token)).isTrue();
    }

    @Test
    @DisplayName("Servicio - Login fallido (Usuario no encontrado)")
    public void testLoginFallidoUsuarioNoEncontrado() {
        // Intentar iniciar sesión con un email no registrado
        assertThatThrownBy(() -> {
            authService.login(
                new LoginDTO("unknown@example.com", "password"),
                Rol.USER
            );
        }).isInstanceOf(AuthException.CredencialesInvalidas.class);
    }

    @Test
    @DisplayName("Servicio - Login fallido (Usuario no verificado)")
    public void testLoginFallidoUsuarioNoVerificado() {
        // Crear un usuario de prueba no verificado
        Usuario usuario = new Usuario(
            "unverified@example.com",
            passwordEncoder.encode("password"),
            Set.of(Rol.USER)
        );

        usuarioRepo.save(usuario);

        // Intentar iniciar sesión con un usuario no verificado
        assertThatThrownBy(() -> {
            authService.login(
                new LoginDTO("unverified@example.com", "password"),
                Rol.USER
            );
        }).isInstanceOf(AuthException.VerificacionPendiente.class);
    }

    @Test
    @DisplayName("Servicio - Login fallido (Contraseña incorrecta)")
    public void testLoginFallidoContrasenaIncorrecta() {
        // Crear un usuario de prueba
        Usuario usuario = new Usuario(
            "test@example.com",
            passwordEncoder.encode("password"),
            Set.of(Rol.USER)
        );
        usuario.setVerificado(true);

        usuarioRepo.save(usuario);

        // Intentar iniciar sesión con una contraseña incorrecta
        assertThatThrownBy(() -> {
            authService.login(
                new LoginDTO("test@example.com", "wrongpassword"),
                Rol.USER
            );
        }).isInstanceOf(AuthException.CredencialesInvalidas.class);
    }

    @Test
    @DisplayName("Servicio - Login fallido (Rol incorrecto)")
    public void testLoginFallidoRolIncorrecto() {
        // Crear un usuario de prueba
        Usuario usuario = new Usuario(
            "test@example.com",
            passwordEncoder.encode("password"),
            Set.of(Rol.USER)
        );

        usuarioRepo.save(usuario);

        // Intentar iniciar sesión con un rol incorrecto
        assertThatThrownBy(() -> {
            authService.login(
                new LoginDTO("test@example.com", "password"),
                Rol.ADMIN
            );
        }).isInstanceOf(AuthException.CredencialesInvalidas.class);
    }

    @Test
    @DisplayName("Servicio - Registro correcto")
    public void testRegistroCorrecto() {
        // Intentar registrar un nuevo usuario
        authService.register(new LoginDTO("newuser@example.com", "password"));

        // Verificar que el usuario se ha guardado en la base de datos
        Optional<Usuario> usuarioOpt = usuarioRepo.findByEmail("newuser@example.com");
        assertThat(usuarioOpt).isPresent();
        assertThat(usuarioOpt.get().getEmail()).isEqualTo("newuser@example.com");
        assertThat(usuarioOpt.get().isVerificado()).isFalse();

        // Comprobar que se ha enviado el email de verificación (mockeado)
        verify(emailService).sendEmail(eq("newuser@example.com"), anyString(), anyString());
    }

    @Test
    @DisplayName("Servicio - Registro correcto (Reenvío de verificación)")
    public void testRegistroCorrectoReenvioVerificacion() {
        // Crear un usuario de prueba
        authService.register(new LoginDTO("user@example.com", "password"));

        // Obtener el usuario y expirar su token de verificación
        Optional<Usuario> usuarioOpt = usuarioRepo.findByEmail("user@example.com");
        Usuario usuario = usuarioOpt.get();
        usuario.setFechaExpiracionTokenVerificacion(usuario.getFechaExpiracionTokenVerificacion().minusDays(2));
        usuarioRepo.save(usuario);

        // Intentar registrar el mismo usuario nuevamente para reenviar el email de verificación
        authService.register(new LoginDTO("user@example.com", "password"));

        // Comprobar que se ha enviado el email de verificación (mockeado)
        verify(emailService, times(2)).sendEmail(eq("user@example.com"), anyString(), anyString());
    }

    @Test
    @DisplayName("Servicio - Registro fallido (Email ya registrado)")
    public void testRegistroFallidoEmailYaRegistrado() {
        // Crear un usuario de prueba
        authService.register(new LoginDTO("existinguser@example.com", "password"));

        // Obtener el usuario y verificarlo
        Optional<Usuario> usuarioOpt = usuarioRepo.findByEmail("existinguser@example.com");
        Usuario usuario = usuarioOpt.get();
        usuario.verificar();
        usuarioRepo.save(usuario);

        // Intentar registrar el mismo usuario nuevamente una vez verificado
        assertThatThrownBy(() -> {
            authService.register(new LoginDTO("existinguser@example.com", "password"));
        }).isInstanceOf(AuthException.EmailYaRegistrado.class);
    }

    @Test
    @DisplayName("Servicio - Registro fallido (Verificación pendiente)")
    public void testRegistroFallidoVerificacionPendiente() {
        // Crear un usuario de prueba
        authService.register(new LoginDTO("unverified@example.com", "password"));

        // Intentar registrar el mismo usuario nuevamente antes de verificar su email
        assertThatThrownBy(() -> {
            authService.register(new LoginDTO("unverified@example.com", "password"));
        }).isInstanceOf(AuthException.VerificacionPendiente.class);
    }

    @Test
    @DisplayName("Servicio - Verificar usuario correctamente")
    public void testVerificarUsuarioCorrectamente() {
        // Crear un usuario de prueba
        authService.register(new LoginDTO("verify@example.com", "password"));

        // Verificar al usuario usando su token de verificación
        Optional<Usuario> usuarioOpt = usuarioRepo.findByEmail("verify@example.com");
        authService.verify(usuarioOpt.get().getTokenVerificacion());

        // Comprobar que el usuario está verificado
        usuarioOpt = usuarioRepo.findByEmail("verify@example.com");
        assertThat(usuarioOpt).isPresent();
        assertThat(usuarioOpt.get().isVerificado()).isTrue();
    }

    @Test
    @DisplayName("Servicio - Verificar usuario fallido (Token inválido)")
    public void testVerificarUsuarioFallidoTokenInvalido() {
        // Intentar verificar un usuario con un token inválido
        assertThatThrownBy(() -> {
            authService.verify("invalid-token");
        }).isInstanceOf(AuthException.UsuarioNoEncontrado.class);
    }

    @Test
    @DisplayName("Servicio - Verificar usuario fallido (Token expirado)")
    public void testVerificarUsuarioFallidoTokenExpirado() {
        // Crear un usuario de prueba
        authService.register(new LoginDTO("expired@example.com", "password"));

        // Expirar el token de verificación
        Optional<Usuario> usuarioOpt = usuarioRepo.findByEmail("expired@example.com");
        Usuario usuario = usuarioOpt.get();
        usuario.setFechaExpiracionTokenVerificacion(usuario.getFechaExpiracionTokenVerificacion().minusDays(2));
        usuarioRepo.save(usuario);

        // Intentar verificar al usuario con el token expirado
        assertThatThrownBy(() -> {
            authService.verify(usuario.getTokenVerificacion());
        }).isInstanceOf(AuthException.TokenExpirado.class);
    }

    @Test
    @DisplayName("Servicio - Cambio de contraseña correcto")
    public void testCambioContrasenaCorrecto() {
        // Crear un usuario de prueba
        authService.register(new LoginDTO("change@example.com", "password"));

        // Verificar al usuario
        Optional<Usuario> usuarioOpt = usuarioRepo.findByEmail("change@example.com");
        authService.verify(usuarioOpt.get().getTokenVerificacion());

        // Cambiar la contraseña del usuario
        authService.cambiarContrasena(usuarioOpt.get().getEmail(), "password", "newpassword");

        // Comprobar que la contraseña se ha actualizado
        usuarioOpt = usuarioRepo.findByEmail("change@example.com");
        assertThat(passwordEncoder.matches("newpassword", usuarioOpt.get().getPassword())).isTrue();
    }

    @Test
    @DisplayName("Servicio - Cambio de contraseña fallido (Usuario no encontrado)")
    public void testCambioContrasenaFallidoUsuarioNoEncontrado() {
        // Intentar cambiar la contraseña de un usuario no registrado
        assertThatThrownBy(() -> {
            authService.cambiarContrasena("notfound@example.com", "password", "newpassword");
        }).isInstanceOf(AuthException.UsuarioNoEncontrado.class);
    }

    @Test
    @DisplayName("Servicio - Cambio de contraseña fallido (Contraseña actual incorrecta)")
    public void testCambioContrasenaFallidoContrasenaActualIncorrecta() {
        // Crear un usuario de prueba
        authService.register(new LoginDTO("fail@example.com", "password"));

        // Verificar al usuario
        Optional<Usuario> usuarioOpt = usuarioRepo.findByEmail("fail@example.com");
        authService.verify(usuarioOpt.get().getTokenVerificacion());

        // Intentar cambiar la contraseña con la contraseña actual incorrecta
        assertThatThrownBy(() -> {
            authService.cambiarContrasena(usuarioOpt.get().getEmail(), "wrongpassword", "newpassword");
        }).isInstanceOf(AuthException.CredencialesInvalidas.class);
    }

    @Test
    @DisplayName("Servicio - Solicitar restablecimiento de contraseña correctamente")
    public void testSolicitarRestablecimientoContrasenaCorrecto() {
        // Crear un usuario de prueba
        authService.register(new LoginDTO("reset@example.com", "password"));

        // Verificar al usuario
        Optional<Usuario> usuarioOpt = usuarioRepo.findByEmail("reset@example.com");
        authService.verify(usuarioOpt.get().getTokenVerificacion());

        // Solicitar el restablecimiento de la contraseña
        authService.solicitarRecuperacion("reset@example.com");

        // Comprobar que se ha generado token de recuperación y se ha enviado el email (mockeado)
        // Hay 2 llamadas porque se envía un email al registrarse y otro al solicitar la recuperación
        usuarioOpt = usuarioRepo.findByEmail("reset@example.com");
        assertThat(usuarioOpt.get().getTokenRecuperacion()).isNotNull();
        assertThat(usuarioOpt.get().getFechaExpiracionTokenRecuperacion()).isNotNull();
        verify(emailService, times(2)).sendEmail(eq("reset@example.com"), anyString(), anyString());
    }

    @Test
    @DisplayName("Servicio - Solicitar restablecimiento de contraseña fallido (Usuario no encontrado)")
    public void testSolicitarRestablecimientoContrasenaFallidoUsuarioNoEncontrado() {
        // Intentar solicitar el restablecimiento de la contraseña para un usuario no registrado
        assertThatThrownBy(() -> {
            authService.solicitarRecuperacion("notfound@example.com");
        }).isInstanceOf(AuthException.UsuarioNoEncontrado.class);
    }

    @Test
    @DisplayName("Servicio - Restablecer contraseña correctamente")
    public void testRestablecerContrasenaCorrectamente() {
        // Crear un usuario de prueba
        authService.register(new LoginDTO("reset@example.com", "password"));

        // Verificar al usuario
        Optional<Usuario> usuarioOpt = usuarioRepo.findByEmail("reset@example.com");
        authService.verify(usuarioOpt.get().getTokenVerificacion());

        // Solicitar el restablecimiento de la contraseña
        authService.solicitarRecuperacion("reset@example.com");

        // Restablecer la contraseña
        usuarioOpt = usuarioRepo.findByEmail("reset@example.com");
        authService.resetPassword(usuarioOpt.get().getTokenRecuperacion(), "newpassword");

        // Comprobar que la contraseña se ha actualizado
        usuarioOpt = usuarioRepo.findByEmail("reset@example.com");
        assertThat(passwordEncoder.matches("newpassword", usuarioOpt.get().getPassword())).isTrue();
    }

    @Test
    @DisplayName("Servicio - Restablecer contraseña fallido (Token inválido)")
    public void testRestablecerContrasenaFallidoTokenInvalido() {
        // Intentar restablecer la contraseña con un token inválido
        assertThatThrownBy(() -> {
            authService.resetPassword("invalid-token", "newpassword");
        }).isInstanceOf(AuthException.UsuarioNoEncontrado.class);
    }

    @Test
    @DisplayName("Servicio - Restablecer contraseña fallido (Token expirado)")
    public void testRestablecerContrasenaFallidoTokenExpirado() {
        // Crear un usuario de prueba
        authService.register(new LoginDTO("expired@example.com", "password"));

        // Verificar al usuario
        Optional<Usuario> usuarioOpt = usuarioRepo.findByEmail("expired@example.com");
        authService.verify(usuarioOpt.get().getTokenVerificacion());

        // Solicitar el restablecimiento de la contraseña
        authService.solicitarRecuperacion("expired@example.com");

        // Expirar el token de recuperación
        Optional<Usuario> usuarioOpt2 = usuarioRepo.findByEmail("expired@example.com");
        usuarioOpt2.get().setFechaExpiracionTokenRecuperacion(
            usuarioOpt2.get().getFechaExpiracionTokenRecuperacion().minusDays(2)
        );
        usuarioRepo.save(usuarioOpt2.get());

        // Intentar restablecer la contraseña con el token expirado
        assertThatThrownBy(() -> {
            authService.resetPassword(usuarioOpt2.get().getTokenRecuperacion(), "newpassword");
        }).isInstanceOf(AuthException.TokenExpirado.class);
    }
}
