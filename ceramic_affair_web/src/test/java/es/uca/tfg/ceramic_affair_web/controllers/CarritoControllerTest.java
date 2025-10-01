package es.uca.tfg.ceramic_affair_web.controllers;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import es.uca.tfg.ceramic_affair_web.controllers.user.CarritoController;
import es.uca.tfg.ceramic_affair_web.entities.Carrito;
import es.uca.tfg.ceramic_affair_web.entities.CarritoItem;
import es.uca.tfg.ceramic_affair_web.entities.Producto;
import es.uca.tfg.ceramic_affair_web.exceptions.AuthException;
import es.uca.tfg.ceramic_affair_web.exceptions.CarritoException;
import es.uca.tfg.ceramic_affair_web.exceptions.ProductoException;
import es.uca.tfg.ceramic_affair_web.security.JwtAuthFilter;
import es.uca.tfg.ceramic_affair_web.security.JwtUtils;
import es.uca.tfg.ceramic_affair_web.services.CarritoService;

/**
 * Clase de prueba para el controlador CarritoController.
 * Proporciona pruebas de capa web para las operaciones relacionadas con el carrito de compras.
 * 
 * @version 1.0
 */
@WebMvcTest(controllers = CarritoController.class)
@AutoConfigureMockMvc(addFilters = false) // Desactiva los filtros de seguridad para las pruebas
public class CarritoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CarritoService carritoService;

    @MockitoBean
    private JwtUtils jwtUtils;

    @MockitoBean
    private JwtAuthFilter jwtAuthFilter;

    @Test
    @DisplayName("Controlador - Obtener carrito con productos")
    void testObtenerCarritoConProductos() throws Exception {
        // Simular un carrito con un item
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

        CarritoItem item = new CarritoItem();
        item.setProducto(producto);
        item.setPrecioUnitario(producto.getPrecio());

        Carrito carrito = new Carrito();
        carrito.setId(1L);
        carrito.addItem(item);

        when(carritoService.getCarrito(1L)).thenReturn(Optional.of(carrito));

        // Realizar la solicitud GET al endpoint del carrito
        mockMvc.perform(get("/api/user/carrito/obtener")
                .param("usuarioId", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Carrito obtenido exitosamente"))
            .andExpect(jsonPath("$.data.total").value(19.99))
            .andExpect(jsonPath("$.data.items[0].producto.nombre").value("Producto de prueba"))
            .andExpect(jsonPath("$.data.items[0].precioUnitario").value(19.99));
    }

    @Test
    @DisplayName("Controlador - Obtener carrito vacío")
    void testObtenerCarritoVacio() throws Exception {
        // Simular un carrito vacío
        when(carritoService.getCarrito(1L)).thenReturn(Optional.empty());

        // Realizar la solicitud GET al endpoint del carrito
        mockMvc.perform(get("/api/user/carrito/obtener")
                .param("usuarioId", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Carrito obtenido exitosamente"))
            .andExpect(jsonPath("$.data.total").value(0.0))
            .andExpect(jsonPath("$.data.items").isEmpty());
    }

    @Test
    @DisplayName("Controlador - Obtener carrito con usuario no encontrado (excepción)")
    void testObtenerCarritoUsuarioNoEncontrado() throws Exception {
        // Simular una excepción al obtener el carrito
        when(carritoService.getCarrito(999L)).thenThrow(new AuthException.UsuarioNoEncontrado());

        // Realizar la solicitud GET al endpoint del carrito
        mockMvc.perform(get("/api/user/carrito/obtener")
                .param("usuarioId", "999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.error").value("Business exception"))
            .andExpect(jsonPath("$.message").value("User not found"))
            .andExpect(jsonPath("$.path").value("/api/user/carrito/obtener"));
    }

    @Test
    @DisplayName("Controlador - Agregar producto al carrito exitosamente")
    void testAgregarProductoAlCarritoExitosamente() throws Exception {
        // Simular un producto
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

        CarritoItem item = new CarritoItem();
        item.setProducto(producto);
        item.setPrecioUnitario(producto.getPrecio());

        Carrito carrito = new Carrito();
        carrito.setId(1L);
        carrito.addItem(item);

        when(carritoService.addProducto(1L, 1L)).thenReturn(carrito);

        // Realizar la solicitud POST al endpoint para agregar un producto
        mockMvc.perform(post("/api/user/carrito/agregar")
                .param("usuarioId", "1")
                .param("productoId", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Producto agregado exitosamente"))
            .andExpect(jsonPath("$.data.total").value(19.99))
            .andExpect(jsonPath("$.data.items[0].producto.nombre").value("Producto de prueba"))
            .andExpect(jsonPath("$.data.items[0].precioUnitario").value(19.99));
    }

    @Test
    @DisplayName("Controlador - Agregar producto al carrito con usuario no encontrado (excepción)")
    void testAgregarProductoAlCarritoUsuarioNoEncontrado() throws Exception {
        // Simular una excepción al agregar un producto
        when(carritoService.addProducto(999L, 1L)).thenThrow(new AuthException.UsuarioNoEncontrado());

        // Realizar la solicitud POST al endpoint para agregar un producto
        mockMvc.perform(post("/api/user/carrito/agregar")
                .param("usuarioId", "999")
                .param("productoId", "1"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.error").value("Business exception"))
            .andExpect(jsonPath("$.message").value("User not found"))
            .andExpect(jsonPath("$.path").value("/api/user/carrito/agregar"));
    }

    @Test
    @DisplayName("Controlador - Agregar producto al carrito con producto no encontrado (excepción)")
    void testAgregarProductoAlCarritoProductoNoEncontrado() throws Exception {
        // Simular una excepción al agregar un producto
        when(carritoService.addProducto(1L, 999L)).thenThrow(new ProductoException.NoEncontrado(999L));

        // Realizar la solicitud POST al endpoint para agregar un producto
        mockMvc.perform(post("/api/user/carrito/agregar")
                .param("usuarioId", "1")
                .param("productoId", "999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.error").value("Business exception"))
            .andExpect(jsonPath("$.message").value("Product not found with ID 999"))
            .andExpect(jsonPath("$.path").value("/api/user/carrito/agregar"));
    }

    @Test
    @DisplayName("Controlador - Agregar producto al carrito con producto no disponible (excepción)")
    void testAgregarProductoAlCarritoProductoNoDisponible() throws Exception {
        when(carritoService.addProducto(1L, 1L)).thenThrow(new ProductoException.SoldOut(1L));

        // Realizar la solicitud POST al endpoint para agregar un producto
        mockMvc.perform(post("/api/user/carrito/agregar")
                .param("usuarioId", "1")
                .param("productoId", "1"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.error").value("Business exception"))
            .andExpect(jsonPath("$.message").value("Product with ID 1 is sold out"))
            .andExpect(jsonPath("$.path").value("/api/user/carrito/agregar"));
    }

    @Test
    @DisplayName("Controlador - Agregar producto al carrito que ya está en el carrito (excepción)")
    void testAgregarProductoAlCarritoYaEnCarrito() throws Exception {
        when(carritoService.addProducto(1L, 1L)).thenThrow(new CarritoException.ItemDuplicado());

        // Realizar la solicitud POST al endpoint para agregar un producto
        mockMvc.perform(post("/api/user/carrito/agregar")
                .param("usuarioId", "1")
                .param("productoId", "1"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.error").value("Business exception"))
            .andExpect(jsonPath("$.message").value("Product already in cart"))
            .andExpect(jsonPath("$.path").value("/api/user/carrito/agregar"));
    }

    @Test
    @DisplayName("Controlador - Eliminar producto del carrito correctamente")
    void testEliminarProductoDelCarritoCorrectamente() throws Exception {
        // Simular carrito con 1 item tras eliminar un producto
        Carrito carrito = new Carrito();
        carrito.addItem(
            new CarritoItem(
                carrito,
                new Producto(
                    "Producto de prueba",
                    null,
                    "Descripción del producto de prueba",
                    10.0f,
                    5.0f,
                    0.0f,
                    new BigDecimal(19.99),
                    false,
                    null
                )
            )
        );

        when(carritoService.removeProducto(1L, 1L)).thenReturn(Optional.of(carrito));

        // Realizar la solicitud DELETE al endpoint para eliminar un producto
        mockMvc.perform(delete("/api/user/carrito/eliminar")
                .param("usuarioId", "1")
                .param("productoId", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Producto eliminado exitosamente"))
            .andExpect(jsonPath("$.data.total").value(19.99))
            .andExpect(jsonPath("$.data.items[0].producto.nombre").value("Producto de prueba"))
            .andExpect(jsonPath("$.data.items[0].precioUnitario").value(19.99));
    }

    @Test
    @DisplayName("Controlador - Carrito vacío tras eliminar producto")
    void testCarritoVacioTrasEliminarProducto() throws Exception {
        // Simular carrito vacío tras eliminar un producto
        when(carritoService.removeProducto(1L, 1L)).thenReturn(Optional.empty());

        // Realizar la solicitud DELETE al endpoint para eliminar un producto
        mockMvc.perform(delete("/api/user/carrito/eliminar")
                .param("usuarioId", "1")
                .param("productoId", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Producto eliminado exitosamente"))
            .andExpect(jsonPath("$.data.total").value(0.0))
            .andExpect(jsonPath("$.data.items").isEmpty());
    }

    @Test
    @DisplayName("Controlador - Eliminar producto del carrito con usuario no encontrado (excepción)")
    void testEliminarProductoDelCarritoUsuarioNoEncontrado() throws Exception {
        // Simular una excepción al eliminar un producto
        when(carritoService.removeProducto(999L, 1L)).thenThrow(new AuthException.UsuarioNoEncontrado());

        // Realizar la solicitud DELETE al endpoint para eliminar un producto
        mockMvc.perform(delete("/api/user/carrito/eliminar")
                .param("usuarioId", "999")
                .param("productoId", "1"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.error").value("Business exception"))
            .andExpect(jsonPath("$.message").value("User not found"))
            .andExpect(jsonPath("$.path").value("/api/user/carrito/eliminar"));
    }

    @Test
    @DisplayName("Controlador - Vaciar carrito correctamente")
    void testVaciarCarritoCorrectamente() throws Exception {
        // En este caso, no se devuelve nada, solo se verifica que el servicio fue llamado correctamente
        doNothing().when(carritoService).clearCarrito(1L);

        // Realizar la solicitud DELETE al endpoint para vaciar el carrito
        mockMvc.perform(delete("/api/user/carrito/vaciar")
                .param("usuarioId", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Carrito vaciado exitosamente"))
            .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    @DisplayName("Controlador - Vaciar carrito con usuario no encontrado (excepción)")
    void testVaciarCarritoUsuarioNoEncontrado() throws Exception {
        // Simular una excepción al vaciar el carrito
        doThrow(new AuthException.UsuarioNoEncontrado()).when(carritoService).clearCarrito(999L);

        // Realizar la solicitud DELETE al endpoint para vaciar el carrito
        mockMvc.perform(delete("/api/user/carrito/vaciar")
                .param("usuarioId", "999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.error").value("Business exception"))
            .andExpect(jsonPath("$.message").value("User not found"))
            .andExpect(jsonPath("$.path").value("/api/user/carrito/vaciar"));
    }
}
