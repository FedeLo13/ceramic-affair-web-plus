package es.uca.tfg.ceramic_affair_web.services;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import es.uca.tfg.ceramic_affair_web.DTOs.CarritoItemDTO;
import es.uca.tfg.ceramic_affair_web.DTOs.ProductoMapper;
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
 * @version 1.1
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
    @DisplayName("Servicio - Validar carrito de usuario sin carrito")
    public void testValidateCarritoUsuarioSinCarrito() {
        // Crear y guardar un usuario de prueba
        Usuario usuario = new Usuario("usuario1@prueba.com", "password123");
        usuarioRepo.save(usuario);

        // Intentar validar el carrito del usuario
        List<CarritoItemDTO> items = carritoService.validarCarritoUsuario(usuario.getId());
        assertTrue(items.isEmpty());
    }

    @Test
    @DisplayName("Servicio - Validar carrito de usuario con productos desactivados tras su compra")
    public void testValidarCarritoUsuarioConProductoDesactivado() {
        // Crear usuario
        Usuario usuario = new Usuario("usuario_validar3@prueba.com", "pass123");
        usuarioRepo.save(usuario);

        // Crear producto activo
        Producto producto = new Producto("Prod Activo", null, "desc", 10f, 5f, 0f, new BigDecimal("10.00"), true, null);
        producto.setActivo(true);
        producto.setSoldOut(false);
        productoRepo.saveAndFlush(producto);

        // Añadir el producto (válido en este momento)
        carritoService.addProducto(usuario.getId(), producto.getId());

        // Simular que un administrador lo desactiva o elimina (soft delete)
        producto.setActivo(false);
        productoRepo.saveAndFlush(producto);

        // Ejecutar validación del carrito
        List<CarritoItemDTO> invalidos = carritoService.validarCarritoUsuario(usuario.getId());

        // Comprobar que el producto ahora se detecta como inválido
        assertEquals(1, invalidos.size(), "Debe detectarse el producto desactivado");
        assertEquals(producto.getId(), invalidos.get(0).getProducto().getId());
    }

    @Test
    @DisplayName("Servicio - Validar carrito de usuario inexistente")
    public void testValidarCarritoUsuarioNoExistente() {
        assertThatThrownBy(() -> carritoService.validarCarritoUsuario(999L))
                .isInstanceOf(AuthException.UsuarioNoEncontrado.class);
    }

    @Test
    @DisplayName("Servicio - Validar carrito de invitado con items válidos e inválidos")
    public void testValidarCarritoInvitado() {
        Producto p1 = new Producto("Producto OK", null, "desc", 10f, 5f, 0f, new BigDecimal("19.99"), true, null);
        p1.setActivo(true);
        p1.setSoldOut(false);
        productoRepo.save(p1);

        Producto p2 = new Producto("Producto agotado", null, "desc", 10f, 5f, 0f, new BigDecimal("15.99"), true, null);
        p2.setActivo(true);
        p2.setSoldOut(true);
        productoRepo.save(p2);

        List<CarritoItemDTO> items = Arrays.asList(
                new CarritoItemDTO(ProductoMapper.toDTO(p1), p1.getPrecio()),
                new CarritoItemDTO(ProductoMapper.toDTO(p2), p2.getPrecio())
        );

        List<CarritoItemDTO> noDisponibles = carritoService.validarCarritoInvitado(items);

        assertEquals(1, noDisponibles.size());
        assertEquals(p2.getId(), noDisponibles.get(0).getProducto().getId());
    }

    @Test
    @DisplayName("Servicio - Validar carrito de invitado vacío")
    public void testValidarCarritoInvitadoVacio() {
        List<CarritoItemDTO> resultado = carritoService.validarCarritoInvitado(Collections.emptyList());
        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("Servicio - Marcar productos como sold out de carrito de usuario")
    public void testMarcarProductosSoldOutUsuario() {
        Usuario usuario = new Usuario("usuario_soldout@prueba.com", "pass");
        usuarioRepo.save(usuario);

        Producto p1 = new Producto("P1", null, "desc", 10f, 5f, 0f, new BigDecimal("10.00"), true, null);
        p1.setActivo(true);
        p1.setSoldOut(false);
        productoRepo.save(p1);

        carritoService.addProducto(usuario.getId(), p1.getId());
        carritoService.marcarProductosComoSoldOutUsuario(usuario.getId());

        Producto actualizado = productoRepo.findById(p1.getId()).orElseThrow();
        assertTrue(actualizado.isSoldOut(), "El producto debe estar marcado como sold out");
    }

    @Test
    @DisplayName("Servicio - Marcar productos sold out - usuario inexistente")
    public void testMarcarProductosSoldOutUsuarioNoExistente() {
        assertThatThrownBy(() -> carritoService.marcarProductosComoSoldOutUsuario(999L))
                .isInstanceOf(AuthException.UsuarioNoEncontrado.class);
    }

    @Test
    @DisplayName("Servicio - Marcar productos como sold out para invitado")
    public void testMarcarProductosSoldOutInvitado() {
        Producto p1 = new Producto("Invitado P1", null, "desc", 10f, 5f, 0f, new BigDecimal("9.99"), true, null);
        p1.setActivo(true);
        p1.setSoldOut(false);
        productoRepo.save(p1);

        Producto p2 = new Producto("Invitado P2", null, "desc", 10f, 5f, 0f, new BigDecimal("14.99"), true, null);
        p2.setActivo(true);
        p2.setSoldOut(false);
        productoRepo.save(p2);

        List<CarritoItemDTO> items = Arrays.asList(
                new CarritoItemDTO(ProductoMapper.toDTO(p1), p1.getPrecio()),
                new CarritoItemDTO(ProductoMapper.toDTO(p2), p2.getPrecio())
        );

        carritoService.marcarProductosComoSoldOutInvitado(items);

        Producto actualizado1 = productoRepo.findById(p1.getId()).orElseThrow();
        Producto actualizado2 = productoRepo.findById(p2.getId()).orElseThrow();

        assertTrue(actualizado1.isSoldOut());
        assertTrue(actualizado2.isSoldOut());
    }

    @Test
    @DisplayName("Servicio - Marcar productos sold out invitado vacío")
    public void testMarcarProductosSoldOutInvitadoVacio() {
        carritoService.marcarProductosComoSoldOutInvitado(Collections.emptyList());
        // No debe lanzar excepción
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
