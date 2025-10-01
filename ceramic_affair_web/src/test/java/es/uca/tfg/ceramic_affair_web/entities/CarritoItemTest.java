package es.uca.tfg.ceramic_affair_web.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Clase de prueba para la entidad CarritoItem.
 * Proporciona pruebas unitarias para verificar el correcto funcionamiento de los métodos de la clase CarritoItem.
 * 
 * @version 1.0
 */
public class CarritoItemTest {

    @Test
    @DisplayName("CarritoItem - Constructor vacío")
    public void testCarritoItemConstructorVacio() {
        CarritoItem carritoItem = new CarritoItem();

        assertNotNull(carritoItem); // Verifica que la instancia no sea nula
        assertNull(carritoItem.getId()); // Verifica que el ID sea nulo (aún no persiste en la base de datos)
        assertNull(carritoItem.getCarrito()); // Verifica que el carrito sea nulo (no se ha establecido)
        assertNull(carritoItem.getProducto()); // Verifica que el producto sea nulo (no se ha establecido)
        assertNull(carritoItem.getPrecioUnitario()); // Verifica que el precio unitario sea nulo (no se ha establecido)
    }

    @Test
    @DisplayName("CarritoItem - Constructor con parámetros")
    public void testCarritoItemConstructorConParametros() {
        Carrito carrito = new Carrito();
        Producto producto = new Producto();
        producto.setPrecio(new java.math.BigDecimal("19.99"));

        CarritoItem carritoItem = new CarritoItem(carrito, producto);

        assertNotNull(carritoItem); // Verifica que la instancia no sea nula
        assertNull(carritoItem.getId()); // Verifica que el ID sea nulo (aún no persiste en la base de datos)
        assertNotNull(carritoItem.getCarrito()); // Verifica que el carrito no sea nulo
        assertNotNull(carritoItem.getProducto()); // Verifica que el producto no sea nulo
        assertNotNull(carritoItem.getPrecioUnitario()); // Verifica que el precio unitario no sea nulo
        assertEquals(producto.getPrecio(), carritoItem.getPrecioUnitario()); // Verifica que el precio unitario sea el esperado
    }

    @Test
    @DisplayName("CarritoItem - Getters y Setters")
    public void testCarritoItemGettersYSetters() {
        Carrito carrito = new Carrito();
        Producto producto = new Producto();
        producto.setPrecio(new java.math.BigDecimal("29.99"));

        CarritoItem carritoItem = new CarritoItem();
        carritoItem.setId(1L);
        carritoItem.setCarrito(carrito);
        carritoItem.setProducto(producto);
        assertEquals(producto.getPrecio(), carritoItem.getPrecioUnitario()); // Verifica que el precio unitario sea el esperado
        carritoItem.setPrecioUnitario(new BigDecimal("25.99"));

        assertNotNull(carritoItem); // Verifica que la instancia no sea nula
        assertNotNull(carritoItem.getId()); // Verifica que el ID no sea nulo
        assertEquals(1L, carritoItem.getId()); // Verifica que el ID sea el esperado
        assertNotNull(carritoItem.getCarrito()); // Verifica que el carrito no sea nulo
        assertNotNull(carritoItem.getProducto()); // Verifica que el producto no sea nulo
        assertNotNull(carritoItem.getPrecioUnitario()); // Verifica que el precio unitario no sea nulo
        assertEquals(new BigDecimal("25.99"), carritoItem.getPrecioUnitario()); // Verifica que el precio unitario sea el esperado
    }
}
