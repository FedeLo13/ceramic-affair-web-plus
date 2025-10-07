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

import es.uca.tfg.ceramic_affair_web.DTOs.CarritoItemDTO;
import es.uca.tfg.ceramic_affair_web.DTOs.PedidoDTO;
import es.uca.tfg.ceramic_affair_web.DTOs.ProductoDTO;
import es.uca.tfg.ceramic_affair_web.DTOs.ProductoMapper;
import es.uca.tfg.ceramic_affair_web.entities.Carrito;
import es.uca.tfg.ceramic_affair_web.entities.Categoria;
import es.uca.tfg.ceramic_affair_web.entities.Pedido;
import es.uca.tfg.ceramic_affair_web.entities.Producto;
import es.uca.tfg.ceramic_affair_web.entities.Usuario;
import es.uca.tfg.ceramic_affair_web.exceptions.AuthException;
import es.uca.tfg.ceramic_affair_web.exceptions.PedidoException;
import es.uca.tfg.ceramic_affair_web.exceptions.ProductoException;
import es.uca.tfg.ceramic_affair_web.repositories.CarritoRepo;
import es.uca.tfg.ceramic_affair_web.repositories.CategoriaRepo;
import es.uca.tfg.ceramic_affair_web.repositories.PedidoRepo;
import es.uca.tfg.ceramic_affair_web.repositories.ProductoRepo;
import es.uca.tfg.ceramic_affair_web.repositories.UsuarioRepo;
import jakarta.transaction.Transactional;

/**
 * Clase de prueba para el servicio PedidoService.
 * Proporciona pruebas de integración para las operaciones relacionadas con los pedidos.
 * 
 * @version 1.0
 */
@SpringBootTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class PedidoServiceTest {

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private PedidoRepo pedidoRepo;

    @Autowired
    private ProductoRepo productoRepo;

    @Autowired
    private CategoriaRepo categoriaRepo;

    @Autowired
    private UsuarioRepo usuarioRepo;

    @Autowired
    private CarritoRepo carritoRepo;

    @MockitoBean
    private GmailEmailService gmailEmailService;

    @MockitoBean
    private RecaptchaService recaptchaService;

    @Test
    @DisplayName("Servicio - Obtener pedido por ID")
    public void testGetPedidoById() {
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

        Pedido encontrado = pedidoService.getPedidoById(pedido.getId());

        assertNotNull(encontrado);
        assertEquals(pedido.getId(), encontrado.getId());
    }

    @Test
    @DisplayName("Servicio - Obtener pedido por ID no existente")
    public void testGetPedidoByIdNoExistente() {
        assertThrows(PedidoException.NoEncontrado.class, () -> {
            pedidoService.getPedidoById(9999L);
        });
    }

    @Test
    @DisplayName("Servicio - Obtener todos los pedidos")
    public void testGetAllPedidos() {
        Pedido pedido1 = new Pedido(
            "Nombre1",
            "Apellidos1",
            "cliente1@example.com",
            "Provincia1",
            "Ciudad1",
            "postal1",
            "Calle 1231"
        );
        pedido1.setEnviado(false);

        Pedido pedido2 = new Pedido(
            "Nombre2",
            "Apellidos2",
            "cliente2@example.com",
            "Provincia2",
            "Ciudad2",
            "postal2",
            "Calle 1232"
        );
        pedido2.setEnviado(true);
        pedidoRepo.save(pedido1);
        pedidoRepo.save(pedido2);

        List<Pedido> pedidos = pedidoService.getAllPedidos();

        assertNotNull(pedidos);
        assertTrue(pedidos.size() >= 2);
        
        Pedido pedidoActivo = pedidos.stream()
            .filter(p -> p.getId().equals(pedido1.getId()))
            .findFirst()
            .orElse(null);

        Pedido pedidoEnviado = pedidos.stream()
            .filter(p -> p.getId().equals(pedido2.getId()))
            .findFirst()
            .orElse(null);

        assertNotNull(pedidoActivo);
        assertNotNull(pedidoEnviado);
        assertFalse(pedidoActivo.isEnviado());
        assertTrue(pedidoEnviado.isEnviado());
    }

    @Test
    @DisplayName("Servicio - Obtener pedidos por ID de usuario")
    public void testGetPedidosByUsuarioId() {
        // Crear usuario
        Usuario usuario = new Usuario("cliente@example.com", "password");
        usuarioRepo.save(usuario);
        Carrito carrito = new Carrito(usuario);
        carritoRepo.save(carrito);

        // Crear pedido asociados al usuario
        Pedido pedido = new Pedido(
            carrito,
            "Nombre1",
            "Apellidos1",
            "cliente1@example.com",
            "Provincia1",
            "Ciudad1",
            "postal1",
            "Calle 1231"
        );

        pedidoRepo.save(pedido);

        List<Pedido> pedidos = pedidoService.getPedidosByUsuarioId(usuario.getId());

        assertNotNull(pedidos);
        assertEquals(1, pedidos.size());
        assertEquals(pedido.getId(), pedidos.get(0).getId());
        assertEquals(usuario.getId(), pedidos.get(0).getUsuario().getId());
    }

    @Test
    @DisplayName("Servicio - Obtener pedidos de usuario inexistente")
    public void testGetPedidosByUsuarioIdNoExistente() {
        assertThrows(AuthException.UsuarioNoEncontrado.class, () -> {
            pedidoService.getPedidosByUsuarioId(9999L);
        });
    }

    @Test
    @DisplayName("Servicio - Obtener todos los pedidos enviados")
    public void testGetAllPedidosEnviados() {
        Pedido pedido1 = new Pedido(
            "Nombre1",
            "Apellidos1",
            "cliente1@example.com",
            "Provincia1",
            "Ciudad1",
            "postal1",
            "Calle 1231"
        );
        pedido1.setEnviado(false);

        Pedido pedido2 = new Pedido(
            "Nombre2",
            "Apellidos2",
            "cliente2@example.com",
            "Provincia2",
            "Ciudad2",
            "postal2",
            "Calle 1232"
        );
        pedido2.setEnviado(true);
        pedidoRepo.save(pedido1);
        pedidoRepo.save(pedido2);

        List<Pedido> pedidosEnviados = pedidoService.getPedidosEnviados();

        assertNotNull(pedidosEnviados);
        assertTrue(pedidosEnviados.size() >= 1);
        assertTrue(pedidosEnviados.stream().allMatch(Pedido::isEnviado));
        assertTrue(pedidosEnviados.stream().anyMatch(p -> p.getId().equals(pedido2.getId())));
    }

    @Test
    @DisplayName("Servicio - Obtener todos los pedidos no enviados")
    public void testGetAllPedidosNoEnviados() {
        Pedido pedido1 = new Pedido(
            "Nombre1",
            "Apellidos1",
            "cliente1@example.com",
            "Provincia1",
            "Ciudad1",
            "postal1",
            "Calle 1231"
        );
        pedido1.setEnviado(false);

        Pedido pedido2 = new Pedido(
            "Nombre2",
            "Apellidos2",
            "cliente2@example.com",
            "Provincia2",
            "Ciudad2",
            "postal2",
            "Calle 1232"
        );
        pedido2.setEnviado(true);
        pedidoRepo.save(pedido1);
        pedidoRepo.save(pedido2);

        List<Pedido> pedidosNoEnviados = pedidoService.getPedidosNoEnviados();

        assertNotNull(pedidosNoEnviados);
        assertTrue(pedidosNoEnviados.size() >= 1);
        assertTrue(pedidosNoEnviados.stream().allMatch(p -> !p.isEnviado()));
        assertTrue(pedidosNoEnviados.stream().anyMatch(p -> p.getId().equals(pedido1.getId())));
    }

    @Test
    @DisplayName("Servicio - Crear pedido para usuario")
    public void testCreatePedidoUsuario() {
        // Crear usuario
        Usuario usuario = new Usuario("cliente@example.com", "password");
        usuarioRepo.save(usuario);
        Carrito carrito = new Carrito(usuario);
        carritoRepo.save(carrito);

        PedidoDTO pedidoDTO = new PedidoDTO();
        pedidoDTO.setNombreCliente("Nombre");
        pedidoDTO.setApellidosCliente("Apellidos");
        pedidoDTO.setEmailCliente("cliente@example.com");
        pedidoDTO.setProvincia("Provincia");
        pedidoDTO.setCiudad("Ciudad");
        pedidoDTO.setCodigoPostal("postal");
        pedidoDTO.setDireccion("Calle 123");

        // Crear pedido
        Pedido nuevoPedido = pedidoService.createPedidoUsuario(usuario.getId(), pedidoDTO);

        assertNotNull(nuevoPedido);
        assertNotNull(nuevoPedido.getId());
        assertEquals(usuario.getId(), nuevoPedido.getUsuario().getId());
        assertEquals("Nombre", nuevoPedido.getNombreCliente());
        assertEquals("Apellidos", nuevoPedido.getApellidosCliente());
        assertEquals("cliente@example.com", nuevoPedido.getEmailCliente());
        assertEquals("Provincia", nuevoPedido.getProvinciaEnvio());
        assertEquals("Ciudad", nuevoPedido.getCiudadEnvio());
        assertEquals("postal", nuevoPedido.getCodigoPostalEnvio());
        assertEquals("Calle 123", nuevoPedido.getDireccionEnvio());
        assertFalse(nuevoPedido.isEnviado());
        assertNotNull(nuevoPedido.getFechaCreacion());
        assertNotNull(nuevoPedido.getItems());
        assertTrue(nuevoPedido.getItems().isEmpty());
    }

    @Test
    @DisplayName("Servicio - Crear pedido para usuario inexistente")
    public void testCreatePedidoUsuarioNoExistente() {
        assertThrows(AuthException.UsuarioNoEncontrado.class, () -> {
            PedidoDTO pedidoDTO = new PedidoDTO();
            pedidoDTO.setNombreCliente("Nombre");
            pedidoDTO.setApellidosCliente("Apellidos");
            pedidoDTO.setEmailCliente("cliente@example.com");
            pedidoDTO.setProvincia("Provincia");
            pedidoDTO.setCiudad("Ciudad");
            pedidoDTO.setCodigoPostal("postal");
            pedidoDTO.setDireccion("Calle 123");
            pedidoService.createPedidoUsuario(9999L, pedidoDTO);
        });
    }

    @Test
    @DisplayName("Servicio - Crear pedido para invitado")
    public void testCreatePedidoInvitado() {
        Categoria categoria = new Categoria("Categoría 1");
        categoriaRepo.save(categoria);
        Producto producto1 = new Producto("Producto 1", categoria, "Descripción 1", 10f, 5f, 0f, new BigDecimal("10.00"), false, null);
        productoRepo.save(producto1);
        Producto producto2 = new Producto("Producto 2", categoria, "Descripción 2", 20f, 10f, 0f, new BigDecimal("20.00"), false, null);
        productoRepo.save(producto2);

        ProductoDTO productoDTO1 = ProductoMapper.toDTO(producto1);
        ProductoDTO productoDTO2 = ProductoMapper.toDTO(producto2);

        CarritoItemDTO item1 = new CarritoItemDTO(productoDTO1, producto1.getPrecio());
        CarritoItemDTO item2 = new CarritoItemDTO(productoDTO2, producto2.getPrecio());

        PedidoDTO pedidoDTO = new PedidoDTO();
        pedidoDTO.setNombreCliente("Nombre");
        pedidoDTO.setApellidosCliente("Apellidos");
        pedidoDTO.setEmailCliente("cliente@example.com");
        pedidoDTO.setProvincia("Provincia");
        pedidoDTO.setCiudad("Ciudad");
        pedidoDTO.setCodigoPostal("postal");
        pedidoDTO.setDireccion("Calle 123");
        List<CarritoItemDTO> items = List.of(item1, item2);

        // Crear pedido
        Pedido nuevoPedido = pedidoService.createPedidoInvitado(items, pedidoDTO);

        assertNotNull(nuevoPedido);
        assertNotNull(nuevoPedido.getId());
        assertNull(nuevoPedido.getUsuario());
        assertEquals("Nombre", nuevoPedido.getNombreCliente());
        assertEquals("Apellidos", nuevoPedido.getApellidosCliente());
        assertEquals("cliente@example.com", nuevoPedido.getEmailCliente());
        assertEquals("Provincia", nuevoPedido.getProvinciaEnvio());
        assertEquals("Ciudad", nuevoPedido.getCiudadEnvio());
        assertEquals("postal", nuevoPedido.getCodigoPostalEnvio());
        assertEquals("Calle 123", nuevoPedido.getDireccionEnvio());
        assertFalse(nuevoPedido.isEnviado());
        assertNotNull(nuevoPedido.getFechaCreacion());
        assertNotNull(nuevoPedido.getItems());
        assertEquals(2, nuevoPedido.getItems().size());
        assertEquals(producto1.getId(), nuevoPedido.getItems().get(0).getProducto().getId());
        assertEquals(producto2.getId(), nuevoPedido.getItems().get(1).getProducto().getId());
        assertEquals(producto1.getPrecio(), nuevoPedido.getItems().get(0).getPrecioUnitario());
        assertEquals(producto2.getPrecio(), nuevoPedido.getItems().get(1).getPrecioUnitario());
    }

    @Test
    @DisplayName("Servicio - Crear pedido para invitado con producto no existente")
    public void testCreatePedidoInvitadoProductoNoExistente() {
        Categoria categoria = new Categoria("Categoría 1");
        categoriaRepo.save(categoria);
        Producto producto1 = new Producto("Producto 1", categoria, "Descripción 1", 10f, 5f, 0f, new BigDecimal("10.00"), false, null);
        productoRepo.save(producto1);

        ProductoDTO productoDTO1 = ProductoMapper.toDTO(producto1);
        ProductoDTO productoDTO2 = new ProductoDTO();
        productoDTO2.setId(9999L); // ID no existente

        CarritoItemDTO item1 = new CarritoItemDTO(productoDTO1, producto1.getPrecio());
        CarritoItemDTO item2 = new CarritoItemDTO(productoDTO2, new BigDecimal("20.00"));

        PedidoDTO pedidoDTO = new PedidoDTO();
        pedidoDTO.setNombreCliente("Nombre");
        pedidoDTO.setApellidosCliente("Apellidos");
        pedidoDTO.setEmailCliente("cliente@example.com");
        pedidoDTO.setProvincia("Provincia");
        pedidoDTO.setCiudad("Ciudad");
        pedidoDTO.setCodigoPostal("postal");
        pedidoDTO.setDireccion("Calle 123");
        List<CarritoItemDTO> items = List.of(item1, item2);

        assertThrows(ProductoException.NoEncontrado.class, () -> {
            pedidoService.createPedidoInvitado(items, pedidoDTO);
        });
    }

    @Test
    @DisplayName("Servicio - Marcar pedido como enviado")
    public void testSetPedidoEnviado() {
        Pedido pedido = new Pedido(
            "Nombre",
            "Apellidos",
            "cliente@example.com",
            "Provincia",
            "Ciudad",
            "postal",
            "Calle 123"
        );
        pedido.setEnviado(false);

        pedidoRepo.save(pedido);

        pedidoService.setPedidoEnviado(pedido.getId(), true);

        Pedido actualizado = pedidoRepo.findById(pedido.getId()).orElseThrow();
        assertTrue(actualizado.isEnviado());
    }

    @Test
    @DisplayName("Servicio - Marcar pedido no existente como enviado")
    public void testSetPedidoEnviadoNoExistente() {
        assertThrows(PedidoException.NoEncontrado.class, () -> {
            pedidoService.setPedidoEnviado(9999L, true);
        });
    }

    @Test
    @DisplayName("Servicio - Eliminar pedido")
    public void testDeletePedido() {
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

        pedidoService.deletePedido(pedido.getId());

        assertFalse(pedidoRepo.findById(pedido.getId()).isPresent());
    }

    @Test
    @DisplayName("Servicio - Eliminar pedido no existente")
    public void testDeletePedidoNoExistente() {
        assertThrows(PedidoException.NoEncontrado.class, () -> {
            pedidoService.deletePedido(9999L);
        });
    }
}


