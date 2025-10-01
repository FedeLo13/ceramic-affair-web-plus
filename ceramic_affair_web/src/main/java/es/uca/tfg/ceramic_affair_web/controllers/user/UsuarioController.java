package es.uca.tfg.ceramic_affair_web.controllers.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.uca.tfg.ceramic_affair_web.DTOs.CambioDTO;
import es.uca.tfg.ceramic_affair_web.exceptions.RecaptchaException;
import es.uca.tfg.ceramic_affair_web.payload.ApiResponseType;
import es.uca.tfg.ceramic_affair_web.services.AuthService;
import es.uca.tfg.ceramic_affair_web.services.RecaptchaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controlador para gestionar las operaciones relacionadas con los usuarios.
 * 
 * @version 1.0
 */
@RestController
@RequestMapping("/api/user")
@Tag(name = "Usuario", description = "Controlador para la gestión de usuarios")
public class UsuarioController {

    @Autowired
    private AuthService authService;

    @Autowired
    private RecaptchaService recaptchaService;

        @PostMapping("/cambio")
    @Operation(summary = "Cambiar contraseña", description = "Permite a un usuario cambiar su contraseña proporcionando la antigua y la nueva contraseña", tags = { "Auth" })
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Contraseña cambiada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos proporcionados o reCAPTCHA inválido"),
        @ApiResponse(responseCode = "401", description = "Credenciales inválidas"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<ApiResponseType<String>> changePassword(@RequestBody CambioDTO dto) {
        // Validar el reCAPTCHA
        boolean isRecaptchaValid = recaptchaService.verifyRecaptcha(dto.getToken());
        if (!isRecaptchaValid) {
            throw new RecaptchaException.Invalido();
        }

        authService.cambiarContrasena(dto.getEmail(), dto.getAntiguaPassword(), dto.getNuevaPassword());
        return ResponseEntity.ok(new ApiResponseType<>(true, "Password changed successfully", "Password changed successfully"));
    }
}
