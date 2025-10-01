package es.uca.tfg.ceramic_affair_web.DTOs;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

/**
 * DTO para la entidad Carrito.
 * Este DTO se utiliza para transferir datos de carritos entre la capa de presentación y la capa de servicio.
 * 
 * @version 1.0
 */
public class CarritoDTO {

    @NotNull(message = "La lista de ítems no puede ser nula")
    @NotEmpty(message = "El carrito no puede estar vacío")
    private List<CarritoItemDTO> items;

    @NotNull(message = "El total no puede ser nulo")
    @PositiveOrZero(message = "El total no puede ser negativo")
    private BigDecimal total;

    public CarritoDTO() {
        // Constructor por defecto
    }

    public CarritoDTO(List<CarritoItemDTO> items, BigDecimal total) {
        this.items = items;
        this.total = total;
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
}
