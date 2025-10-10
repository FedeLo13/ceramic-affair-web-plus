package es.uca.tfg.ceramic_affair_web.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import es.uca.tfg.ceramic_affair_web.DTOs.CarritoItemDTO;
import es.uca.tfg.ceramic_affair_web.DTOs.CheckoutDTO;
import es.uca.tfg.ceramic_affair_web.DTOs.CheckoutResponseDTO;
import es.uca.tfg.ceramic_affair_web.DTOs.PedidoDataDTO;
import es.uca.tfg.ceramic_affair_web.DTOs.ProductoMapper;
import es.uca.tfg.ceramic_affair_web.entities.Carrito;
import es.uca.tfg.ceramic_affair_web.entities.CarritoItem;
import es.uca.tfg.ceramic_affair_web.entities.Categoria;
import es.uca.tfg.ceramic_affair_web.entities.Pago;
import es.uca.tfg.ceramic_affair_web.entities.Pedido;
import es.uca.tfg.ceramic_affair_web.entities.Producto;
import es.uca.tfg.ceramic_affair_web.entities.TipoPago;
import es.uca.tfg.ceramic_affair_web.entities.Usuario;
import es.uca.tfg.ceramic_affair_web.exceptions.CarritoException;
import es.uca.tfg.ceramic_affair_web.exceptions.PagoException;
import es.uca.tfg.ceramic_affair_web.repositories.CarritoRepo;
import es.uca.tfg.ceramic_affair_web.repositories.CategoriaRepo;
import es.uca.tfg.ceramic_affair_web.repositories.PagoRepo;
import es.uca.tfg.ceramic_affair_web.repositories.PedidoRepo;
import es.uca.tfg.ceramic_affair_web.repositories.ProductoRepo;
import es.uca.tfg.ceramic_affair_web.repositories.UsuarioRepo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

/**
 * Test para el servicio de checkout.
 * Proporciona pruebas de integración para el proceso de checkout.
 * 
 * @version 1.0
 */
@SpringBootTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CheckoutServiceTest {

    @Autowired
    private CheckoutService checkoutService;

    @Autowired
    private UsuarioRepo usuarioRepo;

    @Autowired
    private ProductoRepo productoRepo;

    @Autowired
    private CategoriaRepo categoriaRepo;

    @Autowired
    private CarritoRepo carritoRepo;

    @Autowired
    private PedidoRepo pedidoRepo;

    @Autowired
    private PagoRepo pagoRepo;

    @SpyBean
    private PagoService pagoService;

    @PersistenceContext
    private EntityManager entityManager;

    @MockitoBean
    private GmailEmailService gmailEmailService;

    @MockitoBean
    private RecaptchaService recaptchaService;

    @Test
    @DisplayName("Servicio - Checkout de usuario registrado")
    public void testCheckoutUsuarioRegistrado() {
        // Crear un usuario de prueba
        Usuario usuario = new Usuario("testuser@example.com", "testpassword");
        usuarioRepo.save(usuario);

        // Crear un par de productos de prueba
        Categoria categoria = new Categoria("Categoría 1");
        categoriaRepo.save(categoria);
        Producto producto1 = new Producto("Producto 1", categoria, "Descripción 1", 10f, 5f, 0f, new BigDecimal("10.00"), false, null);
        productoRepo.save(producto1);
        Producto producto2 = new Producto("Producto 2", categoria, "Descripción 2", 20f, 10f, 0f, new BigDecimal("20.00"), false, null);
        productoRepo.save(producto2);

        // Crear un carrito para el usuario y agregar los productos
        Carrito carrito = new Carrito(usuario);
        carrito.addItem(new CarritoItem(carrito, producto1));
        carrito.addItem(new CarritoItem(carrito, producto2));
        carritoRepo.save(carrito);

        // Crear el DTO de checkout
        PedidoDataDTO pedidoDTO = new PedidoDataDTO();
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

        // Realizar el checkout
        CheckoutResponseDTO response = checkoutService.checkout(checkoutDTO);

        // Verificar que el checkout se realizó correctamente
        assertNotNull(response);
        assertNotNull(response.getPedidoId());
        assertEquals("SUCCESS", response.getEstado());
        assertEquals(new BigDecimal("30.00"), response.getTotal());

        // Verificar que hay un pedido y un pago asociados al usuario
        List<Pedido> pedidos = pedidoRepo.findByUsuarioId(usuario.getId());
        assertEquals(1, pedidos.size());

        List<Pago> pagos = pagoRepo.findByUsuarioId(usuario.getId());
        assertEquals(1, pagos.size());

        // Verificar que el carrito del usuario ya no existe
        assertFalse(carritoRepo.findByUsuario(usuario).isPresent());

        // Verificar que los productos están marcados como vendidos
        Producto producto1Actualizado = productoRepo.findById(producto1.getId()).orElse(null);
        Producto producto2Actualizado = productoRepo.findById(producto2.getId()).orElse(null);
        assertNotNull(producto1Actualizado);
        assertNotNull(producto2Actualizado);
        assertTrue(producto1Actualizado.isSoldOut());
        assertTrue(producto2Actualizado.isSoldOut());
    }

    @Test
    @DisplayName("Servicio - Checkout de usuario con carrito inválido")
    public void testCheckoutUsuarioConCarritoInvalido() {
        // Crear un usuario de prueba
        Usuario usuario = new Usuario("testuser@example.com", "testpassword");
        usuarioRepo.save(usuario);

        // Crear un par de productos de prueba
        Categoria categoria = new Categoria("Categoría 1");
        categoriaRepo.save(categoria);
        Producto producto1 = new Producto("Producto 1", categoria, "Descripción 1", 10f, 5f, 0f, new BigDecimal("10.00"), false, null);
        productoRepo.save(producto1);
        Producto producto2 = new Producto("Producto 2", categoria, "Descripción 2", 20f, 10f, 0f, new BigDecimal("20.00"), false, null);
        productoRepo.save(producto2);

        // Crear un carrito para el usuario y agregar los productos
        Carrito carrito = new Carrito(usuario);
        carrito.addItem(new CarritoItem(carrito, producto1));
        carrito.addItem(new CarritoItem(carrito, producto2));
        carritoRepo.save(carrito);
        
        // Simular que uno de los productos del carrito se ha vendido
        producto2.setSoldOut(true);
        productoRepo.save(producto2);

        // Crear el DTO de checkout
        PedidoDataDTO pedidoDTO = new PedidoDataDTO();
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

        // Realizar el checkout y verificar que lanza la excepción de carrito inválido
        assertThrows(CarritoException.CarritoInvalido.class, () -> {
            checkoutService.checkout(checkoutDTO);
        });
    }

    @Test
    @DisplayName("Servicio - Checkout con pago inválido")
    public void testCheckoutConPagoInvalido() {
        // Crear un usuario de prueba
        Usuario usuario = new Usuario("testuser@example.com", "testpassword");
        usuarioRepo.save(usuario);

        // Crear un par de productos de prueba
        Categoria categoria = new Categoria("Categoría 1");
        categoriaRepo.save(categoria);
        Producto producto1 = new Producto("Producto 1", categoria, "Descripción 1", 10f, 5f, 0f, new BigDecimal("10.00"), false, null);
        productoRepo.save(producto1);
        Producto producto2 = new Producto("Producto 2", categoria, "Descripción 2", 20f, 10f, 0f, new BigDecimal("20.00"), false, null);
        productoRepo.save(producto2);

        // Crear un carrito para el usuario y agregar los productos
        Carrito carrito = new Carrito(usuario);
        carrito.addItem(new CarritoItem(carrito, producto1));
        carrito.addItem(new CarritoItem(carrito, producto2));
        carritoRepo.save(carrito);

        // Crear el DTO de checkout
        PedidoDataDTO pedidoDTO = new PedidoDataDTO();
        pedidoDTO.setNombreCliente("Test");
        pedidoDTO.setApellidosCliente("User");
        pedidoDTO.setEmailCliente("testuser@example.com");
        pedidoDTO.setProvincia("Provincia");
        pedidoDTO.setCiudad("Ciudad");
        pedidoDTO.setCodigoPostal("12345");
        pedidoDTO.setDireccion("Dirección 123");

        CheckoutDTO checkoutDTO = new CheckoutDTO();
        checkoutDTO.setUsuarioId(usuario.getId());
        checkoutDTO.setTotal(new BigDecimal("-10.00")); // Total inválido para simular pago inválido
        checkoutDTO.setTipoPago(TipoPago.BIZUM);
        checkoutDTO.setPedido(pedidoDTO);

        // Realizar el checkout y verificar que lanza la excepción de pago inválido
        assertThrows(PagoException.PagoInvalido.class, () -> {
            checkoutService.checkout(checkoutDTO);
        });
    }

    @Test
    @DisplayName("Servicio - Checkout de usuario invitado")
    public void testCheckoutUsuarioInvitado() {
        // Crear un par de productos de prueba
        Categoria categoria = new Categoria("Categoría 1");
        categoriaRepo.save(categoria);
        Producto producto1 = new Producto("Producto 1", categoria, "Descripción 1", 10f, 5f, 0f, new BigDecimal("10.00"), false, null);
        productoRepo.save(producto1);
        Producto producto2 = new Producto("Producto 2", categoria, "Descripción 2", 20f, 10f, 0f, new BigDecimal("20.00"), false, null);
        productoRepo.save(producto2);

        // Creamos la lista de items del carrito
        CarritoItemDTO item1 = new CarritoItemDTO(ProductoMapper.toDTO(producto1), producto1.getPrecio());
        CarritoItemDTO item2 = new CarritoItemDTO(ProductoMapper.toDTO(producto2), producto2.getPrecio());
        List<CarritoItemDTO> items = List.of(item1, item2);

        // Crear el DTO de checkout
        PedidoDataDTO pedidoDTO = new PedidoDataDTO();
        pedidoDTO.setNombreCliente("Test");
        pedidoDTO.setApellidosCliente("User");
        pedidoDTO.setEmailCliente("testuser@example.com");
        pedidoDTO.setProvincia("Provincia");
        pedidoDTO.setCiudad("Ciudad");
        pedidoDTO.setCodigoPostal("12345");
        pedidoDTO.setDireccion("Dirección 123");

        CheckoutDTO checkoutDTO = new CheckoutDTO();
        checkoutDTO.setItems(items);
        checkoutDTO.setTotal(new BigDecimal("30.00"));
        checkoutDTO.setTipoPago(TipoPago.BIZUM);
        checkoutDTO.setPedido(pedidoDTO);

        // Realizar el checkout
        CheckoutResponseDTO response = checkoutService.checkout(checkoutDTO);

        // Verificar que el checkout se realizó correctamente
        assertNotNull(response);
        assertNotNull(response.getPedidoId());
        assertEquals("SUCCESS", response.getEstado());
        assertEquals(new BigDecimal("30.00"), response.getTotal());

        // Verificar que hay un pedido y un pago asociado a dicho pedido
        Pedido pedido = pedidoRepo.findById(response.getPedidoId()).orElse(null);
        assertNotNull(pedido);

        Pago pago = pagoRepo.findByPedidoId(pedido.getId()).orElse(null);
        assertNotNull(pago);

        // Verificar que los productos están marcados como vendidos
        Producto producto1Actualizado = productoRepo.findById(producto1.getId()).orElse(null);
        Producto producto2Actualizado = productoRepo.findById(producto2.getId()).orElse(null);
        assertNotNull(producto1Actualizado);
        assertNotNull(producto2Actualizado);
        assertTrue(producto1Actualizado.isSoldOut());
        assertTrue(producto2Actualizado.isSoldOut());
    }

    @Test
    @DisplayName("Servicio - Checkout de usuario invitado con items inválidos")
    public void testCheckoutUsuarioInvitadoConItemsInvalidos() {
        // Crear un par de productos de prueba
        Categoria categoria = new Categoria("Categoría 1");
        categoriaRepo.save(categoria);
        Producto producto1 = new Producto("Producto 1", categoria, "Descripción 1", 10f, 5f, 0f, new BigDecimal("10.00"), false, null);
        productoRepo.save(producto1);
        Producto producto2 = new Producto("Producto 2", categoria, "Descripción 2", 20f, 10f, 0f, new BigDecimal("20.00"), false, null);
        productoRepo.save(producto2);

        // Creamos la lista de items del carrito
        CarritoItemDTO item1 = new CarritoItemDTO(ProductoMapper.toDTO(producto1), producto1.getPrecio());
        CarritoItemDTO item2 = new CarritoItemDTO(ProductoMapper.toDTO(producto2), producto2.getPrecio());
        List<CarritoItemDTO> items = List.of(item1, item2);

        // Simular que uno de los productos del carrito se ha vendido
        producto2.setSoldOut(true);
        productoRepo.save(producto2);

        // Crear el DTO de checkout
        PedidoDataDTO pedidoDTO = new PedidoDataDTO();
        pedidoDTO.setNombreCliente("Test");
        pedidoDTO.setApellidosCliente("User");
        pedidoDTO.setEmailCliente("testuser@example.com");
        pedidoDTO.setProvincia("Provincia");
        pedidoDTO.setCiudad("Ciudad");
        pedidoDTO.setCodigoPostal("12345");
        pedidoDTO.setDireccion("Dirección 123");

        CheckoutDTO checkoutDTO = new CheckoutDTO();
        checkoutDTO.setItems(items);
        checkoutDTO.setTotal(new BigDecimal("30.00"));
        checkoutDTO.setTipoPago(TipoPago.BIZUM);
        checkoutDTO.setPedido(pedidoDTO);

        // Realizar el checkout y verificar que lanza la excepción de carrito inválido
        assertThrows(CarritoException.CarritoInvalido.class, () -> {
            checkoutService.checkout(checkoutDTO);
        });
    }

    @Test
    @DisplayName("Servicio - Confirmar que hay rollback si falla tras marcar productos como vendidos")
    public void testRollbackSiFallaTrasMarcarProductosComoVendidos() {
        // Crear un usuario de prueba
        Usuario usuario = new Usuario("testuser@example.com", "testpassword");
        usuarioRepo.save(usuario);

        // Crear un par de productos de prueba
        Categoria categoria = new Categoria("Categoría 1");
        categoriaRepo.save(categoria);
        Producto producto1 = new Producto("Producto 1", categoria, "Descripción 1", 10f, 5f, 0f, new BigDecimal("10.00"), false, null);
        productoRepo.save(producto1);
        Producto producto2 = new Producto("Producto 2", categoria, "Descripción 2", 20f, 10f, 0f, new BigDecimal("20.00"), false, null);
        productoRepo.save(producto2);

        // Crear un carrito para el usuario y agregar los productos
        Carrito carrito = new Carrito(usuario);
        carrito.addItem(new CarritoItem(carrito, producto1));
        carrito.addItem(new CarritoItem(carrito, producto2));
        carritoRepo.save(carrito);

        // Crear el DTO de checkout
        PedidoDataDTO pedidoDTO = new PedidoDataDTO();
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

        // Simular fallo forzando excepción después de marcar productos como vendidos
        doThrow(new RuntimeException("Fallo simulado"))
            .when(pagoService).createPagoUsuario(anyLong(), any(Pedido.class), any(), any());

        // Realizar el checkout y verificar que lanza la excepción
        assertThrows(RuntimeException.class, () -> {
            checkoutService.checkout(checkoutDTO);
        });

        entityManager.clear(); // Limpiar el contexto de persistencia para forzar la recarga desde la base de datos

        // Verificar que los productos NO están marcados como vendidos (rollback)
        Producto producto1Actualizado = productoRepo.findById(producto1.getId()).orElse(null);
        Producto producto2Actualizado = productoRepo.findById(producto2.getId()).orElse(null);
        assertNotNull(producto1Actualizado);
        assertNotNull(producto2Actualizado);
        assertFalse(producto1Actualizado.isSoldOut());
        assertFalse(producto2Actualizado.isSoldOut());
    }
}
