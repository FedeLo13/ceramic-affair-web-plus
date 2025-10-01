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

/**
 * Clase que representa un ítem en el carrito de compras.
 * 
 * @version 1.0
 */
@Entity
public class CarritoItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carrito_id", nullable = false)
    private Carrito carrito;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Column(nullable = false)
    private BigDecimal precioUnitario;

    /**
     * Constructor vacío para JPA
     */
    public CarritoItem() {
    }

    /**
     * Constructor con parámetros para crear un ítem de carrito.
     * 
     * @param carrito       el carrito al que pertenece el ítem
     * @param producto      el producto asociado al ítem
     */
    public CarritoItem(Carrito carrito, Producto producto) {
        this.carrito = carrito;
        this.producto = producto;
        this.precioUnitario = producto.getPrecio();
    }

    /**
     * Método para obtener el ID del ítem del carrito.
     * 
     * @return el ID del ítem del carrito
     */
    public Long getId() {
        return id;
    }

    /**
     * Método para obtener el carrito al que pertenece el ítem.
     * 
     * @return el carrito al que pertenece el ítem
     */
    public Carrito getCarrito() {
        return carrito;
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
     * Método para establecer el id del ítem del carrito.
     * 
     * @param id el nuevo id del ítem del carrito
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Método para establecer el carrito al que pertenece el ítem.
     * 
     * @param carrito el nuevo carrito al que pertenece el ítem
     */
    public void setCarrito(Carrito carrito) {
        this.carrito = carrito;
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
