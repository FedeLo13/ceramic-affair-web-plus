package es.uca.tfg.ceramic_affair_web.entities;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;

/**
 * Clase que representa un ítem en un pedido.
 * 
 * @version 1.0
 */
@Entity
public class PedidoItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Column(nullable = false)
    private BigDecimal precioUnitario = BigDecimal.valueOf(0.0);

    /**
     * Constructor vacío para JPA
     */
    public PedidoItem() {
    }

    /**
     * Constructor con parámetros para crear un ítem de pedido.
     * 
     * @param pedido       el pedido al que pertenece el ítem
     * @param producto      el producto asociado al ítem
     */
    public PedidoItem(Pedido pedido, Producto producto) {
        this.pedido = pedido;
        this.producto = producto;
        this.precioUnitario = producto.getPrecio();
    }

    /**
     * Método para obtener el ID del ítem del pedido.
     * 
     * @return el ID del ítem del pedido
     */
    public Long getId() {
        return id;
    }

    /**
     * Método para obtener el pedido asociado al ítem.
     * 
     * @return el pedido asociado al ítem
     */
    public Pedido getPedido() {
        return pedido;
    }

    /**
     * Método para obtener el producto asociado al ítem.
     * 
     * @return el producto asociado al ítem
     */
    public Producto getProducto() {
        return producto;
    }

    /**
     * Método para obtener el precio unitario del ítem.
     * 
     * @return el precio unitario del ítem
     */
    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    /**
     * Método para establecer el id del ítem del pedido.
     * 
     * @param id el nuevo id del ítem del pedido
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Método para establecer el pedido asociado al ítem.
     * 
     * @param pedido el nuevo pedido asociado al ítem
     */
    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    /**
     * Método para establecer el producto asociado al ítem.
     * 
     * @param producto el nuevo producto asociado al ítem
     */
    public void setProducto(Producto producto) {
        this.producto = producto;
        this.precioUnitario = producto.getPrecio();
    }

    /**
     * Método para establecer el precio unitario del ítem.
     * 
     * @param precioUnitario el nuevo precio unitario del ítem
     */
    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }
}
