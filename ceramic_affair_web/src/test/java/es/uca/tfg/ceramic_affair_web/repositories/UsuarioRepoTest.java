package es.uca.tfg.ceramic_affair_web.repositories;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import es.uca.tfg.ceramic_affair_web.entities.Usuario;
import es.uca.tfg.ceramic_affair_web.security.Rol;

/**
 * Clase de prueba para el repositorio UsuarioRepo.
 * Proporciona pruebas de integración para las operaciones CRUD en la entidad Usuario.
 * 
 * @version 1.0
 */
@DataJpaTest
public class UsuarioRepoTest {

    @Autowired
    private UsuarioRepo usuarioRepo;

    @Test
    @DisplayName("Repositorio - Guardar y cargar usuario")
    void testGuardarYCargarUsuario() {
        // Crear y guardar un nuevo usuario
        Usuario usuario = new Usuario("test@example.com", "password", Set.of(Rol.USER));
        usuarioRepo.save(usuario);

        // Cargar el usuario por su ID
        Usuario usuarioCargado = usuarioRepo.findById(usuario.getId()).orElse(null);

        // Verificar que el usuario cargado no sea nulo y que sus datos sean correctos
        assertNotNull(usuarioCargado);
        assertEquals(usuario.getEmail(), usuarioCargado.getEmail());
        assertEquals(usuario.getPassword(), usuarioCargado.getPassword());
        assertEquals(usuario.getRoles(), usuarioCargado.getRoles());
    }

    @Test
    @DisplayName("Repositorio - Eliminar usuario")
    void testEliminarUsuario() {
        // Crear y guardar un nuevo usuario
        Usuario usuario = new Usuario("test@example.com", "password", Set.of(Rol.USER));
        usuarioRepo.save(usuario);

        // Eliminar el usuario
        usuarioRepo.delete(usuario);

        // Verificar que el usuario no existe más
        assertFalse(usuarioRepo.findById(usuario.getId()).isPresent());
    }

    @Test
    @DisplayName("Repositorio - Usuario existente por email")
    void testUsuarioExistentePorEmail() {
        // Crear y guardar un nuevo usuario
        Usuario usuario = new Usuario("test@example.com", "password", Set.of(Rol.USER));
        usuarioRepo.save(usuario);

        // Buscar el usuario por su email
        Optional<Usuario> usuarioEncontrado = usuarioRepo.findByEmail("test@example.com");

        // Verificar que el usuario se ha encontrado
        assertTrue(usuarioEncontrado.isPresent());
        assertEquals(usuario.getEmail(), usuarioEncontrado.get().getEmail());
        assertEquals(usuario.getPassword(), usuarioEncontrado.get().getPassword());
        assertEquals(usuario.getRoles(), usuarioEncontrado.get().getRoles());
    }

    @Test
    @DisplayName("Repositorio - Usuario no existente por email")
    void testUsuarioNoExistentePorEmail() {
        // Buscar un usuario por un email que no existe
        Optional<Usuario> usuarioEncontrado = usuarioRepo.findByEmail("noexiste@example.com");

        // Verificar que el usuario no se ha encontrado
        assertFalse(usuarioEncontrado.isPresent());
    }

    @Test
    @DisplayName("Repositorio - Verificar existencia de usuario por email")
    void testExistsByEmail() {
        // Crear y guardar un nuevo usuario
        Usuario usuario = new Usuario("test@example.com", "password", Set.of(Rol.USER));
        usuarioRepo.save(usuario);

        // Verificar que el usuario existe por su email
        assertTrue(usuarioRepo.existsByEmail("test@example.com"));
    }

    @Test
    @DisplayName("Repositorio - Verificar inexistencia de usuario por email")
    void testExistsByEmailNoExistente() {
        // Verificar que el usuario no existe por su email
        assertFalse(usuarioRepo.existsByEmail("noexiste@example.com"));
    }

    @Test
    @DisplayName("Repositorio - Buscar usuario por token de verificación")
    void testFindByTokenVerificacion() {
        // Crear y guardar un nuevo usuario con token de verificación
        Usuario usuario = new Usuario("test@example.com", "password", Set.of(Rol.USER));
        usuario.setTokenVerificacion("verificacionToken");
        usuarioRepo.save(usuario);

        // Buscar el usuario por su token de verificación
        Optional<Usuario> usuarioEncontrado = usuarioRepo.findByTokenVerificacion("verificacionToken");

        // Verificar que el usuario se ha encontrado
        assertTrue(usuarioEncontrado.isPresent());
        assertEquals(usuario.getEmail(), usuarioEncontrado.get().getEmail());
        assertEquals(usuario.getPassword(), usuarioEncontrado.get().getPassword());
        assertEquals(usuario.getRoles(), usuarioEncontrado.get().getRoles());
    }

    @Test
    @DisplayName("Repositorio - Buscar usuario por token de recuperación")
    void testFindByTokenRecuperacion() {
        // Crear y guardar un nuevo usuario con token de recuperación
        Usuario usuario = new Usuario("test@example.com", "password", Set.of(Rol.USER));
        usuario.setTokenRecuperacion("recuperacionToken");
        usuarioRepo.save(usuario);

        // Buscar el usuario por su token de recuperación
        Optional<Usuario> usuarioEncontrado = usuarioRepo.findByTokenRecuperacion("recuperacionToken");

        // Verificar que el usuario se ha encontrado
        assertTrue(usuarioEncontrado.isPresent());
        assertEquals(usuario.getEmail(), usuarioEncontrado.get().getEmail());
        assertEquals(usuario.getPassword(), usuarioEncontrado.get().getPassword());
        assertEquals(usuario.getRoles(), usuarioEncontrado.get().getRoles());
    }

    @Test
    @DisplayName("Repositorio - Eliminar usuarios no verificados y con token expirado")
    void testDeleteByVerificadoFalseAndFechaExpiracionTokenVerificacionBefore() {
        // Crear y guardar un usuario no verificado con token expirado
        Usuario usuarioNoVerificado = new Usuario("test@example.com", "password", Set.of(Rol.USER));
        usuarioNoVerificado.setVerificado(false);
        usuarioNoVerificado.setFechaExpiracionTokenVerificacion(LocalDateTime.now().minusDays(2)); // Token expirado
        usuarioRepo.save(usuarioNoVerificado);

        // Eliminar usuarios no verificados y con token expirado
        usuarioRepo.deleteByVerificadoFalseAndFechaExpiracionTokenVerificacionBefore(LocalDateTime.now());

        // Verificar que el usuario ha sido eliminado
        Optional<Usuario> usuarioEncontrado = usuarioRepo.findByEmail("test@example.com");
        assertFalse(usuarioEncontrado.isPresent());
    }
}
