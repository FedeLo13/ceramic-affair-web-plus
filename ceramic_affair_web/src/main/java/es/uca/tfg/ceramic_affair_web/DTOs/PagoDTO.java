package es.uca.tfg.ceramic_affair_web.DTOs;

import java.math.BigDecimal;

import es.uca.tfg.ceramic_affair_web.entities.TipoPago;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

/**
 * DTO para la entidad Pago.
 * Este DTO se utiliza para transferir datos de pagos entre la capa de presentación y la capa de servicio.
 * 
 * @version 1.0
 */
public class PagoDTO {

    private Long id;

    private Long usuarioId;

    private Long pedidoId;

    @NotNull(message = "El importe no puede ser nulo")
    @PositiveOrZero(message = "El importe no puede ser negativo")
    private BigDecimal importe;

    @NotNull(message = "El tipo de pago es obligatorio")
    private TipoPago tipoPago;

    public PagoDTO() {
    }

    public PagoDTO(Long id, Long usuarioId, Long pedidoId, BigDecimal importe, TipoPago tipoPago) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.pedidoId = pedidoId;
        this.importe = importe;
        this.tipoPago = tipoPago;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Long getPedidoId() {
        return pedidoId;
    }

    public void setPedidoId(Long pedidoId) {
        this.pedidoId = pedidoId;
    }

    public BigDecimal getImporte() {
        return importe;
    }

    public void setImporte(BigDecimal importe) {
        this.importe = importe;
    }

    public TipoPago getTipoPago() {
        return tipoPago;
    }

    public void setTipoPago(TipoPago tipoPago) {
        this.tipoPago = tipoPago;
    }
}
