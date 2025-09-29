package es.uca.tfg.ceramic_affair_web.entities;

import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import es.uca.tfg.ceramic_affair_web.security.Rol;

/**
 * Clase de prueba para la entidad Usuario.
 * Proporciona pruebas unitarias para los métodos de la clase Usuario.
 * 
 * @version 1.0
 */
public class UsuarioTest {

    @Test
    @DisplayName("Usuario - Constructor vacío")
    public void testConstructorVacio() {
        Usuario usuario = new Usuario();

        assertNotNull(usuario); // Verifica que la instancia no sea nula
        assertNull(usuario.getId()); // Verifica que el ID sea nulo (aún no persiste en la base de datos)
        assertNull(usuario.getEmail()); // Verifica que el email sea nulo (no se ha establecido)
        assertNull(usuario.getPassword()); // Verifica que la contraseña sea nula (no se ha establecido)
        assertNotNull(usuario.getRoles()); // Verifica que los roles no sean nulos
        assertTrue(usuario.getRoles().isEmpty()); // Verifica que los roles estén vacíos
    }

    @Test
    @DisplayName("Usuario - Constructor para usuarios")
    public void testConstructorConParametrosUsuarios() {
        String email = "test@example.com";
        String password = "password";
        Set<Rol> roles = Set.of(Rol.USER); // Asignamos un rol de usuario

        Usuario usuario = new Usuario(email, password, roles);

        assertNotNull(usuario); // Verifica que la instancia no sea nula
        assertNull(usuario.getId()); // Verifica que el ID sea nulo (aún no persiste en la base de datos)
        assertNotNull(usuario.getEmail()); // Verifica que el email no sea nulo
        assertEquals(email, usuario.getEmail()); // Verifica que el email sea el esperado
        assertNotNull(usuario.getPassword()); // Verifica que la contraseña no sea nula
        assertEquals(password, usuario.getPassword()); // Verifica que la contraseña sea la esperada
        assertNotNull(usuario.getRoles()); // Verifica que los roles no sean nulos
        assertFalse(usuario.isVerificado()); // Verifica que verificado sea false por defecto
        assertNotNull(usuario.getTokenVerificacion()); // Verifica que el token de verificación no sea nulo (se ha generado)
        assertNull(usuario.getTokenRecuperacion()); // Verifica que el token de recuperación sea nulo (no se ha generado)
        assertNotNull(usuario.getFechaExpiracionTokenVerificacion()); // Verifica que la fecha de expiración del token de verificación no sea nula
        assertNull(usuario.getFechaExpiracionTokenRecuperacion()); // Verifica que la fecha de expiración del token de recuperación sea nula
        assertEquals(1, usuario.getRoles().size()); // Verifica que se haya establecido un rol
        assertTrue(usuario.getRoles().contains(Rol.USER)); // Verifica que el rol de usuario esté presente
    }

    @Test
    @DisplayName("Usuario - Constructor para administradores")
    public void testConstructorConParametrosAdmin() {
        String email = "admin@example.com";
        String password = "adminpassword";

        Usuario usuario = new Usuario(email, password);

        assertNotNull(usuario); // Verifica que la instancia no sea nula
        assertNull(usuario.getId()); // Verifica que el ID sea nulo (aún no persiste en la base de datos)
        assertNotNull(usuario.getEmail()); // Verifica que el email no sea nulo
        assertEquals(email, usuario.getEmail()); // Verifica que el email sea el esperado
        assertNotNull(usuario.getPassword()); // Verifica que la contraseña no sea nula
        assertEquals(password, usuario.getPassword()); // Verifica que la contraseña sea la esperada
        assertNotNull(usuario.getRoles()); // Verifica que los roles no sean nulos
        assertTrue(usuario.isVerificado()); // Verifica que verificado sea true para administradores
        assertNull(usuario.getTokenVerificacion()); // Verifica que el token de verificación sea nulo (no se ha generado)
        assertNull(usuario.getTokenRecuperacion()); // Verifica que el token de recuperación sea nulo (no se ha generado)
        assertNull(usuario.getFechaExpiracionTokenVerificacion()); // Verifica que la fecha de expiración del token de verificación sea nula
        assertNull(usuario.getFechaExpiracionTokenRecuperacion()); // Verifica que la fecha de expiración del token de recuperación sea nula
        assertEquals(2, usuario.getRoles().size()); // Verifica que se haya establecido un rol
        assertTrue(usuario.getRoles().contains(Rol.ADMIN)); // Verifica que el rol de administrador esté presente
    }

