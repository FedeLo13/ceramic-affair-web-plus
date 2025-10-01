package es.uca.tfg.ceramic_affair_web.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;

/**
 * Clase que representa un carrito de compras.
 * 
 * @version 1.0
 */
@Entity
public class Carrito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @OneToMany(mappedBy = "carrito", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CarritoItem> items = new ArrayList<>();

    private BigDecimal total = BigDecimal.ZERO;

    @CreationTimestamp
    private LocalDateTime fechaCreacion;

    /**
     * Constructor vacío para JPA
     */
    public Carrito() {
    }

    /**
     * Constructor con parámetros para crear un carrito.
     * 
     * @param usuario el usuario asociado al carrito
     */
    public Carrito(Usuario usuario) {
        this.usuario = usuario;
    }

    /**
     * Método para obtener el ID del carrito.
     * 
     * @return el ID del carrito
     */
    public Long getId() {
        return id;
    }

    /**
     * Método para obtener el usuario asociado al carrito.
     * 
     * @return el usuario asociado al carrito
     */
    public Usuario getUsuario() {
        return usuario;
    }

    /**
     * Método para obtener los ítems del carrito.
     * 
     * @return la lista de ítems del carrito
     */
    public List<CarritoItem> getItems() {
        return items;
    }

    /**
     * Método para obtener el total del carrito.
     * 
     * @return el total del carrito
     */
    public BigDecimal getTotal() {
        return total;
    }

    /**
     * Método para obtener la fecha de creación del carrito.
     * 
     * @return la fecha de creación del carrito
     */
    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    /**
     * Método para establecer el id del carrito.
     * 
     * @param id el nuevo id del carrito
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Método para establecer el usuario asociado al carrito.
     * 
     * @param usuario el nuevo usuario asociado al carrito
     */
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    /**
     * Método para añadir un ítem al carrito.
     * 
     * @param item el ítem a añadir
     */
    public void addItem(CarritoItem item) {
        items.add(item);
        item.setCarrito(this);
        recalcularTotal();
    }

    /**
     * Método para eliminar un ítem del carrito.
     * 
     * @param item el ítem a eliminar
     */
    public void removeItem(CarritoItem item) {
        items.remove(item);
        item.setCarrito(null);
        recalcularTotal();
    }

    /**
     * Método para establecer el total del carrito.
     * 
     * @param total el nuevo total del carrito
     */
    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    /**
     * Método para establecer la fecha de creación del carrito.
     * 
     * @param fechaCreacion la nueva fecha de creación del carrito
     */
    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    /**
     * Método para recalcular el total del carrito.
     */
    private void recalcularTotal() {
        total = items.stream()
                .map(CarritoItem::getPrecioUnitario)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
