package es.uca.tfg.ceramic_affair_web.entities;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Clase de prueba para la entidad PedidoItems.
 * Proporciona pruebas unitarias para los métodos de la clase PedidoItems.
 * 
 * @version 1.0
 */
public class PedidoItemTest {

    @Test
    @DisplayName("PedidoItem - Constructor vacío")
    public void testConstructorVacio() {
        PedidoItem pedidoItems = new PedidoItem();

        assertNotNull(pedidoItems); // Verifica que la instancia no sea nula
        assertNull(pedidoItems.getId()); // Verifica que el ID sea nulo (aún no persiste en la base de datos)
        assertNull(pedidoItems.getProducto()); // Verifica que el producto sea nulo (no se ha establecido)
        assertNull(pedidoItems.getPedido()); // Verifica que el pedido sea nulo (no se ha establecido)
        assertEquals(BigDecimal.valueOf(0.0), pedidoItems.getPrecioUnitario()); // Verifica que el precio unitario sea 0.0 (valor por defecto)
    }

    @Test
    @DisplayName("PedidoItem - Constructor con parámetros")
    public void testConstructorConParametros() {
        Pedido pedido = new Pedido();
        Producto producto = new Producto();
        producto.setPrecio(BigDecimal.valueOf(19.99));
        PedidoItem pedidoItems = new PedidoItem(pedido, producto);

        assertNotNull(pedidoItems); // Verifica que la instancia no sea nula
        assertNull(pedidoItems.getId()); // Verifica que el ID sea nulo (aún no persiste en la base de datos)
        assertEquals(producto, pedidoItems.getProducto()); // Verifica que el producto se haya establecido correctamente
        assertEquals(pedido, pedidoItems.getPedido()); // Verifica que el pedido se haya establecido correctamente
        assertEquals(BigDecimal.valueOf(19.99), pedidoItems.getPrecioUnitario()); // Verifica que el precio unitario se haya establecido correctamente
    }

    @Test
    @DisplayName("PedidoItem - Getters y Setters")
    public void testGettersYSetters() {
        PedidoItem pedidoItems = new PedidoItem();
        Pedido pedido = new Pedido();
        Producto producto = new Producto();
        producto.setPrecio(BigDecimal.valueOf(29.99));

        pedidoItems.setPedido(pedido);
        pedidoItems.setProducto(producto);
        pedidoItems.setPrecioUnitario(BigDecimal.valueOf(29.99));

        assertEquals(null, pedidoItems.getId()); // Verifica que el ID sea nulo (aún no persiste en la base de datos)
        assertEquals(pedido, pedidoItems.getPedido()); // Verifica que el pedido se haya establecido correctamente
        assertEquals(producto, pedidoItems.getProducto()); // Verifica que el producto se haya establecido correctamente
        assertEquals(BigDecimal.valueOf(29.99), pedidoItems.getPrecioUnitario()); // Verifica que el precio unitario se haya establecido correctamente
    }
}
