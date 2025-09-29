package es.uca.tfg.ceramic_affair_web.entities;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import es.uca.tfg.ceramic_affair_web.security.Rol;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Id;

/**
 * Clase que representa un usuario en el sistema.
 * 
 * @version 1.0
 */
@Entity
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private boolean verificado;

    @Column(unique = true, nullable = true)
    private String tokenVerificacion;

    @Column(unique = true, nullable = true)
    private String tokenRecuperacion;

    @Column(nullable = true)
    private LocalDateTime fechaExpiracionTokenVerificacion;

    @Column(nullable = true)
    private LocalDateTime fechaExpiracionTokenRecuperacion;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "usuario_roles", joinColumns = @JoinColumn(name = "usuario_id"))
    @Column(name = "rol")
    private Set<Rol> roles = new HashSet<>();

    /**
     * Constructor vacío para JPA.
     */
    public Usuario() {
    }

    /**
     * Constructor con parámetros para crear un administrador.
     * 
     * @param email El correo electrónico del usuario.
     * @param password La contraseña del usuario.
     * @param roles Los roles asignados al usuario.
     */
    public Usuario(String email, String password) {
        this.email = email;
        this.password = password;
        this.roles = new HashSet<>(Set.of(Rol.ADMIN, Rol.USER));
        this.verificado = true;
    }

    /**
     * Constructor con parámetros para crear un usuario.
     * 
     * @param email El correo electrónico del usuario.
     * @param password La contraseña del usuario.
     * @param roles Los roles asignados al usuario.
     */
    public Usuario(String email, String password, Set<Rol> roles) {
        this.email = email;
        this.password = password;
        this.roles = new HashSet<>(roles);
        this.verificado = false;
        generarTokenVerificacion();
    }

    /**
     * Método para obtener el ID del usuario.
     * 
     * @return El ID del usuario.
     */
    public Long getId() {
        return id;
    }

    /**
     * Método para obtener el correo electrónico del usuario.
     * 
     * @return El correo electrónico del usuario.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Método para obtener la contraseña del usuario.
     * 
     * @return La contraseña del usuario.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Método para obtener el estado de verificación del usuario.
     * 
     * @return true si el usuario está verificado, false en caso contrario.
     */
    public boolean isVerificado() {
        return verificado;
    }

    /**
     * Método para obtener el token de verificación del usuario.
     * 
     * @return El token de verificación del usuario.
     */
    public String getTokenVerificacion() {
        return tokenVerificacion;
    }

    /**
     * Método para obtener el token de recuperación del usuario.
     * 
     * @return El token de recuperación del usuario.
     */
    public String getTokenRecuperacion() {
        return tokenRecuperacion;
    }

    /**
     * Método para obtener la fecha de expiración del token de verificación del usuario.
     * 
     * @return La fecha de expiración del token de verificación del usuario.
     */
    public LocalDateTime getFechaExpiracionTokenVerificacion() {
        return fechaExpiracionTokenVerificacion;
    }

    /**
     * Método para obtener la fecha de expiración del token de recuperación del usuario.
     * 
     * @return La fecha de expiración del token de recuperación del usuario.
     */
    public LocalDateTime getFechaExpiracionTokenRecuperacion() {
        return fechaExpiracionTokenRecuperacion;
    }

    /**
     * Método para obtener los roles del usuario.
     * 
     * @return Un conjunto de roles asignados al usuario.
     */
    public Set<Rol> getRoles() {
        return roles;
    }

    /**
     * Método para establecer el ID del usuario.
     * 
     * @param id El nuevo ID del usuario.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Método para establecer el email del usuario.
     * 
     * @param email El nuevo correo electrónico del usuario.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Método para establecer la contraseña del usuario.
     * 
     * @param password La nueva contraseña del usuario.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Método para establecer el estado de verificación del usuario.
     * 
     * @param verificado El nuevo estado de verificación del usuario.
     */
    public void setVerificado(boolean verificado) {
        this.verificado = verificado;
    }

    /**
     * Método para verificar el usuario.
     * 
     */
    public void verificar() {
        this.verificado = true;
        this.tokenVerificacion = null;
        this.fechaExpiracionTokenVerificacion = null;
    }

    /**
     * Método para establecer el token de verificación del usuario.
     * 
     * @param tokenVerificacion El nuevo token de verificación del usuario.
     */
    public void setTokenVerificacion(String tokenVerificacion) {
        this.tokenVerificacion = tokenVerificacion;
    }

    /**
     * Método para establecer el token de recuperación del usuario.
     * 
     * @param tokenRecuperacion El nuevo token de recuperación del usuario.
     */
    public void setTokenRecuperacion(String tokenRecuperacion) {
        this.tokenRecuperacion = tokenRecuperacion;
    }

    /**
     * Método para establecer la fecha de expiración del token de verificación del usuario.
     * 
     * @param fechaExpiracionTokenVerificacion La nueva fecha de expiración del token de verificación del usuario.
     */
    public void setFechaExpiracionTokenVerificacion(LocalDateTime fechaExpiracionTokenVerificacion) {
        this.fechaExpiracionTokenVerificacion = fechaExpiracionTokenVerificacion;
    }

    /**
     * Método para establecer la fecha de expiración del token de recuperación del usuario.
     * 
     * @param fechaExpiracionTokenRecuperacion La nueva fecha de expiración del token de recuperación del usuario.
     */
    public void setFechaExpiracionTokenRecuperacion(LocalDateTime fechaExpiracionTokenRecuperacion) {
        this.fechaExpiracionTokenRecuperacion = fechaExpiracionTokenRecuperacion;
    }

    /**
     * Método para establecer los roles del usuario.
     * 
     * @param roles El nuevo conjunto de roles del usuario.
     */
    public void setRoles(Set<Rol> roles) {
        this.roles = new HashSet<>(roles);
    }

    /**
     * Método para regenerar el token de verificación del usuario.
     */
    public void regenerarTokenVerificacion() {
        generarTokenVerificacion();
    }

    /**
     * Método para generar un nuevo token de recuperación del usuario.
     */
    public void generarTokenRecuperacion() {
        this.tokenRecuperacion = UUID.randomUUID().toString();
        this.fechaExpiracionTokenRecuperacion = LocalDateTime.now().plusHours(1); // Token válido por 1 hora
    }

    private void generarTokenVerificacion() {
        this.tokenVerificacion = UUID.randomUUID().toString();
        this.fechaExpiracionTokenVerificacion = LocalDateTime.now().plusDays(1); // Token válido por 1 día
    }
}
