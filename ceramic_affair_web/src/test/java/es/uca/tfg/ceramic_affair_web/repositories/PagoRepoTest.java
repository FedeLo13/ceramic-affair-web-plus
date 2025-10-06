package es.uca.tfg.ceramic_affair_web.repositories;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import es.uca.tfg.ceramic_affair_web.entities.Pago;
import es.uca.tfg.ceramic_affair_web.entities.Pedido;
import es.uca.tfg.ceramic_affair_web.entities.TipoPago;
import es.uca.tfg.ceramic_affair_web.entities.Usuario;
import es.uca.tfg.ceramic_affair_web.security.Rol;

/**
 * Clase de prueba para el repositorio PagoRepo.
 * Proporciona pruebas de integración para las operaciones CRUD en la entidad Pago.
 * 
 * @version 1.0
 */
@DataJpaTest
public class PagoRepoTest {

    @Autowired
    private PagoRepo pagoRepo;

    @Autowired
    private UsuarioRepo usuarioRepo;

    @Autowired
    private PedidoRepo pedidoRepo;

    @Test
    @DisplayName("Repositorio - Guardar y cargar pago")
    void testGuardarYCargarPago() {
        // Crear y guardar un nuevo pedido
        Pedido pedido = new Pedido(
            "Juan",
            "Pérez",
            "juan.perez@example.com",
            "Valencia",
            "Valencia",
            "12345",
            "Calle Falsa 123"
        );
        pedidoRepo.save(pedido);

        // Crear y guardar un nuevo pago
        Pago pago = new Pago(null, pedido, BigDecimal.valueOf(100.0), TipoPago.BIZUM);
        pagoRepo.save(pago);

        // Cargar el pago por ID
        Pago pagoCargado = pagoRepo.findById(pago.getId()).orElse(null);

        assertNotNull(pagoCargado);
        assertEquals(pago.getImporte(), pagoCargado.getImporte());
        assertEquals(pago.getTipoPago(), pagoCargado.getTipoPago());
        assertEquals(pago.getPedido().getId(), pagoCargado.getPedido().getId());
        assertNull(pagoCargado.getUsuario());
    }

    @Test
    @DisplayName("Repositorio - Eliminar pago")
    void testEliminarPago() {
        // Crear y guardar un nuevo pedido
        Pedido pedido = new Pedido(
            "Ana",
            "García",
            "ana.garcia@example.com",
            "Madrid",
            "Madrid",
            "54321",
            "Avenida Siempre Viva 456"
        );
        pedidoRepo.save(pedido);

        // Crear y guardar un nuevo pago
        Pago pago = new Pago(null, pedido, BigDecimal.valueOf(200.0), TipoPago.TARJETA);
        pagoRepo.save(pago);

        // Eliminar el pago
        pagoRepo.delete(pago);

        // Intentar cargar el pago por ID
        Pago pagoEliminado = pagoRepo.findById(pago.getId()).orElse(null);
        assertNull(pagoEliminado);
    }

    @Test
    @DisplayName("Repositorio - Buscar pagos por usuario ID")
    void testBuscarPagosPorUsuarioId() {
        // Crear y guardar un nuevo usuario
        Usuario usuario = new Usuario("usuario1", "password", Set.of(Rol.USER));
        usuarioRepo.save(usuario);

        // Crear y guardar un nuevo pedido
        Pedido pedido = new Pedido(
            "Luis",
            "Martínez",
            "luis.martinez@example.com",
            "Sevilla",
            "Sevilla",
            "67890",
            "Plaza Mayor 789"
        );
        pedidoRepo.save(pedido);

        // Crear y guardar un nuevo pago asociado al usuario
        Pago pago = new Pago(usuario, pedido, BigDecimal.valueOf(150.0), TipoPago.BIZUM);
        pagoRepo.save(pago);

        // Buscar pagos por usuario ID
        List<Pago> pagos = pagoRepo.findByUsuarioId(usuario.getId());
        assertNotNull(pagos);
        assertEquals(1, pagos.size());
        assertEquals(pago.getId(), pagos.get(0).getId());
    }

    @Test
    @DisplayName("Repositorio - Buscar pago por pedido ID")
    void testBuscarPagoPorPedidoId() {
        // Crear y guardar un nuevo pedido
        Pedido pedido = new Pedido(
            "Marta",
            "López",
            "marta.lopez@example.com",
            "Madrid",
            "Madrid",
            "13579",
            "Calle Verdadera 321"
        );
        pedidoRepo.save(pedido);

        // Crear y guardar un nuevo pago asociado al pedido
        Pago pago = new Pago(null, pedido, BigDecimal.valueOf(250.0), TipoPago.TARJETA);
        pagoRepo.save(pago);

        // Buscar el pago por ID de pedido
        Optional<Pago> pagoOpt = pagoRepo.findByPedidoId(pedido.getId());
        assertTrue(pagoOpt.isPresent());
        assertEquals(pago.getId(), pagoOpt.get().getId());
    }
}
