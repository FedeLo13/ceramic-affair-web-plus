package es.uca.tfg.ceramic_affair_web.DTOs;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

/**
 * DTO para la entidad PedidoItem.
 * Este DTO se utiliza para transferir datos de ítems de pedidos entre la capa de presentación y la capa de servicio.
 * 
 * @version 1.0
 */
public class PedidoItemDTO {

    @NotNull(message = "El producto no puede ser nulo")
    private ProductoDTO producto;

    @NotNull(message = "El precio unitario no puede ser nulo")
    @PositiveOrZero(message = "El precio unitario no puede ser negativo")
    private BigDecimal precioUnitario;

    public PedidoItemDTO() {
        // Constructor por defecto
    }

    public PedidoItemDTO(ProductoDTO producto, BigDecimal precioUnitario) {
        this.producto = producto;
        this.precioUnitario = precioUnitario;
    }

    public ProductoDTO getProducto() {
        return producto;
    }

    public void setProducto(ProductoDTO producto) {
        this.producto = producto;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }
}
