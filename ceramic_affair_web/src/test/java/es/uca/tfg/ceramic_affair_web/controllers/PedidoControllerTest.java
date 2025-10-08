package es.uca.tfg.ceramic_affair_web.controllers;

import static org.mockito.Mockito.when;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import es.uca.tfg.ceramic_affair_web.controllers.admin.PedidoAdminController;
import es.uca.tfg.ceramic_affair_web.controllers.common.PedidoPublicController;
import es.uca.tfg.ceramic_affair_web.controllers.user.PedidoUserController;
import es.uca.tfg.ceramic_affair_web.entities.Pedido;
import es.uca.tfg.ceramic_affair_web.entities.Usuario;
import es.uca.tfg.ceramic_affair_web.exceptions.AuthException;
import es.uca.tfg.ceramic_affair_web.exceptions.PedidoException;
import es.uca.tfg.ceramic_affair_web.security.JwtAuthFilter;
import es.uca.tfg.ceramic_affair_web.security.JwtUtils;
import es.uca.tfg.ceramic_affair_web.services.PedidoService;

/**
 * Clase de prueba para los controladores de pedidos.
 * Provide pruebas de capa web para las operaciones CRUD expuestas en los controladores de pedidos,
 * simulando peticiones HTTP sin interactuar con la base de datos.
 * 
 * @version 1.0
 */
