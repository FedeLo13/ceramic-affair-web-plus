package es.uca.tfg.ceramic_affair_web.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;

/**
 * Clase que representa un pago.
 * 
 * @version 1.0
 */
@Entity
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = true)
    @JoinColumn(name = "usuario_id", nullable = true)
    private Usuario usuario;

    @OneToOne(mappedBy = "pago", cascade = CascadeType.ALL, optional = false)
    private Pedido pedido;

    private BigDecimal importe = BigDecimal.ZERO;

    @CreationTimestamp
    private LocalDateTime fechaCreacion;

    private TipoPago tipoPago;

    /**
     * Constructor vacío para JPA
     */
    public Pago() {
    }

    /**
     * Constructor con parámetros para crear un pago.
     * 
     * @param usuario   el usuario que realiza el pago
     * @param pedido    el pedido asociado al pago
     * @param importe   el importe del pago
     * @param tipoPago  el tipo de pago (BIZUM o TARJETA)
     */
    public Pago(Usuario usuario, Pedido pedido, BigDecimal importe, TipoPago tipoPago) {
        this.usuario = usuario;
        this.pedido = pedido;
        this.importe = importe;
        this.tipoPago = tipoPago;
    }

    /**
     * Método para obtener el ID del pago.
     * 
     * @return el ID del pago
     */
    public Long getId() {
        return id;
    }

    /**
     * Método para obtener el usuario que realizó el pago.
     * 
     * @return el usuario que realizó el pago
     */
    public Usuario getUsuario() {
        return usuario;
    }

    /**
     * Método para obtener el pedido asociado al pago.
     * 
     * @return el pedido asociado al pago
     */
    public Pedido getPedido() {
        return pedido;
    }

    /**
     * Método para obtener el importe del pago.
     * 
     * @return el importe del pago
     */
    public BigDecimal getImporte() {
        return importe;
    }

    /**
     * Método para obtener la fecha de creación del pago.
     * 
     * @return la fecha de creación del pago
     */
    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    /**
     * Método para obtener el tipo de pago.
     * 
     * @return el tipo de pago (BIZUM o TARJETA)
     */
    public TipoPago getTipoPago() {
        return tipoPago;
    }

    /**
     * Método para establecer el ID del pago.
     * 
     * @param id el nuevo ID del pago
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Método para establecer el usuario que realizó el pago.
     * 
     * @param usuario el nuevo usuario que realizó el pago
     */
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    /**
     * Método para establecer el pedido asociado al pago.
     * 
     * @param pedido el nuevo pedido asociado al pago
     */
    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    /**
     * Método para establecer el importe del pago.
     * 
     * @param importe el nuevo importe del pago
     */
    public void setImporte(BigDecimal importe) {
        this.importe = importe;
    }

    /**
     * Método para establecer el tipo de pago.
     * 
     * @param tipoPago el nuevo tipo de pago (BIZUM o TARJETA)
     */
    public void setTipoPago(TipoPago tipoPago) {
        this.tipoPago = tipoPago;
    }
}
