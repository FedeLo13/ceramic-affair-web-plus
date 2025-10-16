package es.uca.tfg.ceramic_affair_web.services;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import es.uca.tfg.ceramic_affair_web.DTOs.LoginDTO;
import es.uca.tfg.ceramic_affair_web.entities.Usuario;
import es.uca.tfg.ceramic_affair_web.exceptions.AuthException;
import es.uca.tfg.ceramic_affair_web.exceptions.EmailException;
import es.uca.tfg.ceramic_affair_web.repositories.UsuarioRepo;
import es.uca.tfg.ceramic_affair_web.security.JwtUtils;
import es.uca.tfg.ceramic_affair_web.security.Rol;

/**
 * Servicio para la autenticación y gestión de usuarios.
 * 
 * @version 1.1
 */
@Service
public class AuthService {

    @Autowired
    private UsuarioRepo usuarioRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private EmailService emailService;

    /**
     * Método para verificar las credenciales y el rol de un usuario o administrador.
     * 
     * @param loginDTO El DTO que contiene el email y la contraseña del usuario.
     * @param requiredRole El rol requerido para la autenticación (USER o ADMIN).
     * @return Un token JWT si las credenciales y el rol son válidos.
     * @throws AuthException.CredencialesInvalidas si las credenciales son inválidas o el rol no coincide.
     */
    public String login(LoginDTO loginDTO, Rol requiredRole) {
        Usuario usuario = usuarioRepo.findByEmail(loginDTO.getEmail())
            .orElseThrow(() -> new AuthException.CredencialesInvalidas());

        // Si el login es para USER, comprobar que es SÓLO un USER
        if (requiredRole == Rol.USER) {
            boolean esSoloUser = usuario.getRoles().size() == 1 && usuario.getRoles().contains(Rol.USER);
            if (!esSoloUser) {
                throw new AuthException.CredencialesInvalidas();
            }
        }

        // Si el login es para ADMIN, comprobar que tiene rol ADMIN
        if (requiredRole == Rol.ADMIN){
            if (!usuario.getRoles().contains(Rol.ADMIN)) {
                throw new AuthException.CredencialesInvalidas();
            }
        }

        // Comprobar que el usuario está verificado
        if (!usuario.isVerificado()) {
            throw new AuthException.VerificacionPendiente();
        }

        // Comprobar la contraseña
        if (!passwordEncoder.matches(loginDTO.getPassword(), usuario.getPassword())) {
            throw new AuthException.CredencialesInvalidas();
        }

        return jwtUtils.generateToken(usuario);
    }

    /**
     * Método para registrar un nuevo usuario.
     * 
     * @param loginDTO El DTO que contiene el email y la contraseña del nuevo usuario.
     * @throws AuthException.EmailYaRegistrado si ya existe un usuario con el mismo email.
     */
    public void register(LoginDTO loginDTO) {
        // Comprobar si el email ya está registrado
        Optional<Usuario> usuarioExistente = usuarioRepo.findByEmail(loginDTO.getEmail());

        // Si esta presente...
        if (usuarioExistente.isPresent()) {
            Usuario usuario = usuarioExistente.get();

            // ... y está verificado, lanzar excepción
            if (usuario.isVerificado()) {
                throw new AuthException.EmailYaRegistrado();
            }

            // ... y no está verificado, pero el token de verificación aún no expiró, lanzar excepción
            if(LocalDateTime.now().isBefore(usuario.getFechaExpiracionTokenVerificacion())) {
                throw new AuthException.VerificacionPendiente();
            }

            // Si no se lanzó ninguna excepción, es porque el usuario no está verificado y el token ha expirado
            // Regenerar el token de verificación
            usuario.regenerarTokenVerificacion();
            usuarioRepo.save(usuario);
            enviarCorreoVerificacion(usuario);
        } else { // Si no esta presente...
            // Crear un nuevo usuario con rol USER por defecto
            Usuario nuevoUsuario = new Usuario(
                loginDTO.getEmail(),
                passwordEncoder.encode(loginDTO.getPassword()),
                Set.of(Rol.USER) // Asignar rol USER por defecto
            );

            usuarioRepo.save(nuevoUsuario);
            enviarCorreoVerificacion(nuevoUsuario);
        }
    }

    /**
     * Método para verificar la cuenta de un usuario utilizando un token de verificación.
     * 
     * @param token El token de verificación enviado al email del usuario.
     * @throws AuthException.UsuarioNoEncontrado si no se encuentra un usuario con el token proporcionado.
     * @throws AuthException.TokenExpirado si el token ha expirado.
     */
    public void verify(String token) {
        // Buscar el usuario por el token de verificación
        Optional<Usuario> optionalUsuario = usuarioRepo.findByTokenVerificacion(token);

        // Si no se encuentra el usuario, lanzar una excepción
        if (optionalUsuario.isEmpty()) {
            throw new AuthException.UsuarioNoEncontrado();
        }

        // Si el token ha expirado, lanzar una excepción
        Usuario usuario = optionalUsuario.get();
        if (LocalDateTime.now().isAfter(usuario.getFechaExpiracionTokenVerificacion())) {
            throw new AuthException.TokenExpirado();
        }

        // Verificar el usuario
        usuario.verificar(); // Establecer verificado a true y limpiar el token
        usuarioRepo.save(usuario);
    }

