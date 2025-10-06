package es.uca.tfg.ceramic_affair_web.entities;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Clase de prueba para la entidad Pedido.
 * Proporciona pruebas unitarias para los métodos de la clase Pedido.
 * 
 * @version 1.0
 */
public class PedidoTest {

    @Test
    @DisplayName("Pedido - Constructor vacío")
    public void testConstructorVacio() {
        Pedido pedido = new Pedido();

        assertNotNull(pedido); // Verifica que la instancia no sea nula
        assertNull(pedido.getId()); // Verifica que el ID sea nulo (aún no persiste en la base de datos)
        assertNull(pedido.getUsuario()); // Verifica que el usuario sea nulo (no se ha establecido)
        assertNotNull(pedido.getItems()); // Verifica que la lista de items no sea nula
        assertTrue(pedido.getItems().isEmpty()); // Verifica que la lista de items esté vacía
        assertEquals(BigDecimal.ZERO, pedido.getTotal()); // Verifica que el total sea 0.0 (valor por defecto)
        assertNull(pedido.getFechaCreacion()); // Verifica que la fecha de creación sea nula (aún no persiste en la base de datos)
        assertNull(pedido.getNombreCliente()); // Verifica que el nombre del cliente sea nulo (no se ha establecido)
        assertNull(pedido.getApellidosCliente()); // Verifica que los apellidos del cliente sean nulos (no se ha establecido)
        assertNull(pedido.getEmailCliente()); // Verifica que el email del cliente sea nulo (no se ha establecido)
        assertNull(pedido.getProvinciaEnvio()); // Verifica que la provincia de envío sea nula (no se ha establecido)
        assertNull(pedido.getCiudadEnvio()); // Verifica que la ciudad de envío sea nula (no se ha establecido)
        assertNull(pedido.getCodigoPostalEnvio()); // Verifica que el código postal de envío sea nulo (no se ha establecido)
        assertNull(pedido.getDireccionEnvio()); // Verifica que la dirección de envío sea nula (no se ha establecido)
        assertFalse(pedido.isEnviado());
    }

    @Test
    @DisplayName("Pedido - Constructor con parámetros para usuarios registrados")
    public void testConstructorConParametrosUsuariosRegistrados() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("juan@example.com");

        Carrito carrito = new Carrito(usuario);

        Producto producto1 = new Producto();
        producto1.setNombre("Taza cerámica");
        producto1.setPrecio(new BigDecimal("12.50"));

        Producto producto2 = new Producto();
        producto2.setNombre("Plato decorativo");
        producto2.setPrecio(new BigDecimal("20.00"));

        carrito.addItem(new CarritoItem(carrito, producto1));
        carrito.addItem(new CarritoItem(carrito, producto2));

        Pedido pedido = new Pedido(
        carrito,
        "Juan",
        "Pérez",
        "juan@example.com",
        "Sevilla",
        "Dos Hermanas",
        "41089",
        "Calle Falsa 123"
    );