    @Test
    @DisplayName("Usuario - Setters y Getters")
    public void testSettersYGetters() {
        Usuario usuario = new Usuario();

        String email = "test@example.com";
        String password = "password";
        Set<Rol> roles = Set.of(Rol.USER);

        usuario.setEmail(email);
        usuario.setPassword(password);
        usuario.setRoles(roles);
        usuario.setVerificado(true);
        usuario.setTokenVerificacion("verificacionToken");
        usuario.setTokenRecuperacion("recuperacionToken");
        usuario.setFechaExpiracionTokenVerificacion(java.time.LocalDateTime.now().plusHours(1));
        usuario.setFechaExpiracionTokenRecuperacion(java.time.LocalDateTime.now().plusHours(1));
        usuario.setId(1L);

        assertEquals(1L, usuario.getId());
        assertEquals(email, usuario.getEmail());
        assertEquals(password, usuario.getPassword());
        assertEquals(roles, usuario.getRoles());
        assertTrue(usuario.isVerificado());
        assertEquals("verificacionToken", usuario.getTokenVerificacion());
        assertEquals("recuperacionToken", usuario.getTokenRecuperacion());
        assertNotNull(usuario.getFechaExpiracionTokenVerificacion());
        assertNotNull(usuario.getFechaExpiracionTokenRecuperacion());
    }

    @Test
    @DisplayName("Usuario - Verificar usuario")
    public void testVerificarUsuario() {
        String email = "test@example.com";
        String password = "password";
        Set<Rol> roles = Set.of(Rol.USER);

        Usuario usuario = new Usuario(email, password, roles);

        assertFalse(usuario.isVerificado()); // Verifica que verificado sea false por defecto
        assertNotNull(usuario.getTokenVerificacion()); // Verifica que el token de verificación no sea nulo (se ha generado)
        assertNotNull(usuario.getFechaExpiracionTokenVerificacion()); // Verifica que la fecha de expiración del token de verificación no sea nula (se ha generado)

        usuario.verificar();

        assertTrue(usuario.isVerificado()); // Verifica que verificado sea true después de llamar a verificar()
        assertNull(usuario.getTokenVerificacion()); // Verifica que el token de verificación sea nulo después de verificar
        assertNull(usuario.getFechaExpiracionTokenVerificacion()); // Verifica que la fecha de expiración del token de verificación sea nula después de verificar
    }

    @Test
    @DisplayName("Usuario - Generar token de recuperación")
    public void testGenerarTokenRecuperacion() {
        String email = "test@example.com";
        String password = "password";
        Set<Rol> roles = Set.of(Rol.USER);

        Usuario usuario = new Usuario(email, password, roles);
        usuario.verificar(); // Verifica el usuario para asegurarse de que está verificado
        
        assertNull(usuario.getTokenRecuperacion()); // Verifica que el token de recuperación sea nulo (no se ha generado)
        assertNull(usuario.getFechaExpiracionTokenRecuperacion()); // Verifica que la fecha de expiración del token de recuperación sea nula (no se ha generado)

        usuario.generarTokenRecuperacion();

        assertNotNull(usuario.getTokenRecuperacion()); // Verifica que el token de recuperación no sea nulo después de generarlo
        assertNotNull(usuario.getFechaExpiracionTokenRecuperacion()); // Verifica que la fecha de expiración del token de recuperación no sea nula después de generarlo
    }
}
