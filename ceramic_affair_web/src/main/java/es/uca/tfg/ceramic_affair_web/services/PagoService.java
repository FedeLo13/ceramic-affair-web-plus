package es.uca.tfg.ceramic_affair_web.services;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

/**
 * Servicio para gestionar operaciones relacionadas con los pagos.
 * 
 * @version 1.0
 */
@Service
public class PagoService {

    @Autowired
    private PagoRepo pagoRepo;

    @Autowired
    private UsuarioRepo usuarioRepo;

    @Autowired
    private PedidoRepo pedidoRepo;

    /**
     * Método para crear un pago asociado a un usuario.
     * 
     * @param usuarioId el ID del usuario que realiza el pago
     * @param pedido   el pedido asociado al pago
     * @param importe  el importe del pago
     * @param tipoPago el tipo de pago (BIZUM o TARJETA)
     * @return el pago creado
     * @throws AuthException.UsuarioNoEncontrado si el usuario no existe
     */
    public Pago createPagoUsuario(Long usuarioId, Pedido pedido, BigDecimal importe, TipoPago tipoPago) {
        Usuario usuario = usuarioRepo.findById(usuarioId)
                .orElseThrow(() -> new AuthException.UsuarioNoEncontrado());
        Pago pago = new Pago(usuario, pedido, importe, tipoPago);
        return pagoRepo.save(pago);
    }

    /**
     * Método para crear un pago sin asociar a un usuario (invitado).
     * 
     * @param pedido   el pedido asociado al pago
     * @param importe  el importe del pago
     * @param tipoPago el tipo de pago (BIZUM o TARJETA)
     * @return el pago creado
     */
    public Pago createPagoInvitado(Pedido pedido, BigDecimal importe, TipoPago tipoPago) {
        Pago pago = new Pago(null, pedido, importe, tipoPago);
        return pagoRepo.save(pago);
    }

    /**
     * Método para obtener el pago asociado a un pedido específico.
     * 
     * @param pedidoId el ID del pedido
     * @return el pago asociado al pedido
     * @throws PedidoException.NoEncontrado si el pedido no existe
     * @throws PagoException.NoEncontrado si no se encuentra un pago asociado al pedido
     */
    public Pago getPagoByPedidoId(Long pedidoId) {
        if (!pedidoRepo.existsById(pedidoId)) {
            throw new PedidoException.NoEncontrado();
        }
        return pagoRepo.findByPedidoId(pedidoId)
                .orElseThrow(() -> new PagoException.NoEncontrado());
    }

    /**
     * Método para obtener los pagos de un usuario específico.
     * 
     * @param usuarioId el ID del usuario
     * @return una lista de pagos asociados al usuario
     * @throws AuthException.UsuarioNoEncontrado si el usuario no existe
     */
    public List<Pago> getPagosByUsuarioId(Long usuarioId) {
        if (!usuarioRepo.existsById(usuarioId)) {
            throw new AuthException.UsuarioNoEncontrado();
        }
        return pagoRepo.findByUsuarioId(usuarioId);
    }
}
