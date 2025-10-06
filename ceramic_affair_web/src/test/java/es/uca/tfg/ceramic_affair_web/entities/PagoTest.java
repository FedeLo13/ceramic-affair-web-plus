package es.uca.tfg.ceramic_affair_web.entities;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Clase de prueba para la entidad Pago.
 * Proporciona pruebas unitarias para los métodos de la clase Pago.
 * 
 * @version 1.0
 */
public class PagoTest {

    @Test
    @DisplayName("Pago - Constructor vacío")
    public void testConstructorVacio() {
        Pago pago = new Pago();

        assertNotNull(pago); // Verifica que la instancia no sea nula
        assertNull(pago.getId()); // Verifica que el ID sea nulo (aún no persiste en la base de datos)
        assertNull(pago.getUsuario()); // Verifica que el usuario sea nulo (no se ha establecido)
        assertNull(pago.getPedido()); // Verifica que el pedido sea nulo (no se ha establecido)
        assertEquals(BigDecimal.ZERO, pago.getImporte()); // Verifica que el importe sea cero por defecto
        assertNull(pago.getFechaCreacion()); // Verifica que la fecha de creación sea nula (aún no persiste en la base de datos)
        assertNull(pago.getTipoPago()); // Verifica que el tipo de pago sea nulo (no se ha establecido)
    }

    @Test
    @DisplayName("Pago - Constructor con parámetros")
    public void testConstructorConParametros() {
        Usuario usuario = new Usuario(); 
        Pedido pedido = new Pedido(); 
        BigDecimal importe = new BigDecimal("100.50");
        TipoPago tipoPago = TipoPago.BIZUM; // Suponiendo que tienes un enum TipoPago

        Pago pago = new Pago(usuario, pedido, importe, tipoPago);

        assertNotNull(pago); // Verifica que la instancia no sea nula
        assertNull(pago.getId()); // Verifica que el ID sea nulo (aún no persiste en la base de datos)
        assertEquals(usuario, pago.getUsuario()); // Verifica que el usuario se haya establecido correctamente
        assertEquals(pedido, pago.getPedido()); // Verifica que el pedido se haya establecido correctamente
        assertEquals(importe, pago.getImporte()); // Verifica que el importe se haya establecido correctamente
        assertNull(pago.getFechaCreacion()); // Verifica que la fecha de creación sea nula (aún no persiste en la base de datos)
        assertEquals(tipoPago, pago.getTipoPago()); // Verifica que el tipo de pago se haya establecido correctamente
    }

    @Test
    @DisplayName("Pago - Getters y Setters")
    public void testGettersYSetters() {
        Pago pago = new Pago();
        Usuario usuario = new Usuario(); 
        Pedido pedido = new Pedido(); 
        BigDecimal importe = new BigDecimal("200.75");
        TipoPago tipoPago = TipoPago.TARJETA; // Suponiendo que tienes un enum TipoPago

        pago.setUsuario(usuario);
        pago.setPedido(pedido);
        pago.setImporte(importe);
        pago.setTipoPago(tipoPago);

        assertEquals(null, pago.getId()); // Verifica que el ID sea nulo (aún no persiste en la base de datos)
        assertEquals(usuario, pago.getUsuario()); // Verifica que el usuario se haya establecido correctamente
        assertEquals(pedido, pago.getPedido()); // Verifica que el pedido se haya establecido correctamente
        assertEquals(importe, pago.getImporte()); // Verifica que el importe se haya establecido correctamente
        assertNull(pago.getFechaCreacion()); // Verifica que la fecha de creación sea nula (aún no persiste en la base de datos)
        assertEquals(tipoPago, pago.getTipoPago()); // Verifica que el tipo de pago se haya establecido correctamente
    }
}
