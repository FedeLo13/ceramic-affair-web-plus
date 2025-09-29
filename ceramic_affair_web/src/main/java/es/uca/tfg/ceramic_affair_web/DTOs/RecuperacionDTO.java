package es.uca.tfg.ceramic_affair_web.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO para la recuperación de contraseña.
 * Este DTO se utiliza para transferir datos de recuperación de contraseña entre la capa de presentación y la capa de servicio.
 * 
 * @version 1.0
 */
public class RecuperacionDTO {

    @NotBlank(message = "El token de recuperación es obligatorio")
    private String token;

    @NotBlank(message = "La nueva contraseña es obligatoria")
    @Size(min = 8, message = "La nueva contraseña debe tener al menos 8 caracteres")
    private String nuevaPassword;

    public RecuperacionDTO() {
        // Constructor por defecto
    }

    public RecuperacionDTO(String token, String nuevaPassword) {
        this.token = token;
        this.nuevaPassword = nuevaPassword;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getNuevaPassword() {
        return nuevaPassword;
    }

    public void setNuevaPassword(String nuevaPassword) {
        this.nuevaPassword = nuevaPassword;
    }
}
