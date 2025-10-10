package es.uca.tfg.ceramic_affair_web.DTOs;

import java.math.BigDecimal;
import java.util.List;

import es.uca.tfg.ceramic_affair_web.entities.TipoPago;
import jakarta.validation.constraints.NotNull;

/**
 * DTO para el proceso de checkout.
 * Este DTO se utiliza para transferir datos relacionados con el proceso de pago y finalización de una compra.
 * 
 * @version 1.0
 */
public class CheckoutDTO {

    private Long usuarioId;

    private List<CarritoItemDTO> items;

    @NotNull(message = "El importe total no puede ser nulo")
    private BigDecimal total;

    @NotNull(message = "El tipo de pago es obligatorio")
    private TipoPago tipoPago;

    @NotNull(message = "Los datos del pedido son obligatorios")
    private PedidoDataDTO pedido;

    public CheckoutDTO() {
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public List<CarritoItemDTO> getItems() {
        return items;
    }

    public void setItems(List<CarritoItemDTO> items) {
        this.items = items;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public TipoPago getTipoPago() {
        return tipoPago;
    }

    public void setTipoPago(TipoPago tipoPago) {
        this.tipoPago = tipoPago;
    }

    public PedidoDataDTO getPedido() {
        return pedido;
    }

    public void setPedido(PedidoDataDTO pedido) {
        this.pedido = pedido;
    }
}