    /**
     * Método para cambiar la contraseña de un usuario autenticado.
     * 
     * @param email El email del usuario que desea cambiar su contraseña.
     * @param antiguaPassword La contraseña actual del usuario.
     * @param nuevaPassword La nueva contraseña que el usuario desea establecer.
     * @throws AuthException.UsuarioNoEncontrado si no se encuentra un usuario con el email proporcionado.
     * @throws AuthException.CredencialesInvalidas si la contraseña actual es incorrecta.
     */
    public void cambiarContrasena(String email, String antiguaPassword, String nuevaPassword) {
        // Buscar el usuario por email
        Optional<Usuario> optionalUsuario = usuarioRepo.findByEmail(email);

        // Si no se encuentra el usuario, lanzar una excepción
        if (optionalUsuario.isEmpty()) {
            throw new AuthException.UsuarioNoEncontrado();
        }

        Usuario usuario = optionalUsuario.get();

        // Verificar la contraseña actual
        if (!passwordEncoder.matches(antiguaPassword, usuario.getPassword())) {
            throw new AuthException.CredencialesInvalidas();
        }

        // Actualizar la contraseña
        usuario.setPassword(passwordEncoder.encode(nuevaPassword));
        usuarioRepo.save(usuario);
    }

    /**
     * Método para solicitar la recuperación de contraseña.
     * 
     * @param email El email del usuario que solicita la recuperación.
     * @throws AuthException.UsuarioNoEncontrado si no se encuentra un usuario con el email proporcionado.
     */
    public void solicitarRecuperacion(String email) {
        // Buscar el usuario por email
        Optional<Usuario> optionalUsuario = usuarioRepo.findByEmail(email);

        // Si no se encuentra el usuario, lanzar una excepción
        if (optionalUsuario.isEmpty()) {
            throw new AuthException.UsuarioNoEncontrado();
        }

        // Generar un nuevo token de recuperación y establecer la fecha de expiración
        Usuario usuario = optionalUsuario.get();
        usuario.generarTokenRecuperacion();
        usuarioRepo.save(usuario);

        // Enviar el correo de recuperación
        enviarCorreoRecuperacion(usuario);
    }

    /**
     * Método para restablecer la contraseña de un usuario utilizando un token de recuperación.
     * 
     * @param token El token de recuperación enviado al email del usuario.
     * @param nuevaPassword La nueva contraseña que el usuario desea establecer.
     * @throws AuthException.UsuarioNoEncontrado si no se encuentra un usuario con el token proporcionado.
     * @throws AuthException.TokenExpirado si el token ha expirado.
     */
    public void resetPassword(String token, String nuevaPassword) {
        // Buscar el usuario por el token de recuperación
        Optional<Usuario> optionalUsuario = usuarioRepo.findByTokenRecuperacion(token);

        // Si no se encuentra el usuario, lanzar una excepción
        if (optionalUsuario.isEmpty()) {
            throw new AuthException.UsuarioNoEncontrado();
        }

        // Si el token ha expirado, lanzar una excepción
        Usuario usuario = optionalUsuario.get();
        if (LocalDateTime.now().isAfter(usuario.getFechaExpiracionTokenRecuperacion())) {
            throw new AuthException.TokenExpirado();
        }

        // Actualizar la contraseña y limpiar el token de recuperación
        usuario.setPassword(passwordEncoder.encode(nuevaPassword));
        usuario.setTokenRecuperacion(null);
        usuario.setFechaExpiracionTokenRecuperacion(null);
        usuarioRepo.save(usuario);
    }

    private void enviarCorreoRecuperacion(Usuario usuario) {
        String enlace = "http://localhost:5173/reset-password?token=" + usuario.getTokenRecuperacion();
        String cuerpo = "<p>Hello,</p>"
                + "<p>Please reset your password by clicking the link below:</p>"
                + "<a href=\"" + enlace + "\">Reset Password</a>"
                + "<p>This link will expire in 1 hour.</p>"
                + "<p>Thank you!</p>";
        try {
            emailService.sendEmail(usuario.getEmail(), "Password Recovery", cuerpo);
        } catch (Exception e) {
            throw new EmailException.EnvioFallido(e);
        }
    }

    private void enviarCorreoVerificacion(Usuario usuario) {
        String enlace = "http://localhost:8080/api/public/auth/verificar?token=" + usuario.getTokenVerificacion();
        String cuerpo = "<p>Hello,</p>"
                + "<p>Please verify your email by clicking the link below:</p>"
                + "<a href=\"" + enlace + "\">Verify Email</a>"
                + "<p>This link will expire in 24 hours.</p>"
                + "<p>Thank you!</p>";
        try {
            emailService.sendEmail(usuario.getEmail(), "Email Verification", cuerpo);
        } catch (Exception e) {
            throw new EmailException.EnvioFallido(e);
        }
    }
}
