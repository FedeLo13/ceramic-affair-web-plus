package es.uca.tfg.ceramic_affair_web.controllers.common;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import es.uca.tfg.ceramic_affair_web.DTOs.LoginDTO;
import es.uca.tfg.ceramic_affair_web.DTOs.OlvidoDTO;
import es.uca.tfg.ceramic_affair_web.DTOs.RecuperacionDTO;
import es.uca.tfg.ceramic_affair_web.exceptions.RecaptchaException;
import es.uca.tfg.ceramic_affair_web.payload.ApiResponseType;
import es.uca.tfg.ceramic_affair_web.security.Rol;
import es.uca.tfg.ceramic_affair_web.services.AuthService;
import es.uca.tfg.ceramic_affair_web.services.RecaptchaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

/**
 * Controlador para la gestión de autenticación.
 * Este controlador maneja las operaciones de inicio de sesión, registro y recuperación de contraseña.
 * 
 * @version 1.1
 */
@RestController
@RequestMapping("/api/public/auth")
@Tag(name = "Auth", description = "Controlador para la gestión de autenticación")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private RecaptchaService recaptchaService;

    @PostMapping("/login/user")
    @Operation(summary = "Iniciar sesión como usuario", description = "Permite a un usuario iniciar sesión con su email y contraseña", tags = { "Auth" })
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Inicio de sesión exitoso"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos proporcionados, reCAPTCHA inválido, verificación pendiente o usuario no encontrado"),
        @ApiResponse(responseCode = "401", description = "Credenciales inválidas"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<ApiResponseType<Map<String, String>>> userLogin(@Valid @RequestBody LoginDTO loginDTO) {
        // Validar el reCAPTCHA
        boolean isRecaptchaValid = recaptchaService.verifyRecaptcha(loginDTO.getRecaptchaToken());
        if (!isRecaptchaValid) {
            throw new RecaptchaException.Invalido();
        }

        String token = authService.login(loginDTO, Rol.USER);
        return ResponseEntity.ok(new ApiResponseType<>(true, "Login successful", Map.of("token", token)));
    }

    @PostMapping("/login/admin")
    @Operation(summary = "Iniciar sesión como administrador", description = "Permite a un administrador iniciar sesión con su email y contraseña", tags = { "Auth" })
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Inicio de sesión exitoso"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos proporcionados, reCAPTCHA inválido o verificación pendiente"),
        @ApiResponse(responseCode = "401", description = "Credenciales inválidas"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<ApiResponseType<Map<String, String>>> adminLogin(@Valid @RequestBody LoginDTO loginDTO) {
        // Validar el reCAPTCHA
        boolean isRecaptchaValid = recaptchaService.verifyRecaptcha(loginDTO.getRecaptchaToken());
        if (!isRecaptchaValid) {
            throw new RecaptchaException.Invalido();
        }

        String token = authService.login(loginDTO, Rol.ADMIN);
        return ResponseEntity.ok(new ApiResponseType<>(true, "Login successful", Map.of("token", token)));
    }

    @PostMapping("/registro")
    @Operation(summary = "Registrar un nuevo usuario", description = "Permite registrar un nuevo usuario con email y contraseña", tags = { "Auth" })
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Usuario registrado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos proporcionados, reCAPTCHA inválido o verificación pendiente"),
        @ApiResponse(responseCode = "409", description = "Email ya registrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<ApiResponseType<String>> register(@Valid @RequestBody LoginDTO loginDTO) {
        // Validar el reCAPTCHA
        boolean isRecaptchaValid = recaptchaService.verifyRecaptcha(loginDTO.getRecaptchaToken());
        if (!isRecaptchaValid) {
            throw new RecaptchaException.Invalido();
        }

        authService.register(loginDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponseType<>(true, "Verification email has been sent", "Verification email has been sent"));
    }

    @GetMapping("/verificar")
    @Operation(summary = "Verificar un usuario", description = "Verifica un usuario utilizando el token de verificación", tags = { "Auth" })
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuario verificado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
        @ApiResponse(responseCode = "410", description = "Token de verificación expirado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<Void> verifyUser(@RequestParam String token) {
        authService.verify(token);
        return redirectToFrontend("verified");
    }

    @PostMapping("/olvido")
    @Operation(summary = "Solicitar recuperación de contraseña", description = "Permite a un usuario solicitar la recuperación de su contraseña", tags = { "Auth" })
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Correo de recuperación enviado"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos proporcionados o reCAPTCHA inválido"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<ApiResponseType<String>> solicitarRecuperacion(@RequestBody OlvidoDTO dto) {
        // Validar el reCAPTCHA
        boolean isRecaptchaValid = recaptchaService.verifyRecaptcha(dto.getRecaptchaToken());
        if (!isRecaptchaValid) {
            throw new RecaptchaException.Invalido();
        }

        authService.solicitarRecuperacion(dto.getEmail());
        return ResponseEntity.ok(new ApiResponseType<>(true, "Recovery email has been sent", "Recovery email has been sent"));
    }

    @PostMapping("/reset")
    @Operation(summary = "Recuperar contraseña", description = "Permite a un usuario recuperar su contraseña utilizando un token de recuperación", tags = { "Auth" })
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Contraseña recuperada exitosamente"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
        @ApiResponse(responseCode = "410", description = "Token de recuperación expirado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<ApiResponseType<String>> recuperarContraseña(@RequestBody RecuperacionDTO dto) {
        authService.resetPassword(dto.getToken(), dto.getNuevaPassword());
        return ResponseEntity.ok(new ApiResponseType<>(true, "Password has been reset successfully", "Password has been reset successfully"));
    }

    private ResponseEntity<Void> redirectToFrontend(String status) {
        String frontendUrl = "http://localhost:5173/confirmation?status=" + status;
        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", frontendUrl)
                .build();
    }
}
