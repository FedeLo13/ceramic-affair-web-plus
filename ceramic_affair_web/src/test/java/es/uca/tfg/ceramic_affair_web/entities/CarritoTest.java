package es.uca.tfg.ceramic_affair_web.entities;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Clase de prueba para la entidad Carrito.
 * Proporciona pruebas unitarias para verificar el correcto funcionamiento de los métodos de la clase Carrito.
 * 
 * @version 1.0
 */
public class CarritoTest {

    @Test
    @DisplayName("Carrito - Constructor vacío")
    public void testCarritoConstructorVacio() {
        Carrito carrito = new Carrito();

        assertNotNull(carrito); // Verifica que la instancia no sea nula
        assertNull(carrito.getId()); // Verifica que el ID sea nulo (aún no persiste en la base de datos)
        assertNull(carrito.getUsuario()); // Verifica que el usuario sea nulo (no se ha establecido)
        assertNotNull(carrito.getItems()); // Verifica que la lista de items no sea nula
        assertEquals(0, carrito.getItems().size()); // Verifica que la lista de items esté vacía
        assertEquals(BigDecimal.ZERO, carrito.getTotal()); // Verifica que el total sea cero
        assertNull(carrito.getFechaCreacion()); // Verifica que la fecha de creación sea nula (aún no se ha persistido)
    }

    @Test
    @DisplayName("Carrito - Constructor con parámetros")
    public void testCarritoConstructorConParametros() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);

        Carrito carrito = new Carrito(usuario);

        assertNotNull(carrito); // Verifica que la instancia no sea nula
        assertNull(carrito.getId()); // Verifica que el ID sea nulo (aún no persiste en la base de datos)
        assertNotNull(carrito.getUsuario()); // Verifica que el usuario no sea nulo
        assertEquals(usuario, carrito.getUsuario()); // Verifica que el usuario sea el esperado
        assertNotNull(carrito.getItems()); // Verifica que la lista de items no sea nula
        assertEquals(0, carrito.getItems().size()); // Verifica que la lista de items esté vacía
        assertEquals(BigDecimal.ZERO, carrito.getTotal()); // Verifica que el total sea cero
        assertNull(carrito.getFechaCreacion()); // Verifica que la fecha de creación sea nula (aún no se ha persistido)
    }

    @Test
    @DisplayName("Carrito - Getters y Setters")
    public void testCarritoGettersYSetters() {
        Usuario usuario = new Usuario();
        usuario.setId(2L);

        Carrito carrito = new Carrito();
        carrito.setId(1L);
        carrito.setUsuario(usuario);
        carrito.setTotal(new BigDecimal("99.99"));
        LocalDateTime fechaCreacion = LocalDateTime.now();
        carrito.setFechaCreacion(fechaCreacion);

        assertNotNull(carrito); // Verifica que la instancia no sea nula
        assertNotNull(carrito.getId()); // Verifica que el ID no sea nulo
        assertEquals(1L, carrito.getId()); // Verifica que el ID sea el esperado
        assertNotNull(carrito.getUsuario()); // Verifica que el usuario no sea nulo
        assertEquals(usuario, carrito.getUsuario()); // Verifica que el usuario sea el esperado
        assertNotNull(carrito.getItems()); // Verifica que la lista de items no sea nula
        assertEquals(0, carrito.getItems().size()); // Verifica que la lista de items esté vacía
        assertNotNull(carrito.getTotal()); // Verifica que el total no sea nulo
        assertEquals(new BigDecimal("99.99"), carrito.getTotal()); // Verifica que el total sea el esperado
        assertNotNull(carrito.getFechaCreacion()); // Verifica que la fecha de creación no sea nula
        assertEquals(fechaCreacion, carrito.getFechaCreacion()); // Verifica que la fecha de creación sea la esperada
    }

    @Test
    @DisplayName("Carrito - Añadir y eliminar items")
    public void testCarritoAnadirYEliminarItems() {
        Carrito carrito = new Carrito();
        Producto producto1 = new Producto();
        producto1.setPrecio(new BigDecimal("10.00"));
        Producto producto2 = new Producto();
        producto2.setPrecio(new BigDecimal("20.00"));

        CarritoItem item1 = new CarritoItem(carrito, producto1);
        CarritoItem item2 = new CarritoItem(carrito, producto2);

        // Añadir items
        carrito.addItem(item1);
        carrito.addItem(item2);
        assertEquals(2, carrito.getItems().size()); // Verifica que se hayan añadido los items
        assertEquals(new BigDecimal("30.00"), carrito.getTotal()); // Verifica que el total sea correcto

        // Eliminar un item
        carrito.removeItem(item1);
        assertEquals(1, carrito.getItems().size()); // Verifica que se haya eliminado el item
        assertEquals(new BigDecimal("20.00"), carrito.getTotal()); // Verifica que el total sea correcto
    }
}
