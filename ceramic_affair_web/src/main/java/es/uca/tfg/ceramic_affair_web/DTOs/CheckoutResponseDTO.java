package es.uca.tfg.ceramic_affair_web.DTOs;

import java.math.BigDecimal;

/**
 * DTO para la respuesta del proceso de checkout.
 * 
 * @version 1.0
 */
public class CheckoutResponseDTO {

    private Long pedidoId;

    private String estado;

    private BigDecimal total;

    public CheckoutResponseDTO(Long pedidoId, String estado, BigDecimal total) {
        this.pedidoId = pedidoId;
        this.estado = estado;
        this.total = total;
    }

    public Long getPedidoId() {
        return pedidoId;
    }

    public String getEstado() {
        return estado;
    }

    public BigDecimal getTotal() {
        return total;
    }
}
