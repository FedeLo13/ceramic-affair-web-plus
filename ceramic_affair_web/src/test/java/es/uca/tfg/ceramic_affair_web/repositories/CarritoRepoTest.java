package es.uca.tfg.ceramic_affair_web.repositories;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import es.uca.tfg.ceramic_affair_web.entities.Carrito;
import es.uca.tfg.ceramic_affair_web.entities.Usuario;

/**
 * Clase de prueba para el repositorio CarritoRepo.
 * Proporciona pruebas de integración para las operaciones CRUD en la entidad Carrito.
 * 
 * @version 1.0
 */
@DataJpaTest
public class CarritoRepoTest {

    @Autowired
    private CarritoRepo carritoRepo;

    @Autowired
    private UsuarioRepo usuarioRepo;

    @Test
    @DisplayName("Repositorio - Guardar y cargar carrito")
    void testGuardarYCargarCarrito() {
        // Crear y guardar un nuevo carrito
        Usuario usuario = new Usuario("usuario1@example.com", "password");
        usuarioRepo.save(usuario);
        Carrito carrito = new Carrito(usuario);
        carritoRepo.save(carrito);

        // Cargar el carrito por su ID
        Carrito carritoCargado = carritoRepo.findById(carrito.getId()).orElse(null);

        // Verificar que el carrito cargado no sea nulo y que sus datos sean correctos
        assertNotNull(carritoCargado);
        assertEquals(carrito.getId(), carritoCargado.getId());
        assertEquals(carrito.getUsuario().getEmail(), carritoCargado.getUsuario().getEmail());
        assertEquals(carrito.getTotal(), carritoCargado.getTotal());
    }

    @Test
    @DisplayName("Repositorio - Eliminar carrito")
    void testEliminarCarrito() {
        // Crear y guardar un nuevo carrito
        Usuario usuario = new Usuario("usuario1@example.com", "password");
        usuarioRepo.save(usuario);
        Carrito carrito = new Carrito(usuario);
        carritoRepo.save(carrito);

        // Eliminar el carrito
        carritoRepo.delete(carrito);

        // Verificar que el carrito ya no esté presente en la base de datos
        Carrito carritoEliminado = carritoRepo.findById(carrito.getId()).orElse(null);
        assertNull(carritoEliminado);
    }
}
