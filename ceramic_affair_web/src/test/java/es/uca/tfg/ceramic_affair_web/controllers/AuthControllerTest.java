package es.uca.tfg.ceramic_affair_web.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import es.uca.tfg.ceramic_affair_web.DTOs.LoginDTO;
import es.uca.tfg.ceramic_affair_web.controllers.common.AuthController;
import es.uca.tfg.ceramic_affair_web.security.JwtUtils;
import es.uca.tfg.ceramic_affair_web.security.Rol;
import es.uca.tfg.ceramic_affair_web.services.AuthService;
import es.uca.tfg.ceramic_affair_web.services.RecaptchaService;
import es.uca.tfg.ceramic_affair_web.exceptions.AuthException;

/**
 * Clase de prueba para el controlador de autenticación.
 * Proporciona pruebas de capa web para las operaciones de autenticación, incluyendo el inicio de sesión, registro y la obtención de tokens JWT.
 * 
 * @version 1.0
 */
@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc // No desactivar la configuración de seguridad en este caso (login no requiere autenticación previa)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private JwtUtils jwtUtils;

    @MockitoBean
    private RecaptchaService recaptchaService;

    @Test
    @DisplayName("Controlador - Login de usuario con credenciales válidas")
    void testLoginCorrectoUsuario() throws Exception {
        when(authService.login(any(LoginDTO.class), any(Rol.class))).thenReturn("mocked-jwt-token");
        when(recaptchaService.verifyRecaptcha(any(String.class))).thenReturn(true);

        String jsonBody = """
            {
                "email": "user@example.com",
                "password": "password",
                "recaptchaToken": "valid-recaptcha-token"
            }
        """;

        mockMvc.perform(post("/api/public/auth/login/user")
                .contentType("application/json")
                .content(jsonBody))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Login successful"))
            .andExpect(jsonPath("$.data.token").value("mocked-jwt-token"));
    }

    @Test
    @DisplayName("Controlador - Login de usuario con credenciales inválidas")
    void testLoginInvalidoUsuario() throws Exception {
        when(authService.login(any(LoginDTO.class), any(Rol.class)))
            .thenThrow(new AuthException.CredencialesInvalidas());
        when(recaptchaService.verifyRecaptcha(any(String.class))).thenReturn(true);

        String jsonBody = """
            {
                "email": "user@example.com",
                "password": "wrongpassword",
                "recaptchaToken": "valid-recaptcha-token"
            }
        """;

        mockMvc.perform(post("/api/public/auth/login/user")
                .contentType("application/json")
                .content(jsonBody))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.status").value(401))
            .andExpect(jsonPath("$.error").value("Business exception"))
            .andExpect(jsonPath("$.message").value("Invalid credentials"))
            .andExpect(jsonPath("$.path").value("/api/public/auth/login/user"));
    }

    @Test
    @DisplayName("Controlador - Login de admin con credenciales válidas")
    void testLoginCorrectoAdmin() throws Exception {
        when(authService.login(any(LoginDTO.class), any(Rol.class))).thenReturn("mocked-jwt-token");
        when(recaptchaService.verifyRecaptcha(any(String.class))).thenReturn(true);

        String jsonBody = """
            {
                "email": "admin@example.com",
                "password": "adminpassword",
                "recaptchaToken": "valid-recaptcha-token"
            }
        """;

        mockMvc.perform(post("/api/public/auth/login/admin")
                .contentType("application/json")
                .content(jsonBody))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Login successful"))
            .andExpect(jsonPath("$.data.token").value("mocked-jwt-token"));
    }

    @Test
    @DisplayName("Controlador - Login de admin con credenciales inválidas")
    void testLoginInvalidoAdmin() throws Exception {
        when(authService.login(any(LoginDTO.class), any(Rol.class)))
            .thenThrow(new AuthException.CredencialesInvalidas());
        when(recaptchaService.verifyRecaptcha(any(String.class))).thenReturn(true);

        String jsonBody = """
            {
                "email": "admin@example.com",
                "password": "wrongpassword",
                "recaptchaToken": "valid-recaptcha-token"
            }
        """;

        mockMvc.perform(post("/api/public/auth/login/admin")
                .contentType("application/json")
                .content(jsonBody))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.status").value(401))
            .andExpect(jsonPath("$.error").value("Business exception"))
            .andExpect(jsonPath("$.message").value("Invalid credentials"))
            .andExpect(jsonPath("$.path").value("/api/public/auth/login/admin"));
    }

    @Test
    @DisplayName("Controlador - Login con reCAPTCHA inválido")
    void testLoginRecaptchaInvalido() throws Exception {
        when(recaptchaService.verifyRecaptcha(any(String.class))).thenReturn(false);

        String jsonBody = """
            {
                "email": "user@example.com",
                "password": "password",
                "recaptchaToken": "invalid-recaptcha-token"
            }
        """;

        mockMvc.perform(post("/api/public/auth/login/user")
                .contentType("application/json")
                .content(jsonBody))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.error").value("Invalid reCAPTCHA"))
            .andExpect(jsonPath("$.message").value("The reCAPTCHA token is invalid or has expired. Please try again."))
            .andExpect(jsonPath("$.path").value("/api/public/auth/login/user"));
    }

    @Test
    @DisplayName("Controlador - Registro de nuevo usuario")
    void testRegistroUsuario() throws Exception {
        when(recaptchaService.verifyRecaptcha(any(String.class))).thenReturn(true);

        String jsonBody = """
            {
                "email": "newuser@example.com",
                "password": "newpassword",
                "recaptchaToken": "valid-recaptcha-token"
            }
        """;

        mockMvc.perform(post("/api/public/auth/registro")
                .contentType("application/json")
                .content(jsonBody))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Verification email has been sent"))
            .andExpect(jsonPath("$.data").value("Verification email has been sent"));
    }

    @Test
    @DisplayName("Controlador - Registro de usuario con email ya registrado")
    void testRegistroUsuarioEmailYaRegistrado() throws Exception {
        doThrow(new AuthException.EmailYaRegistrado())
            .when(authService).register(any(LoginDTO.class));
        when(recaptchaService.verifyRecaptcha(any(String.class))).thenReturn(true);

        String jsonBody = """
            {
                "email": "existinguser@example.com",
                "password": "password",
                "recaptchaToken": "valid-recaptcha-token"
            }
        """;

        mockMvc.perform(post("/api/public/auth/registro")
                .contentType("application/json")
                .content(jsonBody))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.status").value(409))
            .andExpect(jsonPath("$.error").value("Business exception"))
            .andExpect(jsonPath("$.message").value("Email already registered"))
            .andExpect(jsonPath("$.path").value("/api/public/auth/registro"));

    }

    @Test
    @DisplayName("Controlador - Registro de usuario con verificación pendiente")
    void testRegistroUsuarioVerificacionPendiente() throws Exception {
        doThrow(new AuthException.VerificacionPendiente())
            .when(authService).register(any(LoginDTO.class));
        when(recaptchaService.verifyRecaptcha(any(String.class))).thenReturn(true);

        String jsonBody = """
            {
                "email": "user@example.com",
                "password": "password",
                "recaptchaToken": "valid-recaptcha-token"
            }
        """;

        mockMvc.perform(post("/api/public/auth/registro")
                .contentType("application/json")
                .content(jsonBody))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.error").value("Business exception"))
            .andExpect(jsonPath("$.message").value("Email verification pending"))
            .andExpect(jsonPath("$.path").value("/api/public/auth/registro"));
    }

    @Test
    @DisplayName("Controlador - Registro de usuario con reCAPTCHA inválido")
    void testRegistroRecaptchaInvalido() throws Exception {
        when(recaptchaService.verifyRecaptcha(any(String.class))).thenReturn(false);

        String jsonBody = """
            {
                "email": "user@example.com",
                "password": "password",
                "recaptchaToken": "invalid-recaptcha-token"
            }
        """;

        mockMvc.perform(post("/api/public/auth/registro")
                .contentType("application/json")
                .content(jsonBody))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.error").value("Invalid reCAPTCHA"))
            .andExpect(jsonPath("$.message").value("The reCAPTCHA token is invalid or has expired. Please try again."))
            .andExpect(jsonPath("$.path").value("/api/public/auth/registro"));
    }

    @Test
    @DisplayName("Controlador - Verificación de cuenta con token válido")
    void testVerificacionCuentaValida() throws Exception {
        String token = "valid-verification-token";

        mockMvc.perform(get("/api/public/auth/verificar")
                .param("token", token))
            .andExpect(status().isFound())
            .andExpect(header().string("Location", "http://localhost:5173/confirmation?status=success"));
    }

    @Test
    @DisplayName("Controlador - Verificación de cuenta con token inválido")
    void testVerificacionCuentaInvalida() throws Exception {
        doThrow(new AuthException.UsuarioNoEncontrado("Invalid verification token"))
            .when(authService).verify("invalid-verification-token");

        String token = "invalid-verification-token";

        mockMvc.perform(get("/api/public/auth/verificar")
                .param("token", token))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.error").value("Business exception"))
            .andExpect(jsonPath("$.message").value("Invalid verification token"))
            .andExpect(jsonPath("$.path").value("/api/public/auth/verificar"));
    }

    @Test
    @DisplayName("Controlador - Verificación de cuenta con token expirado")
    void testVerificacionCuentaExpirada() throws Exception {
        doThrow(new AuthException.TokenExpirado("Verification token has expired"))
            .when(authService).verify("expired-verification-token");

        String token = "expired-verification-token";

        mockMvc.perform(get("/api/public/auth/verificar")
                .param("token", token))
            .andExpect(status().isGone())
            .andExpect(jsonPath("$.status").value(410))
            .andExpect(jsonPath("$.error").value("Business exception"))
            .andExpect(jsonPath("$.message").value("Verification token has expired"))
            .andExpect(jsonPath("$.path").value("/api/public/auth/verificar"));
    }

    @Test
    @DisplayName("Controlador - Cambio de contraseña con datos válidos")
    void testCambioContrasenaValido() throws Exception {
        when(recaptchaService.verifyRecaptcha(any(String.class))).thenReturn(true);

        String jsonBody = """
            {
                "email": "user@example.com",
                "token": "valid-recaptcha-token",
                "antiguaPassword": "oldpassword",
                "nuevaPassword": "newpassword"
            }
        """;

        mockMvc.perform(post("/api/public/auth/cambio")
                .contentType("application/json")
                .content(jsonBody))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Password changed successfully"))
            .andExpect(jsonPath("$.data").value("Password changed successfully"));
    }

    @Test
    @DisplayName("Controlador - Cambio de contraseña con reCAPTCHA inválido")
    void testCambioContrasenaRecaptchaInvalido() throws Exception {
        when(recaptchaService.verifyRecaptcha(any(String.class))).thenReturn(false);

        String jsonBody = """
            {
                "email": "user@example.com",
                "token": "valid-recaptcha-token",
                "antiguaPassword": "oldpassword",
                "nuevaPassword": "newpassword"
            }
        """;

        mockMvc.perform(post("/api/public/auth/cambio")
                .contentType("application/json")
                .content(jsonBody))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.error").value("Invalid reCAPTCHA"))
            .andExpect(jsonPath("$.message").value("The reCAPTCHA token is invalid or has expired. Please try again."))
            .andExpect(jsonPath("$.path").value("/api/public/auth/cambio"));
    }

    @Test
    @DisplayName("Controlador - Cambio de contraseña con credenciales inválidas")
    void testCambioContrasenaCredencialesInvalidas() throws Exception {
        doThrow(new AuthException.CredencialesInvalidas("Invalid credentials"))
            .when(authService).cambiarContrasena(any(String.class), any(String.class), any(String.class));
        when(recaptchaService.verifyRecaptcha(any(String.class))).thenReturn(true);

        String jsonBody = """
            {
                "email": "user@example.com",
                "token": "valid-recaptcha-token",
                "antiguaPassword": "oldpassword",
                "nuevaPassword": "newpassword"
            }
        """;

        mockMvc.perform(post("/api/public/auth/cambio")
                .contentType("application/json")
                .content(jsonBody))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.status").value(401))
            .andExpect(jsonPath("$.error").value("Business exception"))
            .andExpect(jsonPath("$.message").value("Invalid credentials"))
            .andExpect(jsonPath("$.path").value("/api/public/auth/cambio"));
    }

    @Test
    @DisplayName("Controlador - Cambio de contraseña con usuario no encontrado")
    void testCambioContrasenaUsuarioNoEncontrado() throws Exception {
        doThrow(new AuthException.UsuarioNoEncontrado("User not found"))
            .when(authService).cambiarContrasena(any(String.class), any(String.class), any(String.class));
        when(recaptchaService.verifyRecaptcha(any(String.class))).thenReturn(true);

        String jsonBody = """
            {
                "email": "user@example.com",
                "token": "valid-recaptcha-token",
                "antiguaPassword": "oldpassword",
                "nuevaPassword": "newpassword"
            }
        """;

        mockMvc.perform(post("/api/public/auth/cambio")
                .contentType("application/json")
                .content(jsonBody))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.error").value("Business exception"))
            .andExpect(jsonPath("$.message").value("User not found"))
            .andExpect(jsonPath("$.path").value("/api/public/auth/cambio"));
    }

    @Test
    @DisplayName("Controlador - Solicitud de restablecimiento de contraseña con email válido")
    void testSolicitudRestablecimientoConEmailValido() throws Exception {
        when(recaptchaService.verifyRecaptcha(any(String.class))).thenReturn(true);

        String jsonBody = """
            {
                "email": "user@example.com",
                "recaptchaToken": "valid-recaptcha-token"
            }
        """;

        mockMvc.perform(post("/api/public/auth/olvido")
                .contentType("application/json")
                .content(jsonBody))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Recovery email has been sent"))
            .andExpect(jsonPath("$.data").value("Recovery email has been sent"));
    }

    @Test
    @DisplayName("Controlador - Solicitud de restablecimiento de contraseña con reCAPTCHA inválido")
    void testSolicitudRestablecimientoRecaptchaInvalido() throws Exception {
        when(recaptchaService.verifyRecaptcha(any(String.class))).thenReturn(false);

        String jsonBody = """
            {
                "email": "user@example.com",
                "recaptchaToken": "invalid-recaptcha-token"
            }
        """;

        mockMvc.perform(post("/api/public/auth/olvido")
                .contentType("application/json")
                .content(jsonBody))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.error").value("Invalid reCAPTCHA"))
            .andExpect(jsonPath("$.message").value("The reCAPTCHA token is invalid or has expired. Please try again."))
            .andExpect(jsonPath("$.path").value("/api/public/auth/olvido"));
    }

    @Test
    @DisplayName("Controlador - Solicitud de restablecimiento de contraseña con usuario no encontrado")
    void testSolicitudRestablecimientoUsuarioNoEncontrado() throws Exception {
        doThrow(new AuthException.UsuarioNoEncontrado())
            .when(authService).solicitarRecuperacion(any(String.class));
        when(recaptchaService.verifyRecaptcha(any(String.class))).thenReturn(true);

        String jsonBody = """
            {
                "email": "user@example.com",
                "recaptchaToken": "valid-recaptcha-token"
            }
        """;

        mockMvc.perform(post("/api/public/auth/olvido")
                .contentType("application/json")
                .content(jsonBody))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.error").value("Business exception"))
            .andExpect(jsonPath("$.message").value("User not found"))
            .andExpect(jsonPath("$.path").value("/api/public/auth/olvido"));
    }

    @Test
    @DisplayName("Controlador - Restablecimiento de contraseña con token válido")
    void testRestablecimientoConTokenValido() throws Exception {
        String jsonBody = """
            {
                "token": "valid-reset-token",
                "nuevaPassword": "newpassword"
            }
        """;

        mockMvc.perform(post("/api/public/auth/reset")
                .contentType("application/json")
                .content(jsonBody))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Password has been reset successfully"))
            .andExpect(jsonPath("$.data").value("Password has been reset successfully"));
    }

    @Test
    @DisplayName("Controlador - Restablecimiento de contraseña con token expirado")
    void testRestablecimientoConTokenExpirado() throws Exception {
        doThrow(new AuthException.TokenExpirado("Reset token has expired"))
            .when(authService).resetPassword(any(String.class), any(String.class));

        String jsonBody = """
            {
                "token": "expired-reset-token",
                "nuevaPassword": "newpassword"
            }
        """;

        mockMvc.perform(post("/api/public/auth/reset")
                .contentType("application/json")
                .content(jsonBody))
            .andExpect(status().isGone())
            .andExpect(jsonPath("$.status").value(410))
            .andExpect(jsonPath("$.error").value("Business exception"))
            .andExpect(jsonPath("$.message").value("Reset token has expired"))
            .andExpect(jsonPath("$.path").value("/api/public/auth/reset"));
    }

    @Test
    @DisplayName("Controlador - Restablecimiento de contraseña con usuario no encontrado")
    void testRestablecimientoUsuarioNoEncontrado() throws Exception {
        doThrow(new AuthException.UsuarioNoEncontrado("User not found"))
            .when(authService).resetPassword(any(String.class), any(String.class));

        String jsonBody = """
            {
                "token": "valid-reset-token",
                "nuevaPassword": "newpassword"
            }
        """;

        mockMvc.perform(post("/api/public/auth/reset")
                .contentType("application/json")
                .content(jsonBody))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.error").value("Business exception"))
            .andExpect(jsonPath("$.message").value("User not found"))
            .andExpect(jsonPath("$.path").value("/api/public/auth/reset"));
    }

    // Configuración para desactivar la seguridad correctamente
    @TestConfiguration
    public static class NoSecurityConfig {
        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            http.csrf(csrf -> csrf.disable()) // Desactivar CSRF para simplificar las pruebas
                .authorizeHttpRequests(auth -> auth
                    .anyRequest().permitAll() // Permitir todas las solicitudes en las pruebas
                );
            return http.build();
        }
    }
}
