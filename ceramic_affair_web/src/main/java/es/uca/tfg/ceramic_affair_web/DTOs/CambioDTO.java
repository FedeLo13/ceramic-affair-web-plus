package es.uca.tfg.ceramic_affair_web.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO para el cambio de contraseña.
 * Este DTO se utiliza para transferir datos relacionados con el cambio de contraseña entre la capa de presentación y la capa de servicio.
 * 
 * @version 1.0
 */
public class CambioDTO {

    @NotBlank(message = "El email es obligatorio")
    private String email;

    @NotBlank(message = "El token es obligatorio")
    private String token;

    @NotBlank(message = "La contraseña actual es obligatoria")
    @Size(min = 8, message = "La contraseña actual debe tener al menos 8 caracteres")
    private String antiguaPassword;

    @NotBlank(message = "La nueva contraseña es obligatoria")
    @Size(min = 8, message = "La nueva contraseña debe tener al menos 8 caracteres")
    private String nuevaPassword;

    // Getters y Setters

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getAntiguaPassword() {
        return antiguaPassword;
    }

    public void setAntiguaPassword(String antiguaPassword) {
        this.antiguaPassword = antiguaPassword;
    }

    public String getNuevaPassword() {
        return nuevaPassword;
    }

    public void setNuevaPassword(String nuevaPassword) {
        this.nuevaPassword = nuevaPassword;
    }
}