@WebMvcTest(controllers = {PedidoPublicController.class, PedidoUserController.class, PedidoAdminController.class})
@AutoConfigureMockMvc(addFilters = false) // Desactivar la configuración de seguridad para las pruebas
public class PedidoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PedidoService pedidoService;

    @MockitoBean
    private JwtUtils jwtUtils;

    @MockitoBean
    private JwtAuthFilter jwtAuthFilter;

    @Test
    @DisplayName("Controlador - Obtener pedido por ID")
    public void testObtenerPedidoPorId() throws Exception {
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

        // Simular el comportamiento del servicio
        when(pedidoService.getPedidoById(1L)).thenReturn(pedido);

        // Realizar la petición GET y verificar la respuesta
        mockMvc.perform(get("/api/public/pedidos/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.message").value("Pedido encontrado"))
        .andExpect(jsonPath("$.data.id").value(1))
        .andExpect(jsonPath("$.data.nombreCliente").value("Manola"))
        .andExpect(jsonPath("$.data.apellidosCliente").value("García"))
        .andExpect(jsonPath("$.data.emailCliente").value("manola.garcia@example.com"))
        .andExpect(jsonPath("$.data.provinciaEnvio").value("Soria"))
        .andExpect(jsonPath("$.data.ciudadEnvio").value("Soria"))
        .andExpect(jsonPath("$.data.codigoPostalEnvio").value("12345"))
        .andExpect(jsonPath("$.data.direccionEnvio").value("Calle Falsa 123"));
    }

    @Test
    @DisplayName("Controlador - Obtener pedido por ID no encontrado")
    public void testObtenerPedidoPorIdNoEncontrado() throws Exception {
        // Simular el comportamiento del servicio para lanzar una excepción
        when(pedidoService.getPedidoById(999L)).thenThrow(new PedidoException.NoEncontrado());

        // Realizar la petición GET y verificar la respuesta
        mockMvc.perform(get("/api/public/pedidos/999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.error").value("Business exception"))
            .andExpect(jsonPath("$.message").value("Order not found"))
            .andExpect(jsonPath("$.path").value("/api/public/pedidos/999"));
    }

    @Test
    @DisplayName("Controlador - Listar pedidos de usuario")
    public void testListarPedidosUsuario() throws Exception {
        // Crear un usuario simulado
        Usuario usuario = new Usuario("usuario@example.com", "Usuario");
        usuario.setId(1L);

        // Crear pedidos simulados
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

        Pedido pedido2 = new Pedido(
            "Juan",
            "Pérez",
            "juan.perez@example.com",
            "Madrid",
            "Madrid",
            "54321",
            "Calle Verdadera 456"
        );
        pedido2.setId(2L);

        // Simular el comportamiento del servicio
        when(pedidoService.getPedidosByUsuarioId(1L)).thenReturn(List.of(pedido, pedido2));

        // Realizar la petición GET y verificar la respuesta
        mockMvc.perform(get("/api/user/pedidos/listar").param("usuarioId", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Pedidos obtenidos exitosamente"))
            .andExpect(jsonPath("$.data").isArray())
            .andExpect(jsonPath("$.data[0].id").value(1))
            .andExpect(jsonPath("$.data[0].nombreCliente").value("Manola"))
            .andExpect(jsonPath("$.data[1].id").value(2))
            .andExpect(jsonPath("$.data[1].nombreCliente").value("Juan"));
    }

    @Test
    @DisplayName("Controlador - Listar pedidos de usuario no encontrado")
    public void testListarPedidosUsuarioNoEncontrados() throws Exception {
        // Simular el comportamiento del servicio para un usuario sin pedidos
        when(pedidoService.getPedidosByUsuarioId(1L)).thenThrow( new AuthException.UsuarioNoEncontrado());

        // Realizar la petición GET y verificar la respuesta
        mockMvc.perform(get("/api/user/pedidos/listar").param("usuarioId", "1"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.error").value("Business exception"))
            .andExpect(jsonPath("$.message").value("User not found"))
            .andExpect(jsonPath("$.path").value("/api/user/pedidos/listar"));
    }

    @Test
    @DisplayName("Controlador - Listar todos los pedidos")
    public void testListarTodosLosPedidos() throws Exception {
        // Crear pedidos simulados
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
        pedido.setEnviado(true);

        Pedido pedido2 = new Pedido(
            "Juan",
            "Pérez",
            "juan.perez@example.com",
            "Madrid",
            "Madrid",
            "54321",
            "Calle Verdadera 456"
        );
        pedido2.setId(2L);
        pedido2.setEnviado(false);

        // Simular el comportamiento del servicio
        when(pedidoService.getAllPedidos()).thenReturn(List.of(pedido, pedido2));

        // Realizar la petición GET y verificar la respuesta
        mockMvc.perform(get("/api/admin/pedidos/listar"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Lista de pedidos obtenida con éxito"))
            .andExpect(jsonPath("$.data").isArray())
            .andExpect(jsonPath("$.data[0].id").value(1))
            .andExpect(jsonPath("$.data[0].nombreCliente").value("Manola"))
            .andExpect(jsonPath("$.data[0].enviado").value(true))
            .andExpect(jsonPath("$.data[1].id").value(2))
            .andExpect(jsonPath("$.data[1].nombreCliente").value("Juan"))
            .andExpect(jsonPath("$.data[1].enviado").value(false));
    }

    @Test
    @DisplayName("Controlador - Listar todos los pedidos enviados")
    public void testListarPedidosEnviados() throws Exception {
        // Crear pedidos simulados
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
        pedido.setEnviado(true);

        Pedido pedido2 = new Pedido(
            "Juan",
            "Pérez",
            "juan.perez@example.com",
            "Madrid",
            "Madrid",
            "54321",
            "Calle Verdadera 456"
        );
        pedido2.setId(2L);
        pedido2.setEnviado(true);

        // Simular el comportamiento del servicio
        when(pedidoService.getPedidosEnviados()).thenReturn(List.of(pedido, pedido2));

        // Realizar la petición GET y verificar la respuesta
        mockMvc.perform(get("/api/admin/pedidos/enviados"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Lista de pedidos enviados obtenida con éxito"))
            .andExpect(jsonPath("$.data").isArray())
            .andExpect(jsonPath("$.data[0].id").value(1))
            .andExpect(jsonPath("$.data[0].nombreCliente").value("Manola"))
            .andExpect(jsonPath("$.data[0].enviado").value(true))
            .andExpect(jsonPath("$.data[1].id").value(2))
            .andExpect(jsonPath("$.data[1].nombreCliente").value("Juan"))
            .andExpect(jsonPath("$.data[1].enviado").value(true));
    }

    @Test
    @DisplayName("Controlador - Listar todos los pedidos no enviados")
    public void testListarPedidosNoEnviados() throws Exception {
        // Crear pedidos simulados
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
        pedido.setEnviado(false);

        Pedido pedido2 = new Pedido(
            "Juan",
            "Pérez",
            "juan.perez@example.com",
            "Madrid",
            "Madrid",
            "54321",
            "Calle Verdadera 456"
        );
        pedido2.setId(2L);
        pedido2.setEnviado(false);

        // Simular el comportamiento del servicio
        when(pedidoService.getPedidosNoEnviados()).thenReturn(List.of(pedido, pedido2));

        // Realizar la petición GET y verificar la respuesta
        mockMvc.perform(get("/api/admin/pedidos/no-enviados"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Lista de pedidos no enviados obtenida con éxito"))
            .andExpect(jsonPath("$.data").isArray())
            .andExpect(jsonPath("$.data[0].id").value(1))
            .andExpect(jsonPath("$.data[0].nombreCliente").value("Manola"))
            .andExpect(jsonPath("$.data[0].enviado").value(false))
            .andExpect(jsonPath("$.data[1].id").value(2))
            .andExpect(jsonPath("$.data[1].nombreCliente").value("Juan"))
            .andExpect(jsonPath("$.data[1].enviado").value(false));
    }

    @Test
    @DisplayName("Controlador - Marcar pedido como enviado")
    public void testMarcarPedidoComoEnviado() throws Exception {
        // Simular el comportamiento del servicio
        doNothing().when(pedidoService).setPedidoEnviado(1L, true);

        // Realizar la petición PATCH y verificar la respuesta
        mockMvc.perform(patch("/api/admin/pedidos/1/enviado").param("enviado", "true"))
            .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Controlador - Marcar pedido como enviado no encontrado")
    public void testMarcarPedidoComoEnviadoNoEncontrado() throws Exception {
        // Simular el comportamiento del servicio para lanzar una excepción
        doThrow(new PedidoException.NoEncontrado()).when(pedidoService).setPedidoEnviado(999L, true);

        // Realizar la petición PATCH y verificar la respuesta
        mockMvc.perform(patch("/api/admin/pedidos/999/enviado").param("enviado", "true"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.error").value("Business exception"))
            .andExpect(jsonPath("$.message").value("Order not found"))
            .andExpect(jsonPath("$.path").value("/api/admin/pedidos/999/enviado"));
    }
}
