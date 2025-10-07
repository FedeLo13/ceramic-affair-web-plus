package es.uca.tfg.ceramic_affair_web.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.uca.tfg.ceramic_affair_web.DTOs.CarritoItemDTO;
import es.uca.tfg.ceramic_affair_web.DTOs.CheckoutDTO;
import es.uca.tfg.ceramic_affair_web.DTOs.CheckoutResponseDTO;
import es.uca.tfg.ceramic_affair_web.entities.Pedido;
import es.uca.tfg.ceramic_affair_web.exceptions.CarritoException;
import es.uca.tfg.ceramic_affair_web.exceptions.PagoException;
import jakarta.transaction.Transactional;

/**
 * Servicio para gestionar el proceso de checkout.
 * 
 * @version 1.0
 */
@Service
public class CheckoutService {

    @Autowired
    private PagoValidatorFactory pagoValidatorFactory;

    @Autowired
    private CarritoService carritoService;

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private PagoService pagoService;

    /**
     * Método para procesar el checkout.
     * 
     * @param checkoutDTO los datos del checkout
     * @return la respuesta del checkout
     * @throws CarritoException.CarritoInvalido si el carrito es inválido
     * @throws CarritoException.CarritoYaVendido si algún producto del carrito ya está vendido
     * @throws AuthException.UsuarioNoEncontrado si el usuario no existe
     * @throws PagoException.PagoInvalido si el pago es inválido
     */
    @Transactional
    public CheckoutResponseDTO checkout(CheckoutDTO checkoutDTO) {

        // Paso 1: Validar el stock de los productos en el carrito

        // Si el id del usuario está presente, el pedido es de un usuario registrado
        if (checkoutDTO.getUsuarioId() != null) {
            List<CarritoItemDTO> itemsNoDisponibles = carritoService.validarCarritoUsuario(checkoutDTO.getUsuarioId());
            if (!itemsNoDisponibles.isEmpty()) {
                throw new CarritoException.CarritoInvalido();
            }
        } else {
            // Si no, es un pedido de invitado
            List<CarritoItemDTO> itemsNoDisponibles = carritoService.validarCarritoInvitado(checkoutDTO.getItems());
            if (!itemsNoDisponibles.isEmpty()) {
                throw new CarritoException.CarritoInvalido();
            }
        }

        // Paso 2: Validar el pago
        PagoValidator pagoValidator = pagoValidatorFactory.getValidator(checkoutDTO.getTipoPago());
        if (!pagoValidator.validar(checkoutDTO.getTotal())) {
            throw new PagoException.PagoInvalido();
        }

        // Paso 3: Establecer como sold out los productos comprados
        if (checkoutDTO.getUsuarioId() != null) {
            carritoService.marcarProductosComoSoldOutUsuario(checkoutDTO.getUsuarioId());
        } else {
            carritoService.marcarProductosComoSoldOutInvitado(checkoutDTO.getItems());
        } 

        // Paso 4: Crear el pedido y el pago
        Pedido pedido = null;

        // Si el id del usuario está presente, el pedido es de un usuario registrado
        if (checkoutDTO.getUsuarioId() != null) {
            pedido = pedidoService.createPedidoUsuario(checkoutDTO.getUsuarioId(), checkoutDTO.getPedido());
            pagoService.createPagoUsuario(checkoutDTO.getUsuarioId(), pedido, checkoutDTO.getTotal(), checkoutDTO.getTipoPago());

            // Vaciar el carrito del usuario
            carritoService.clearCarrito(checkoutDTO.getUsuarioId());
        } else {
            // Si no, es un pedido de invitado
            pedido = pedidoService.createPedidoInvitado(checkoutDTO.getItems(), checkoutDTO.getPedido());
            pagoService.createPagoInvitado(pedido, checkoutDTO.getTotal(), checkoutDTO.getTipoPago());
        }

        return new CheckoutResponseDTO(pedido.getId(), "SUCCESS", pedido.getTotal());
    }
}
