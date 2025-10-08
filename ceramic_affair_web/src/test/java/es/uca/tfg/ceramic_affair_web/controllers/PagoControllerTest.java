package es.uca.tfg.ceramic_affair_web.controllers;

import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import es.uca.tfg.ceramic_affair_web.controllers.common.PagoPublicController;
import es.uca.tfg.ceramic_affair_web.controllers.user.PagoUserController;
import es.uca.tfg.ceramic_affair_web.entities.Pago;
import es.uca.tfg.ceramic_affair_web.entities.Pedido;
import es.uca.tfg.ceramic_affair_web.entities.TipoPago;
import es.uca.tfg.ceramic_affair_web.entities.Usuario;
import es.uca.tfg.ceramic_affair_web.exceptions.AuthException;
import es.uca.tfg.ceramic_affair_web.exceptions.PagoException;
import es.uca.tfg.ceramic_affair_web.exceptions.PedidoException;
import es.uca.tfg.ceramic_affair_web.security.JwtAuthFilter;
import es.uca.tfg.ceramic_affair_web.security.JwtUtils;
import es.uca.tfg.ceramic_affair_web.services.PagoService;

/**
 * Clase de prueba para los controladores de Pago.
 * Proporciona pruebas de capa web para las operaciones CRUD expuestas en los controladores de Pago,
 * simulando peticiones HTTP sin interactuar con la base de datos.
 * 
 * @version 1.0
 */
@WebMvcTest(controllers = {PagoPublicController.class, PagoUserController.class})
@AutoConfigureMockMvc(addFilters = false) // Desactivar la configuración de seguridad para las pruebas
public class PagoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PagoService pagoService;

    @MockitoBean
    private JwtUtils jwtUtils;

    @MockitoBean
    private JwtAuthFilter jwtAuthFilter;

    @Test
    @DisplayName("Controlador - Obtener pago por pedido")
    public void testObtenerPagoPorPedido() throws Exception {
        // Crear un pedido simulado
        Pedido pedido = new Pedido(
            "Manola",
            "García",
            "manola.garcia@example.com",
            "Soria",
            "Soria",
            "12345",
            "Calle Falsa 123"
        );
        pedido.setId(1L);

        // Crear un pago simulado
        Pago pago = new Pago(
            null,
            pedido,
            BigDecimal.valueOf(150.75),
            TipoPago.TARJETA
        );
        pago.setId(1L);

        // Simular el comportamiento del servicio
        when(pagoService.getPagoByPedidoId(1L)).thenReturn(pago);

        // Realizar la petición GET y verificar la respuesta
        mockMvc.perform(get("/api/public/pagos/{pedidoId}", 1L))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Pago encontrado"))
            .andExpect(jsonPath("$.data.id").value(1))
            .andExpect(jsonPath("$.data.importe").value(150.75))
            .andExpect(jsonPath("$.data.tipoPago").value("TARJETA"))
            .andExpect(jsonPath("$.data.pedido.id").value(1));
    }

    @Test
    @DisplayName("Controlador - Obtener pago por pedido no encontrado")
    public void testObtenerPagoPorPedidoNoEncontrado() throws Exception {
        // Simular el comportamiento del servicio para un pedido no encontrado
        when(pagoService.getPagoByPedidoId(999L)).thenThrow(new PedidoException.NoEncontrado());

        // Realizar la petición GET y verificar la respuesta
        mockMvc.perform(get("/api/public/pagos/{pedidoId}", 999L))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.error").value("Business exception"))
            .andExpect(jsonPath("$.message").value("Order not found"))
            .andExpect(jsonPath("$.path").value("/api/public/pagos/999"));
    }

    @Test
    @DisplayName("Controlador - Obtener pago por pedido (pago no encontrado)")
    public void testObtenerPagoPorPedidoPagoNoEncontrado() throws Exception {
        // Simular el comportamiento del servicio para un pago no encontrado
        when(pagoService.getPagoByPedidoId(2L)).thenThrow(new PagoException.NoEncontrado());

        // Realizar la petición GET y verificar la respuesta
        mockMvc.perform(get("/api/public/pagos/{pedidoId}", 2L))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.error").value("Business exception"))
            .andExpect(jsonPath("$.message").value("Payment not found"))
            .andExpect(jsonPath("$.path").value("/api/public/pagos/2"));
    }

    @Test
    @DisplayName("Controlador - Obtener pagos por usuario")
    public void testObtenerPagosPorUsuario() throws Exception {
        // Crear un usuario simulado
        Usuario usuario = new Usuario("usuario@example.com", "password");
        usuario.setId(1L);

        // Crear un pedido simulado
        Pedido pedido = new Pedido(
            "Manola",
            "García",
            "manola.garcia@example.com",
            "Soria",
            "Soria",
            "12345",
            "Calle Falsa 123"
        );
        pedido.setId(1L);

        // Crear un pago simulado
        Pago pago = new Pago(
            usuario,
            pedido,
            BigDecimal.valueOf(150.75),
            TipoPago.TARJETA
        );
        pago.setId(1L);

        // Simular el comportamiento del servicio
        when(pagoService.getPagosByUsuarioId(1L)).thenReturn(List.of(pago));

        // Realizar la petición GET y verificar la respuesta
        mockMvc.perform(get("/api/user/pagos/{userId}", 1L))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Lista de pagos obtenida"))
            .andExpect(jsonPath("$.data[0].id").value(1))
            .andExpect(jsonPath("$.data[0].importe").value(150.75))
            .andExpect(jsonPath("$.data[0].tipoPago").value("TARJETA"))
            .andExpect(jsonPath("$.data[0].usuario.id").value(1))
            .andExpect(jsonPath("$.data[0].pedido.id").value(1));
    }

    @Test
    @DisplayName("Controlador - Obtener pagos por usuario no encontrado")
    public void testObtenerPagosPorUsuarioNoEncontrado() throws Exception {
        // Simular el comportamiento del servicio para un usuario no encontrado
        when(pagoService.getPagosByUsuarioId(999L)).thenThrow(new AuthException.UsuarioNoEncontrado());

        // Realizar la petición GET y verificar la respuesta
        mockMvc.perform(get("/api/user/pagos/{userId}", 999L))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.error").value("Business exception"))
            .andExpect(jsonPath("$.message").value("User not found"))
            .andExpect(jsonPath("$.path").value("/api/user/pagos/999"));
    }
}

