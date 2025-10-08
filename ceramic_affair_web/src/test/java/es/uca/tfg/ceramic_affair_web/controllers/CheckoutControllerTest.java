package es.uca.tfg.ceramic_affair_web.controllers;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.uca.tfg.ceramic_affair_web.DTOs.CheckoutDTO;
import es.uca.tfg.ceramic_affair_web.DTOs.CheckoutResponseDTO;
import es.uca.tfg.ceramic_affair_web.DTOs.PedidoDTO;
import es.uca.tfg.ceramic_affair_web.controllers.common.CheckoutController;
import es.uca.tfg.ceramic_affair_web.entities.Pedido;
import es.uca.tfg.ceramic_affair_web.entities.TipoPago;
import es.uca.tfg.ceramic_affair_web.entities.Usuario;
import es.uca.tfg.ceramic_affair_web.exceptions.AuthException;
import es.uca.tfg.ceramic_affair_web.exceptions.CarritoException;
import es.uca.tfg.ceramic_affair_web.security.JwtUtils;
import es.uca.tfg.ceramic_affair_web.services.CheckoutService;

/**
 * Clase de prueba para el controlador de Checkout.
 * Proporciona pruebas de capa web para las operaciones expuestas en el controlador de Checkout,
 * simulando peticiones HTTP sin interactuar con la base de datos.
 * 
 * @version 1.0
 */