        assertNotNull(pedido); // Verifica que la instancia no sea nula
        assertNull(pedido.getId()); // Verifica que el ID sea nulo (aún no persiste en la base de datos)
        assertEquals(usuario, pedido.getUsuario()); // Verifica que el usuario se haya establecido correctamente
        assertEquals(2, pedido.getItems().size()); // Verifica que la lista de items tenga 2 elementos
        assertEquals(new BigDecimal("32.50"), pedido.getTotal()); // Verifica que el total se haya calculado correctamente
        assertNull(pedido.getFechaCreacion()); // Verifica que la fecha de creación sea nula (aún no persiste en la base de datos)
        assertEquals("Juan", pedido.getNombreCliente()); // Verifica que el nombre del cliente se haya establecido correctamente
        assertEquals("Pérez", pedido.getApellidosCliente()); // Verifica que los apellidos del cliente se hayan establecido correctamente
        assertEquals("juan@example.com", pedido.getEmailCliente()); // Verifica que el email del cliente se haya establecido correctamente
        assertEquals("Sevilla", pedido.getProvinciaEnvio()); // Verifica que la provincia de envío se haya establecido correctamente
        assertEquals("Dos Hermanas", pedido.getCiudadEnvio()); // Verifica que la ciudad de envío se haya establecido correctamente
        assertEquals("41089", pedido.getCodigoPostalEnvio()); // Verifica que el código postal de envío se haya establecido correctamente
        assertEquals("Calle Falsa 123", pedido.getDireccionEnvio()); // Verifica que la dirección de envío se haya establecido correctamente
        assertFalse(pedido.isEnviado());
    }

    @Test
    @DisplayName("Pedido - Constructor con parámetros para usuarios no registrados")
    public void testConstructorConParametrosUsuariosNoRegistrados() {
        Pedido pedido = new Pedido(
            "María",
            "Gómez",
            "maria@example.com",
            "Cádiz",
            "Cádiz",
            "11001",
            "Avenida Siempre Viva 456"
        );

        assertNotNull(pedido); // Verifica que la instancia no sea nula
        assertNull(pedido.getId()); // Verifica que el ID sea nulo (aún no persiste en la base de datos)
        assertNull(pedido.getUsuario()); // Verifica que el usuario sea nulo (no se ha establecido)
        assertNotNull(pedido.getItems()); // Verifica que la lista de items no sea nula
        assertTrue(pedido.getItems().isEmpty()); // Verifica que la lista de items esté vacía
        assertEquals(BigDecimal.ZERO, pedido.getTotal()); // Verifica que el total sea 0.0 (valor por defecto)
        assertNull(pedido.getFechaCreacion()); // Verifica que la fecha de creación sea nula (aún no persiste en la base de datos)
        assertEquals("María", pedido.getNombreCliente()); // Verifica que el nombre del cliente se haya establecido correctamente
        assertEquals("Gómez", pedido.getApellidosCliente()); // Verifica que los apellidos del cliente se hayan establecido correctamente
        assertEquals("maria@example.com", pedido.getEmailCliente()); // Verifica que el email del cliente se haya establecido correctamente
        assertEquals("Cádiz", pedido.getProvinciaEnvio()); // Verifica que la provincia de envío se haya establecido correctamente
        assertEquals("Cádiz", pedido.getCiudadEnvio()); // Verifica que la ciudad de envío se haya establecido correctamente
        assertEquals("11001", pedido.getCodigoPostalEnvio()); // Verifica que el código postal de envío se haya establecido correctamente
        assertEquals("Avenida Siempre Viva 456", pedido.getDireccionEnvio()); // Verifica que la dirección de envío se haya establecido correctamente
        assertFalse(pedido.isEnviado());
    }

    @Test
    @DisplayName("Pedido - Getters y Setters")
    public void testGettersYSetters() {
        Pedido pedido = new Pedido();

        Usuario usuario = new Usuario();
        usuario.setId(2L);
        usuario.setEmail("maria@example.com");
        pedido.setUsuario(usuario);
        pedido.setTotal(BigDecimal.valueOf(45.75));
        pedido.setFechaCreacion(LocalDateTime.now());
        pedido.setNombreCliente("María");
        pedido.setApellidosCliente("Gómez");
        pedido.setEmailCliente("maria@example.com");
        pedido.setProvinciaEnvio("Cádiz");
        pedido.setCiudadEnvio("Cádiz");
        pedido.setCodigoPostalEnvio("11001");
        pedido.setDireccionEnvio("Avenida Siempre Viva 456");
        pedido.setEnviado(true);
    
        assertEquals(usuario, pedido.getUsuario()); // Verifica que el usuario se haya establecido correctamente
        assertEquals(BigDecimal.valueOf(45.75), pedido.getTotal()); // Verifica que el total se haya establecido correctamente
        assertNotNull(pedido.getFechaCreacion()); // Verifica que la fecha de creación se haya establecido correctamente
        assertEquals("María", pedido.getNombreCliente()); // Verifica que el nombre del cliente se haya establecido correctamente
        assertEquals("Gómez", pedido.getApellidosCliente()); // Verifica que los apellidos del cliente se hayan establecido correctamente
        assertEquals("maria@example.com", pedido.getEmailCliente()); // Verifica que el email del cliente se haya establecido correctamente
        assertEquals("Cádiz", pedido.getProvinciaEnvio()); // Verifica que la provincia de envío se haya establecido correctamente
        assertEquals("Cádiz", pedido.getCiudadEnvio()); // Verifica que la ciudad de envío se haya establecido correctamente
        assertEquals("11001", pedido.getCodigoPostalEnvio()); // Verifica que el código postal de envío se haya establecido correctamente
        assertEquals("Avenida Siempre Viva 456", pedido.getDireccionEnvio()); // Verifica que la dirección de envío se haya establecido correctamente
        assertTrue(pedido.isEnviado());
    }

    @Test
    @DisplayName("Pedido - Métodos para agregar y eliminar items")
    public void testAgregarYEliminarItems() {
        // Crear pedido vacío
        Pedido pedido = new Pedido();
        assertEquals(BigDecimal.ZERO, pedido.getTotal());
        assertTrue(pedido.getItems().isEmpty());

        // Crear producto simulado
        Producto producto = new Producto();
        producto.setNombre("Taza artesanal");
        producto.setPrecio(new BigDecimal("15.00"));

        // Crear ítem de pedido
        PedidoItem item = new PedidoItem(pedido, producto);

        // Añadir ítem
        pedido.addItem(item);

        // Verificaciones tras añadir
        assertEquals(1, pedido.getItems().size());
        assertEquals(new BigDecimal("15.00"), pedido.getTotal());
        assertEquals(pedido, item.getPedido());

        // Crear segundo ítem
        Producto producto2 = new Producto();
        producto2.setNombre("Plato decorativo");
        producto2.setPrecio(new BigDecimal("20.00"));

        PedidoItem item2 = new PedidoItem(pedido, producto2);
        pedido.addItem(item2);

        // Verificar total acumulado
        assertEquals(2, pedido.getItems().size());
        assertEquals(new BigDecimal("35.00"), pedido.getTotal());

        // Eliminar el primer ítem
        pedido.removeItem(item);

        // Verificar que se eliminó correctamente
        assertEquals(1, pedido.getItems().size());
        assertEquals(new BigDecimal("20.00"), pedido.getTotal());
        assertNull(item.getPedido());
    }
}
