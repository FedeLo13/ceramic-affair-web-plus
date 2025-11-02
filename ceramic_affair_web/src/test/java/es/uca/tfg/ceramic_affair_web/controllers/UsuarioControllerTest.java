package es.uca.tfg.ceramic_affair_web.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import es.uca.tfg.ceramic_affair_web.controllers.user.UsuarioController;
import es.uca.tfg.ceramic_affair_web.exceptions.AuthException;
import es.uca.tfg.ceramic_affair_web.security.JwtAuthFilter;
import es.uca.tfg.ceramic_affair_web.security.JwtUtils;
import es.uca.tfg.ceramic_affair_web.services.AuthService;
import es.uca.tfg.ceramic_affair_web.services.RecaptchaService;

/**
 * Clase de prueba para el controlador UsuarioController.
 * Proporciona pruebas de capa web para las operaciones relacionadas con los usuarios.
 * 
 * @version 1.0
 */
@WebMvcTest(controllers = UsuarioController.class)
@AutoConfigureMockMvc(addFilters = false) // Desactiva los filtros de seguridad para las pruebas
public class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private RecaptchaService recaptchaService;

    @MockitoBean
    private JwtUtils jwtUtils;

    @MockitoBean
    private JwtAuthFilter jwtAuthFilter;

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

        mockMvc.perform(post("/api/user/cambio")
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

        mockMvc.perform(post("/api/user/cambio")
                .contentType("application/json")
                .content(jsonBody))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.error").value("Invalid reCAPTCHA"))
            .andExpect(jsonPath("$.message").value("The reCAPTCHA token is invalid or has expired. Please try again."))
            .andExpect(jsonPath("$.path").value("/api/user/cambio"));
    }

    @Test
    @DisplayName("Controlador - Cambio de contraseña con credenciales inválidas")
    void testCambioContrasenaCredencialesInvalidas() throws Exception {
        doThrow(new AuthException.AntiguaPasswordInvalida("Old password is incorrect"))
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

        mockMvc.perform(post("/api/user/cambio")
                .contentType("application/json")
                .content(jsonBody))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.status").value(403))
            .andExpect(jsonPath("$.error").value("Business exception"))
            .andExpect(jsonPath("$.message").value("Old password is incorrect"))
            .andExpect(jsonPath("$.path").value("/api/user/cambio"));
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

        mockMvc.perform(post("/api/user/cambio")
                .contentType("application/json")
                .content(jsonBody))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.error").value("Business exception"))
            .andExpect(jsonPath("$.message").value("User not found"))
            .andExpect(jsonPath("$.path").value("/api/user/cambio"));
    }
}
