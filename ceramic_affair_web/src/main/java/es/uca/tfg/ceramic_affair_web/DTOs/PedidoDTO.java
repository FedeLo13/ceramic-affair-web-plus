package es.uca.tfg.ceramic_affair_web.DTOs;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

/**
 * DTO para la entidad Pedido.
 * Este DTO se utiliza para transferir datos de pedidos entre la capa de presentación y la capa de servicio.
 * 
 * @version 1.0
 */
public class PedidoDTO {

    private Long id;

    @NotNull(message = "La lista de ítems no puede ser nula")
    @NotEmpty(message = "El pedido no puede estar vacío")
    private List<PedidoItemDTO> items;

    @NotNull(message = "El total no puede ser nulo")
    @PositiveOrZero(message = "El total no puede ser negativo")
    private BigDecimal total;

    @NotNull(message = "Los datos del pedido son obligatorios")
    private PedidoDataDTO pedido;

    @NotNull(message = "El estado de envío es obligatorio")
    private Boolean enviado;

    public PedidoDTO() {
    }

    public PedidoDTO(Long id, List<PedidoItemDTO> items, BigDecimal total, PedidoDataDTO pedido, Boolean enviado) {
        this.id = id;
        this.items = items;
        this.total = total;
        this.pedido = pedido;
        this.enviado = enviado;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<PedidoItemDTO> getItems() {
        return items;
    }

    public void setItems(List<PedidoItemDTO> items) {
        this.items = items;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public PedidoDataDTO getPedido() {
        return pedido;
    }

    public void setPedido(PedidoDataDTO pedido) {
        this.pedido = pedido;
    }

    public Boolean getEnviado() {
        return enviado;
    }

    public void setEnviado(Boolean enviado) {
        this.enviado = enviado;
    }
}
