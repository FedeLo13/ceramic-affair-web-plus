package es.uca.tfg.ceramic_affair_web.repositories;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import es.uca.tfg.ceramic_affair_web.entities.Pedido;
import es.uca.tfg.ceramic_affair_web.entities.Usuario;
import es.uca.tfg.ceramic_affair_web.security.Rol;

/**
 * Clase de prueba para el repositorio PedidoRepo.
 * Proporciona pruebas de integración para las operaciones CRUD en la entidad Pedido.
 * 
 * @version 1.0
 */
@DataJpaTest
public class PedidoRepoTest {

    @Autowired
    private PedidoRepo pedidoRepo;

    @Autowired
    private UsuarioRepo usuarioRepo;

    @Test
    @DisplayName("Repositorio - Guardar y cargar pedido")
    void testGuardarYCargarPedido() {
        // Crear y guardar un nuevo pedido asociado al usuario
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

        // Cargar el pedido por ID
        Pedido pedidoCargado = pedidoRepo.findById(pedido.getId()).orElse(null);

        assertNotNull(pedidoCargado);
        assertEquals(pedido.getNombreCliente(), pedidoCargado.getNombreCliente());
        assertEquals(pedido.getApellidosCliente(), pedidoCargado.getApellidosCliente());
        assertEquals(pedido.getEmailCliente(), pedidoCargado.getEmailCliente());
        assertEquals(pedido.getCiudadEnvio(), pedidoCargado.getCiudadEnvio());
        assertEquals(pedido.getProvinciaEnvio(), pedidoCargado.getProvinciaEnvio());
        assertEquals(pedido.getCodigoPostalEnvio(), pedidoCargado.getCodigoPostalEnvio());
        assertEquals(pedido.getDireccionEnvio(), pedidoCargado.getDireccionEnvio());
    }

    @Test
    @DisplayName("Repositorio - Eliminar pedido")
    void testEliminarPedido() {
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

        // Eliminar el pedido
        pedidoRepo.delete(pedido);

        // Verificar que el pedido ya no esté presente en la base de datos
        Pedido pedidoEliminado = pedidoRepo.findById(pedido.getId()).orElse(null);
        assertNull(pedidoEliminado);
    }

    @Test
    @DisplayName("Repositorio - Buscar pedidos por usuarioId")
    void testBuscarPedidosPorUsuarioId() {
        // Crear y guardar un nuevo usuario
        Usuario usuario = new Usuario(
            "usuario1",
            "password",
            Set.of(Rol.USER)
        );
        usuarioRepo.save(usuario);

        // Crear y guardar un nuevo pedido asociado al usuario
        Pedido pedido = new Pedido(
            "Juan",
            "Pérez",
            "juan.perez@example.com",
            "Valencia",
            "Valencia",
            "12345",
            "Calle Falsa 123"
        );
        pedido.setUsuario(usuario);
        pedidoRepo.save(pedido);

        // Buscar pedidos por usuarioId
        List<Pedido> pedidos = pedidoRepo.findByUsuarioId(usuario.getId());

        assertNotNull(pedidos);
        assertEquals(1, pedidos.size());
        assertEquals(pedido.getId(), pedidos.get(0).getId());
    }

    @Test
    @DisplayName("Repositorio - Buscar pedidos enviados ordenados por fecha de creación descendente")
    void testBuscarPedidosEnviadosOrdenadosPorFechaCreacionDesc() {
        // Crear y guardar pedidos enviados y no enviados
        Pedido pedido1 = new Pedido(
            "Juan",
            "Pérez",
            "juan.perez@example.com",
            "Valencia",
            "Valencia",
            "12345",
            "Calle Falsa 123"
        );
        pedido1.setEnviado(true);

        Pedido pedido2 = new Pedido(
            "Ana",
            "García",
            "ana.garcia@example.com",
            "Madrid",
            "Madrid",
            "67890",
            "Avenida Siempre Viva 456"
        );
        pedido2.setEnviado(false);

        Pedido pedido3 = new Pedido(
            "Luis",
            "Martínez",
            "luis.martinez@example.com",
            "Barcelona",
            "Barcelona",
            "54321",
            "Plaza Mayor 789"
        );
        pedido3.setEnviado(true);

        pedidoRepo.save(pedido1);
        pedidoRepo.save(pedido2);
        pedidoRepo.save(pedido3);

        // Buscar pedidos enviados ordenados por fecha de creación descendente
        List<Pedido> pedidosEnviados = pedidoRepo.findByEnviadoTrueOrderByFechaCreacionDesc();
        assertNotNull(pedidosEnviados);
        assertEquals(2, pedidosEnviados.size());
        assertTrue(pedidosEnviados.stream().allMatch(Pedido::isEnviado));
    }

    @Test
    @DisplayName("Repositorio - Buscar pedidos no enviados ordenados por fecha de creación descendente")
    void testBuscarPedidosNoEnviadosOrdenadosPorFechaCreacionDesc() {
        // Crear y guardar pedidos enviados y no enviados
        Pedido pedido1 = new Pedido(
            "Juan",
            "Pérez",
            "juan.perez@example.com",
            "Valencia",
            "Valencia",
            "12345",
            "Calle Falsa 123"
        );
        pedido1.setEnviado(true);

        Pedido pedido2 = new Pedido(
            "Ana",
            "García",
            "ana.garcia@example.com",
            "Madrid",
            "Madrid",
            "67890",
            "Avenida Siempre Viva 456"
        );
        pedido2.setEnviado(false);

        Pedido pedido3 = new Pedido(
            "Luis",
            "Martínez",
            "luis.martinez@example.com",
            "Barcelona",
            "Barcelona",
            "54321",
            "Plaza Mayor 789"
        );
        pedido3.setEnviado(false);

        pedidoRepo.save(pedido1);
        pedidoRepo.save(pedido2);
        pedidoRepo.save(pedido3);

        // Buscar pedidos no enviados ordenados por fecha de creación descendente
        List<Pedido> pedidosNoEnviados = pedidoRepo.findByEnviadoFalseOrderByFechaCreacionDesc();
        assertNotNull(pedidosNoEnviados);
        assertEquals(2, pedidosNoEnviados.size());
        assertTrue(pedidosNoEnviados.stream().allMatch(p -> !p.isEnviado()));
    }
}
