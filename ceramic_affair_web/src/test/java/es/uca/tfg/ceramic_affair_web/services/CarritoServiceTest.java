package es.uca.tfg.ceramic_affair_web.services;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import es.uca.tfg.ceramic_affair_web.entities.Carrito;
import es.uca.tfg.ceramic_affair_web.entities.Producto;
import es.uca.tfg.ceramic_affair_web.entities.Usuario;
import es.uca.tfg.ceramic_affair_web.exceptions.AuthException;
import es.uca.tfg.ceramic_affair_web.exceptions.CarritoException;
import es.uca.tfg.ceramic_affair_web.exceptions.ProductoException;
import es.uca.tfg.ceramic_affair_web.repositories.ProductoRepo;
import es.uca.tfg.ceramic_affair_web.repositories.UsuarioRepo;
import jakarta.transaction.Transactional;

/**
 * Clase de prueba para el servicio CarritoService.
 * Proporciona pruebas de integración para las operaciones relacionadas con el carrito de compras.
 * 
 * @version 1.0
 */
@SpringBootTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CarritoServiceTest {

    @Autowired
    private CarritoService carritoService;

    @Autowired
    private UsuarioRepo usuarioRepo;

    @Autowired
    private ProductoRepo productoRepo;

    @MockitoBean
    private GmailEmailService gmailEmailService;

    @MockitoBean
    private RecaptchaService recaptchaService;

    @Test
    @DisplayName("Servicio - Obtener carrito de usuario sin carrito")
    public void testGetCarritoCorrecto() {
        // Crear y guardar un usuario de prueba
        Usuario usuario = new Usuario("usuario1@prueba.com", "password123");
        usuarioRepo.save(usuario);

        // Obtener el carrito del usuario
        Optional<Carrito> carritoOpt = carritoService.getCarrito(usuario.getId());

        // Comprobar que el carrito está vacío
        assertTrue(carritoOpt.isEmpty());
    }

    @Test
    @DisplayName("Servicio - Obtener carrito de usuario no existente")
    public void testGetCarritoUsuarioNoExistente() {
        // Intentar obtener el carrito de un usuario que no existe
        assertThatThrownBy(() -> {
            carritoService.getCarrito(999L);
        }).isInstanceOf(AuthException.UsuarioNoEncontrado.class);
    }

    @Test
    @DisplayName("Servicio - Añadir producto a carrito de usuario correctamente")
    public void testAddProductoCorrecto() {
        // Crear y guardar un usuario de prueba
        Usuario usuario = new Usuario("usuario1@prueba.com", "password123");
        usuarioRepo.save(usuario);

        // Crear y guardar un producto de prueba
        Producto producto = new Producto(
            "Producto de prueba",
            null,
            "Descripción del producto de prueba",
            10.0f,
            5.0f,
            0.0f,
            new BigDecimal(19.99),
            false,
            null
        );
        productoRepo.save(producto);

        // Añadir el producto al carrito del usuario
        carritoService.addProducto(usuario.getId(), producto.getId());

        // Obtener el carrito del usuario y comprobar que el producto se ha añadido
        Carrito carrito = carritoService.getCarrito(usuario.getId()).orElse(null);
        assertNotNull(carrito);
        assertNotNull(carrito.getItems());
        assertThat(carrito.getItems()).hasSize(1);
        assertEquals(producto.getId(), carrito.getItems().get(0).getProducto().getId());
        assertEquals(carrito.getTotal(), producto.getPrecio());
    }

    @Test
    @DisplayName("Servicio - Añadir producto a carrito de usuario no existente")
    public void testAddProductoUsuarioNoExistente() {
        // Crear y guardar un producto de prueba
        Producto producto = new Producto(
            "Producto de prueba",
            null,
            "Descripción del producto de prueba",
            10.0f,
            5.0f,
            0.0f,
            new BigDecimal(19.99),
            false,
            null
        );
        productoRepo.save(producto);

        // Intentar añadir el producto al carrito de un usuario que no existe
        assertThatThrownBy(() -> {
            carritoService.addProducto(999L, producto.getId());
        }).isInstanceOf(AuthException.UsuarioNoEncontrado.class);
    }

    @Test
    @DisplayName("Servicio - Añadir producto no existente a carrito de usuario")
    public void testAddProductoNoExistente() {
        // Crear y guardar un usuario de prueba
        Usuario usuario = new Usuario("usuario1@prueba.com", "password123");
        usuarioRepo.save(usuario);

        // Intentar añadir un producto que no existe al carrito del usuario
        assertThatThrownBy(() -> {
            carritoService.addProducto(usuario.getId(), 999L);
        }).isInstanceOf(ProductoException.NoEncontrado.class);
    }

    @Test
    @DisplayName("Servicio - Añadir producto duplicado a carrito de usuario")
    public void testAddProductoDuplicado() {
        // Crear y guardar un usuario de prueba
        Usuario usuario = new Usuario("usuario1@prueba.com", "password123");
        usuarioRepo.save(usuario);

        // Crear y guardar un producto de prueba
        Producto producto = new Producto(
            "Producto de prueba",
            null,
            "Descripción del producto de prueba",
            10.0f,
            5.0f,
            0.0f,
            new BigDecimal(19.99),
            false,
            null
        );
        productoRepo.save(producto);

        // Añadir el producto al carrito del usuario
        carritoService.addProducto(usuario.getId(), producto.getId());

        // Intentar añadir el mismo producto de nuevo al carrito del usuario
        assertThatThrownBy(() -> {
            carritoService.addProducto(usuario.getId(), producto.getId());
        }).isInstanceOf(CarritoException.ItemDuplicado.class);
    }

    @Test
    @DisplayName("Servicio - Añadir producto agotado a carrito de usuario")
    public void testAddProductoAgotado() {
        // Crear y guardar un usuario de prueba
        Usuario usuario = new Usuario("usuario1@prueba.com", "password123");
        usuarioRepo.save(usuario);

        // Crear y guardar un producto de prueba agotado
        Producto producto = new Producto(
            "Producto de prueba",
            null,
            "Descripción del producto de prueba",
            10.0f,
            5.0f,
            0.0f,
            new BigDecimal(19.99),
            false,
            null
        );
        producto.setSoldOut(true);
        productoRepo.save(producto);

        // Intentar añadir el producto agotado al carrito del usuario
        assertThatThrownBy(() -> {
            carritoService.addProducto(usuario.getId(), producto.getId());
        }).isInstanceOf(ProductoException.SoldOut.class);
    }

    @Test
    @DisplayName("Servicio - Eliminar producto de carrito de usuario correctamente")
    public void testRemoveProductoCorrecto() {
        // Crear y guardar un usuario de prueba
        Usuario usuario = new Usuario("usuario1@prueba.com", "password123");
        usuarioRepo.save(usuario);

        // Crear y guardar un par de productos de prueba
        Producto producto1 = new Producto(
            "Producto de prueba 1",
            null,
            "Descripción del producto de prueba 1",
            10.0f,
            5.0f,
            0.0f,
            new BigDecimal(19.99),
            false,
            null
        );
        productoRepo.save(producto1);

        Producto producto2 = new Producto(
            "Producto de prueba 2",
            null,
            "Descripción del producto de prueba 2",
            15.0f,
            7.5f,
            0.0f,
            new BigDecimal(29.99),
            false,
            null
        );
        productoRepo.save(producto2);

        // Añadir ambos productos al carrito del usuario
        carritoService.addProducto(usuario.getId(), producto1.getId());
        carritoService.addProducto(usuario.getId(), producto2.getId());

        // Eliminar uno de los productos del carrito del usuario y comprobar que queda el otro
        Carrito carrito = carritoService.removeProducto(usuario.getId(), producto1.getId()).orElse(null);

        assertNotNull(carrito);
        assertNotNull(carrito.getItems());
        assertThat(carrito.getItems()).hasSize(1);
        assertEquals(producto2.getId(), carrito.getItems().get(0).getProducto().getId());
        assertEquals(carrito.getTotal(), producto2.getPrecio());

        // Eliminar el otro producto del carrito del usuario y comprobar que el carrito se ha eliminado
        Optional<Carrito> carritoVacio = carritoService.removeProducto(usuario.getId(), producto2.getId());
        assertTrue(carritoVacio.isEmpty());
    }

    @Test
    @DisplayName("Servicio - Eliminar producto de carrito de usuario que no tiene carrito")
    public void testRemoveProductoSinCarrito() {
        // Crear y guardar un usuario de prueba
        Usuario usuario = new Usuario("usuario1@prueba.com", "password123");
        usuarioRepo.save(usuario);

        // Intentar eliminar un producto de un carrito que no existe
        Optional<Carrito> carritoVacio = carritoService.removeProducto(usuario.getId(), 999L);
        assertTrue(carritoVacio.isEmpty());
    }

    @Test
    @DisplayName("Servicio - Eliminar producto no existente de carrito de usuario")
    public void testRemoveProductoNoExistente() {
        // Crear y guardar un usuario de prueba
        Usuario usuario = new Usuario("usuario1@prueba.com", "password123");
        usuarioRepo.save(usuario);

        // Crear y guardar un producto de prueba
        Producto producto = new Producto(
            "Producto de prueba",
            null,
            "Descripción del producto de prueba",
            10.0f,
            5.0f,
            0.0f,
            new BigDecimal(19.99),
            false,
            null
        );
        productoRepo.save(producto);

        // Añadir el producto al carrito del usuario
        carritoService.addProducto(usuario.getId(), producto.getId());

        // Intentar eliminar un producto que no existe del carrito del usuario
        Carrito carrito = carritoService.removeProducto(usuario.getId(), 999L).orElse(null);

        assertNotNull(carrito);
        assertNotNull(carrito.getItems());
        assertThat(carrito.getItems()).hasSize(1);
        assertEquals(producto.getId(), carrito.getItems().get(0).getProducto().getId());
    }

    @Test
    @DisplayName("Servicio - Eliminar producto de carrito de usuario no existente")
    public void testRemoveProductoUsuarioNoExistente() {
        // Intentar eliminar un producto del carrito de un usuario que no existe
        assertThatThrownBy(() -> {
            carritoService.removeProducto(999L, 999L);
        }).isInstanceOf(AuthException.UsuarioNoEncontrado.class);
    }

    @Test
    @DisplayName("Servicio - Vaciar carrito de usuario correctamente")
    public void testClearCarritoCorrecto() {
        // Crear y guardar un usuario de prueba
        Usuario usuario = new Usuario("usuario1@prueba.com", "password123");
        usuarioRepo.save(usuario);

        // Crear y guardar un par de productos de prueba
        Producto producto1 = new Producto(
            "Producto de prueba 1",
            null,
            "Descripción del producto de prueba 1",
            10.0f,
            5.0f,
            0.0f,
            new BigDecimal(19.99),
            false,
            null
        );
        productoRepo.save(producto1);

        Producto producto2 = new Producto(
            "Producto de prueba 2",
            null,
            "Descripción del producto de prueba 2",
            15.0f,
            7.5f,
            0.0f,
            new BigDecimal(29.99),
            false,
            null
        );
        productoRepo.save(producto2);

        // Añadir ambos productos al carrito del usuario
        carritoService.addProducto(usuario.getId(), producto1.getId());
        carritoService.addProducto(usuario.getId(), producto2.getId());

        // Vaciar el carrito del usuario
        carritoService.clearCarrito(usuario.getId());

        // Comprobar que el carrito se ha eliminado
        Optional<Carrito> carritoVacio = carritoService.getCarrito(usuario.getId());
        assertTrue(carritoVacio.isEmpty());
    }

    @Test
    @DisplayName("Servicio - Vaciar carrito de usuario no existente")
    public void testClearCarritoUsuarioNoExistente() {
        // Intentar vaciar el carrito de un usuario que no existe
        assertThatThrownBy(() -> {
            carritoService.clearCarrito(999L);
        }).isInstanceOf(AuthException.UsuarioNoEncontrado.class);
    }
}
