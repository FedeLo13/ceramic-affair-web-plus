package es.uca.tfg.ceramic_affair_web.DTOs;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO para la solicitud de recuperación de contraseña.
 * Este DTO se utiliza para transferir datos de solicitud de recuperación entre la capa de presentación y la capa de servicio.
 * 
 * @version 1.0
 */
public class OlvidoDTO {

    @NotBlank(message = "El email es obligatorio")
    private String email;

    @NotBlank(message = "El token de reCAPTCHA es obligatorio")
    private String recaptchaToken;

    public OlvidoDTO() {
        // Constructor por defecto
    }

    public OlvidoDTO(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRecaptchaToken() {
        return recaptchaToken;
    }

    public void setRecaptchaToken(String recaptchaToken) {
        this.recaptchaToken = recaptchaToken;
    }
}