@WebMvcTest(controllers = CheckoutController.class)
@AutoConfigureMockMvc
public class CheckoutControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CheckoutService checkoutService;

    @MockitoBean
    private JwtUtils jwtUtils;

    @Test
    @DisplayName("Controlador - Procesar checkout")
    public void testProcesarCheckout() throws Exception {
        // Crear usuario simulado
        Usuario usuario = new Usuario("testuser@example.com", "password");
        usuario.setId(1L);

        // Crear DTO de checkout simulado
        PedidoDTO pedidoDTO = new PedidoDTO();
        pedidoDTO.setNombreCliente("Test");
        pedidoDTO.setApellidosCliente("User");
        pedidoDTO.setEmailCliente("testuser@example.com");
        pedidoDTO.setProvincia("Provincia");
        pedidoDTO.setCiudad("Ciudad");
        pedidoDTO.setCodigoPostal("12345");
        pedidoDTO.setDireccion("Dirección 123");

        CheckoutDTO checkoutDTO = new CheckoutDTO();
        checkoutDTO.setUsuarioId(usuario.getId());
        checkoutDTO.setTotal(new BigDecimal("30.00"));
        checkoutDTO.setTipoPago(TipoPago.BIZUM);
        checkoutDTO.setPedido(pedidoDTO);

        // Crear pedido simulado
        Pedido pedido = new Pedido(
            pedidoDTO.getNombreCliente(),
            pedidoDTO.getApellidosCliente(),
            pedidoDTO.getEmailCliente(),
            pedidoDTO.getProvincia(),
            pedidoDTO.getCiudad(),
            pedidoDTO.getCodigoPostal(),
            pedidoDTO.getDireccion()
        );
        pedido.setId(1L);

        // Simular el comportamiento del servicio de checkout
        when(checkoutService.checkout(ArgumentMatchers.any(CheckoutDTO.class))).thenReturn(new CheckoutResponseDTO(pedido.getId(), "SUCCESS", checkoutDTO.getTotal()));


        // Realizar la petición POST al endpoint de checkout
        mockMvc.perform(post("/api/public/checkout/procesar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(checkoutDTO)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Checkout procesado con éxito"))
            .andExpect(jsonPath("$.data.pedidoId").value(1L))
            .andExpect(jsonPath("$.data.estado").value("SUCCESS"))
            .andExpect(jsonPath("$.data.total").value(30.00));
    }

    @Test
    @DisplayName("Controlador - Procesar checkout con carrito inválido")
    public void testProcesarCheckoutInvalidData() throws Exception {
        // Crear usuario simulado
        Usuario usuario = new Usuario("testuser@example.com", "password");
        usuario.setId(1L);

        // Crear DTO de checkout simulado
        PedidoDTO pedidoDTO = new PedidoDTO();
        pedidoDTO.setNombreCliente("Test");
        pedidoDTO.setApellidosCliente("User");
        pedidoDTO.setEmailCliente("testuser@example.com");
        pedidoDTO.setProvincia("Provincia");
        pedidoDTO.setCiudad("Ciudad");
        pedidoDTO.setCodigoPostal("12345");
        pedidoDTO.setDireccion("Dirección 123");

        CheckoutDTO checkoutDTO = new CheckoutDTO();
        checkoutDTO.setUsuarioId(usuario.getId());
        checkoutDTO.setTotal(new BigDecimal("30.00"));
        checkoutDTO.setTipoPago(TipoPago.BIZUM);
        checkoutDTO.setPedido(pedidoDTO);

        doThrow(new CarritoException.CarritoInvalido()).when(checkoutService).checkout(ArgumentMatchers.any(CheckoutDTO.class));

        // Realizar la petición POST al endpoint de checkout
        mockMvc.perform(post("/api/public/checkout/procesar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(checkoutDTO)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.error").value("Business exception"))
            .andExpect(jsonPath("$.message").value("Cart is not valid"))
            .andExpect(jsonPath("$.path").value("/api/public/checkout/procesar"));
    }

    @Test
    @DisplayName("Controlador - Procesar checkout con usuario no encontrado")
    public void testProcesarCheckoutUserNotFound() throws Exception {
        // Crear DTO de checkout simulado
        PedidoDTO pedidoDTO = new PedidoDTO();
        pedidoDTO.setNombreCliente("Test");
        pedidoDTO.setApellidosCliente("User");
        pedidoDTO.setEmailCliente("testuser@example.com");
        pedidoDTO.setProvincia("Provincia");
        pedidoDTO.setCiudad("Ciudad");
        pedidoDTO.setCodigoPostal("12345");
        pedidoDTO.setDireccion("Dirección 123");

        CheckoutDTO checkoutDTO = new CheckoutDTO();
        checkoutDTO.setUsuarioId(999L); // ID de usuario que no existe
        checkoutDTO.setTotal(new BigDecimal("30.00"));
        checkoutDTO.setTipoPago(TipoPago.BIZUM);
        checkoutDTO.setPedido(pedidoDTO);

        doThrow(new AuthException.UsuarioNoEncontrado()).when(checkoutService).checkout(ArgumentMatchers.any(CheckoutDTO.class));

        // Realizar la petición POST al endpoint de checkout
        mockMvc.perform(post("/api/public/checkout/procesar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(checkoutDTO)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.error").value("Business exception"))
            .andExpect(jsonPath("$.message").value("User not found"))
            .andExpect(jsonPath("$.path").value("/api/public/checkout/procesar"));
    }

    @Test
    @DisplayName("Controlador - Procesar checkout con conflicto en carrito")
    public void testProcesarCheckoutCartConflict() throws Exception {
        // Crear usuario simulado
        Usuario usuario = new Usuario("testuser@example.com", "password");
        usuario.setId(1L);

        // Crear DTO de checkout simulado
        PedidoDTO pedidoDTO = new PedidoDTO();
        pedidoDTO.setNombreCliente("Test");
        pedidoDTO.setApellidosCliente("User");
        pedidoDTO.setEmailCliente("testuser@example.com");
        pedidoDTO.setProvincia("Provincia");
        pedidoDTO.setCiudad("Ciudad");
        pedidoDTO.setCodigoPostal("12345");
        pedidoDTO.setDireccion("Dirección 123");

        CheckoutDTO checkoutDTO = new CheckoutDTO();
        checkoutDTO.setUsuarioId(usuario.getId());
        checkoutDTO.setTotal(new BigDecimal("30.00"));
        checkoutDTO.setTipoPago(TipoPago.BIZUM);
        checkoutDTO.setPedido(pedidoDTO);

        doThrow(new CarritoException.CarritoYaVendido()).when(checkoutService).checkout(ArgumentMatchers.any(CheckoutDTO.class));

        // Realizar la petición POST al endpoint de checkout
        mockMvc.perform(post("/api/public/checkout/procesar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(checkoutDTO)))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.status").value(409))
            .andExpect(jsonPath("$.error").value("Business exception"))
            .andExpect(jsonPath("$.message").value("Cart contains sold out products"))
            .andExpect(jsonPath("$.path").value("/api/public/checkout/procesar"));
    }
        

    @TestConfiguration
    public static class NoSecurityConfig {
        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                    .anyRequest().permitAll()
                );
            return http.build();
        }
    }
}
