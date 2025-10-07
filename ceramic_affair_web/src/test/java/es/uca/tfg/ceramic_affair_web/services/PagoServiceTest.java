package es.uca.tfg.ceramic_affair_web.services;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import es.uca.tfg.ceramic_affair_web.entities.Pago;
import es.uca.tfg.ceramic_affair_web.entities.Pedido;
import es.uca.tfg.ceramic_affair_web.entities.TipoPago;
import es.uca.tfg.ceramic_affair_web.entities.Usuario;
import es.uca.tfg.ceramic_affair_web.exceptions.AuthException;
import es.uca.tfg.ceramic_affair_web.exceptions.PagoException;
import es.uca.tfg.ceramic_affair_web.exceptions.PedidoException;
import es.uca.tfg.ceramic_affair_web.repositories.PagoRepo;
import es.uca.tfg.ceramic_affair_web.repositories.PedidoRepo;
import es.uca.tfg.ceramic_affair_web.repositories.UsuarioRepo;
import jakarta.transaction.Transactional;

/**
 * Clase de prueba para el servicio PagoService.
 * Proporciona pruebas de integración para las operaciones relacionadas con los pagos.
 * 
 * @version 1.0
 */
@SpringBootTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class PagoServiceTest {

    @Autowired
    private PagoService pagoService;

    @Autowired
    private PagoRepo pagoRepo;

    @Autowired
    private PedidoRepo pedidoRepo;

    @Autowired
    private UsuarioRepo usuarioRepo;

    @MockitoBean
    private GmailEmailService gmailEmailService;

    @MockitoBean
    private RecaptchaService recaptchaService;

    @Test
    @DisplayName("Servicio - Crear pago usuario")
    public void testCrearPagoUsuario() {
        Usuario usuario = new Usuario("usuario1@ejemplo.com", "password123");
        Pedido pedido = new Pedido(
            "Nombre",
            "Apellidos",
            "cliente@example.com",
            "Provincia",
            "Ciudad",
            "postal",
            "Calle 123"
        );
        usuarioRepo.save(usuario);
        pedidoRepo.save(pedido);

        Pago pago = pagoService.createPagoUsuario(usuario.getId(), pedido, BigDecimal.valueOf(100.00), TipoPago.BIZUM);

        assertNotNull(pago);
        assertEquals(usuario.getId(), pago.getUsuario().getId());
        assertEquals(pedido.getId(), pago.getPedido().getId());
        assertEquals(BigDecimal.valueOf(100.00), pago.getImporte());
        assertEquals(TipoPago.BIZUM, pago.getTipoPago());
    }

    @Test
    @DisplayName("Servicio - Crear pago usuario no encontrado")
    public void testCrearPagoUsuarioNoEncontrado() {
        Pedido pedido = new Pedido(
            "Nombre",
            "Apellidos",
            "cliente@example.com",
            "Provincia",
            "Ciudad",
            "postal",
            "Calle 123"
        );
        pedidoRepo.save(pedido);

        assertThrows(AuthException.UsuarioNoEncontrado.class, () -> {
            pagoService.createPagoUsuario(999L, pedido, BigDecimal.valueOf(100.00), TipoPago.BIZUM);
        });
    }

    @Test
    @DisplayName("Servicio - Crear pago invitado")
    public void testCrearPagoInvitado() {
        Pedido pedido = new Pedido(
            "Nombre",
            "Apellidos",
            "cliente@example.com",
            "Provincia",
            "Ciudad",
            "postal",
            "Calle 123"
        );
        pedidoRepo.save(pedido);

        Pago pago = pagoService.createPagoInvitado(pedido, BigDecimal.valueOf(50.00), TipoPago.TARJETA);

        assertNotNull(pago);
        assertNull(pago.getUsuario());
        assertEquals(pedido.getId(), pago.getPedido().getId());
        assertEquals(BigDecimal.valueOf(50.00), pago.getImporte());
        assertEquals(TipoPago.TARJETA, pago.getTipoPago());
    }

    @Test
    @DisplayName("Servicio - Obtener pago por ID de pedido")
    public void testGetPagoByPedidoId() {
        Pedido pedido = new Pedido(
            "Nombre",
            "Apellidos",
            "cliente@example.com",
            "Provincia",
            "Ciudad",
            "postal",
            "Calle 123"
        );
        pedidoRepo.save(pedido);

        Pago pago = new Pago(null, pedido, BigDecimal.valueOf(75.00), TipoPago.BIZUM);
        pagoRepo.save(pago);

        Pago pagoObtenido = pagoService.getPagoByPedidoId(pedido.getId());

        assertNotNull(pagoObtenido);
        assertEquals(pago.getId(), pagoObtenido.getId());
        assertEquals(pedido.getId(), pagoObtenido.getPedido().getId());
        assertEquals(BigDecimal.valueOf(75.00), pagoObtenido.getImporte());
        assertEquals(TipoPago.BIZUM, pagoObtenido.getTipoPago());
    }

    @Test
    @DisplayName("Servicio - Obtener pago por ID de pedido no encontrado")
    public void testGetPagoByPedidoIdNoEncontrado() {
        assertThrows(PedidoException.NoEncontrado.class, () -> {
            pagoService.getPagoByPedidoId(999L);
        });
    }

    @Test
    @DisplayName("Servicio - Obtener pago por ID de pedido sin pago")
    public void testGetPagoByPedidoIdSinPago() {
        Pedido pedido = new Pedido(
            "Nombre",
            "Apellidos",
            "cliente@example.com",
            "Provincia",
            "Ciudad",
            "postal",
            "Calle 123"
        );
        pedidoRepo.save(pedido);

        assertThrows(PagoException.NoEncontrado.class, () -> {
            pagoService.getPagoByPedidoId(pedido.getId());
        });
    }

    @Test
    @DisplayName("Servicio - Obtener pagos por ID de usuario") 
    public void testGetPagosByUsuarioId() {
        Usuario usuario = new Usuario("cliente@example.com", "password");
        usuarioRepo.save(usuario);

        Pedido pedido = new Pedido(
            "Nombre",
            "Apellidos",
            "cliente@example.com",
            "Provincia",
            "Ciudad",
            "postal",
            "Calle 123"
        );
        pedidoRepo.save(pedido);

        Pago pago = pagoService.createPagoUsuario(usuario.getId(), pedido, BigDecimal.valueOf(100.00), TipoPago.BIZUM);

        List<Pago> pagos = pagoService.getPagosByUsuarioId(usuario.getId());

        assertNotNull(pagos);
        assertEquals(1, pagos.size());
        assertEquals(pago.getId(), pagos.get(0).getId());
    }
}
