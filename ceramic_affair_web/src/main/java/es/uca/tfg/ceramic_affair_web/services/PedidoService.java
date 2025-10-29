package es.uca.tfg.ceramic_affair_web.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.uca.tfg.ceramic_affair_web.DTOs.CarritoItemDTO;
import es.uca.tfg.ceramic_affair_web.DTOs.PedidoDataDTO;
import es.uca.tfg.ceramic_affair_web.entities.Carrito;
import es.uca.tfg.ceramic_affair_web.entities.Pedido;
import es.uca.tfg.ceramic_affair_web.entities.PedidoItem;
import es.uca.tfg.ceramic_affair_web.entities.Producto;
import es.uca.tfg.ceramic_affair_web.exceptions.AuthException;
import es.uca.tfg.ceramic_affair_web.exceptions.PedidoException;
import es.uca.tfg.ceramic_affair_web.exceptions.ProductoException;
import es.uca.tfg.ceramic_affair_web.repositories.PedidoRepo;
import es.uca.tfg.ceramic_affair_web.repositories.ProductoRepo;
import es.uca.tfg.ceramic_affair_web.repositories.UsuarioRepo;

/**
 * Servicio para gestionar operaciones relacionadas con los pedidos.
 * 
 * @version 1.0
 */
@Service
public class PedidoService {

    @Autowired
    private PedidoRepo pedidoRepo;

    @Autowired
    private CarritoService carritoService;

    @Autowired
    private ProductoRepo productoRepo;

    @Autowired
    private UsuarioRepo usuarioRepo;

    @Autowired
    private EmailService emailService;

    /**
     * Método para obtener un pedido por su ID.
     * 
     * @param id el ID del pedido
     * @return el pedido correspondiente al ID, o vacío si no existe
     * @throws PedidoException.NoEncontrado si el pedido no existe
     */
    public Pedido getPedidoById(Long id) {
        return pedidoRepo.findById(id)
                .orElseThrow(() -> new PedidoException.NoEncontrado());
    }

    /**
     * Método para obtener todos los pedidos.
     * 
     * @return Lista de pedidos.
     */
    public List<Pedido> getAllPedidos() {
        return pedidoRepo.findAll();
    }

    /**
     * Método para obtener los pedidos de un usuario específico.
     * 
     * @param usuarioId el ID del usuario
     * @return la lista de pedidos del usuario
     * @throws AuthException.UsuarioNoEncontrado si el usuario no existe
     */
    public List<Pedido> getPedidosByUsuarioId(Long usuarioId) {
        if (!usuarioRepo.existsById(usuarioId)) {
            throw new AuthException.UsuarioNoEncontrado();
        }
        return pedidoRepo.findByUsuarioId(usuarioId);
    }

    /**
     * Método para obtener todos los pedidos enviados
     * 
     * @return la lista de pedidos enviados
     */
    public List<Pedido> getPedidosEnviados() {
        return pedidoRepo.findByEnviadoTrueOrderByFechaCreacionDesc();
    }

    /**
     * Método para obtener todos los pedidos no enviados
     * 
     * @return la lista de pedidos no enviados
     */
    public List<Pedido> getPedidosNoEnviados() {
        return pedidoRepo.findByEnviadoFalseOrderByFechaCreacionDesc();
    }

    /**
     * Método para crear un nuevo pedido de un usuario.
     * 
     * @param usuarioId el ID del usuario que realiza el pedido
     * @param pedido los datos del pedido a crear
     * @return el pedido creado
     * @throws AuthException.UsuarioNoEncontrado si el usuario no existe
     */
    public Pedido createPedidoUsuario(Long usuarioId, PedidoDataDTO pedido) {
        Carrito carrito = carritoService.getCarrito(usuarioId).get();

        Pedido nuevoPedido = new Pedido(
            carrito,
            pedido.getNombreCliente(),
            pedido.getApellidosCliente(),
            pedido.getEmailCliente(),
            pedido.getProvincia(),
            pedido.getCiudad(),
            pedido.getCodigoPostal(),
            pedido.getDireccion()
        );

        return pedidoRepo.save(nuevoPedido);
    }

    /**
     * Método para crear un nuevo pedido de un invitado.
     * 
     * @param items los items del carrito del invitado
     * @param pedido los datos del pedido a crear
     * @return el pedido creado
     * @throws ProductoException.NoEncontrado si algún producto no existe
     */
    public Pedido createPedidoInvitado(List<CarritoItemDTO> items, PedidoDataDTO pedido) {
        // Crear pedido vacío con los datos del cliente
        Pedido nuevoPedido = new Pedido(
            pedido.getNombreCliente(),
            pedido.getApellidosCliente(),
            pedido.getEmailCliente(),
            pedido.getProvincia(),
            pedido.getCiudad(),
            pedido.getCodigoPostal(),
            pedido.getDireccion()
        );

        // Convertir los items del carrito del invitado a PedidoItems y asignarlos al pedido
        for (CarritoItemDTO itemDTO : items) {
            Producto producto = productoRepo.findById(itemDTO.getProducto().getId())
                    .orElseThrow(() -> new ProductoException.NoEncontrado(itemDTO.getProducto().getId()));
            PedidoItem pedidoItem = new PedidoItem(nuevoPedido, producto);
            nuevoPedido.addItem(pedidoItem);
        }

        return pedidoRepo.save(nuevoPedido);
    }

    /**
     * Método para marcar un pedido como enviado o no enviado.
     * 
     * @param idPedido el ID del pedido a actualizar
     * @param enviado true para marcar como enviado, false para no enviado
     * @return el pedido actualizado
     * @throws PedidoException.NoEncontrado si el pedido no existe
     */
    public void setPedidoEnviado(Long idPedido, boolean enviado) {
        Pedido pedido = pedidoRepo.findById(idPedido)
                .orElseThrow(() -> new PedidoException.NoEncontrado());
        pedido.setEnviado(enviado);
        if (enviado) {
            enviarCorreoEnvio(pedido);
        }
        pedidoRepo.save(pedido);
    }

    /**
     * Método para eliminar un pedido por su ID.
     * 
     * @param id el ID del pedido a eliminar
     * @throws PedidoException.NoEncontrado si el pedido no existe
     */
    public void deletePedido(Long id) {
        if (!pedidoRepo.existsById(id)) {
            throw new PedidoException.NoEncontrado();
        }
        pedidoRepo.deleteById(id);
    }

    private void enviarCorreoEnvio(Pedido pedido) {
        String asunto = "Order Shipment Confirmation";
        String cuerpo = "<p>Dear " + pedido.getNombreCliente() + ",</p>" +
                        "<p>Your order with ID #" + pedido.getId() + " has been shipped.</p>" +
                        "<p>Thank you for shopping with us!</p>" +
                        "<p>Best regards,</p>" +
                        "<p>Ceramic Affair Team</p>";

        emailService.sendEmail(pedido.getEmailCliente(), asunto, cuerpo);
    }
}
