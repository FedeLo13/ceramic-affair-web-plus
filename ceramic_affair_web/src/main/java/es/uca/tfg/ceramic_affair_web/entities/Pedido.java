package es.uca.tfg.ceramic_affair_web.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

/**
 * Clase que representa un pedido.
 * 
 * @version 1.0
 */
@Entity
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = true)
    @JoinColumn(name = "usuario_id", nullable = true)
    private Usuario usuario;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PedidoItem> items = new ArrayList<>();

    private BigDecimal total = BigDecimal.ZERO;

    @CreationTimestamp
    private LocalDateTime fechaCreacion;

    // Datos del cliente
    @Column(nullable = false)
    private String nombreCliente;

    @Column(nullable = false)
    private String apellidosCliente;

    @Column(nullable = false)
    private String emailCliente;

    // Datos de envío
    @Column(nullable = false)
    private String provinciaEnvio;

    @Column(nullable = false)
    private String ciudadEnvio;

    @Column(nullable = false)
    private String codigoPostalEnvio;

    @Column(nullable = false)
    private String direccionEnvio;

    @Column(nullable = false)
    private boolean enviado = false;

    /**
     * Constructor vacío para JPA
     */
    public Pedido() {
    }

    /**
     * Constructor con parámetros para crear un pedido de un usuario registrado con carrito.
     * 
     * @param carrito          el carrito del usuario
     * @param nombreCliente    el nombre del cliente
     * @param apellidosCliente los apellidos del cliente
     * @param emailCliente     el email del cliente
     * @param provinciaEnvio   la provincia de envío
     * @param ciudadEnvio      la ciudad de envío
     * @param codigoPostalEnvio el código postal de envío
     * @param direccionEnvio   la dirección de envío
     */
    public Pedido(Carrito carrito, String nombreCliente, String apellidosCliente, String emailCliente,
            String provinciaEnvio, String ciudadEnvio, String codigoPostalEnvio, String direccionEnvio) {
        this.usuario = carrito.getUsuario();
        this.nombreCliente = nombreCliente;
        this.apellidosCliente = apellidosCliente;
        this.emailCliente = emailCliente;
        this.provinciaEnvio = provinciaEnvio;
        this.ciudadEnvio = ciudadEnvio;
        this.codigoPostalEnvio = codigoPostalEnvio;
        this.direccionEnvio = direccionEnvio;
        for (CarritoItem carritoItem : carrito.getItems()) {
            PedidoItem pedidoItem = new PedidoItem(this, carritoItem.getProducto());
            this.items.add(pedidoItem);
            this.total = this.total.add(pedidoItem.getPrecioUnitario());
        }
    }

    /**
     * Constructor con parámetros para crear un pedido de un usuario no registrado.
     * 
     * @param nombreCliente    el nombre del cliente
     * @param apellidosCliente los apellidos del cliente
     * @param emailCliente     el email del cliente
     * @param provinciaEnvio   la provincia de envío
     * @param ciudadEnvio      la ciudad de envío
     * @param codigoPostalEnvio el código postal de envío
     * @param direccionEnvio   la dirección de envío
     */
    public Pedido(String nombreCliente, String apellidosCliente, String emailCliente,
            String provinciaEnvio, String ciudadEnvio, String codigoPostalEnvio, String direccionEnvio) {
        this.usuario = null; // Usuario no registrado   
        this.nombreCliente = nombreCliente;
        this.apellidosCliente = apellidosCliente;
        this.emailCliente = emailCliente;
        this.provinciaEnvio = provinciaEnvio;
        this.ciudadEnvio = ciudadEnvio;
        this.codigoPostalEnvio = codigoPostalEnvio;
        this.direccionEnvio = direccionEnvio;
    }

    /**
     * Método para obtener el ID del pedido.
     * 
     * @return el ID del pedido
     */
    public Long getId() {
        return id;
    }

    /**
     * Método para obtener el usuario asociado al pedido.
     * 
     * @return el usuario asociado al pedido
     */
    public Usuario getUsuario() {
        return usuario;
    }

    /**
     * Método para obtener los ítems del pedido.
     * 
     * @return la lista de ítems del pedido
     */
    public List<PedidoItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    /**
     * Método para obtener el total del pedido.
     * 
     * @return el total del pedido
     */
    public BigDecimal getTotal() {
        return total;
    }

    /**
     * Método para obtener la fecha de creación del pedido.
     * 
     * @return la fecha de creación del pedido
     */
    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    /**
     * Método para obtener el nombre del cliente.
     * 
     * @return el nombre del cliente
     */
    public String getNombreCliente() {
        return nombreCliente;
    }

    /**
     * Método para obtener los apellidos del cliente.
     * 
     * @return los apellidos del cliente
     */
    public String getApellidosCliente() {
        return apellidosCliente;
    }

    /**
     * Método para obtener el email del cliente.
     * 
     * @return el email del cliente
     */
    public String getEmailCliente() {
        return emailCliente;
    }

    /**
     * Método para obtener la provincia de envío.
     * 
     * @return la provincia de envío
     */
    public String getProvinciaEnvio() {
        return provinciaEnvio;
    }

    /**
     * Método para obtener la ciudad de envío.
     * 
     * @return la ciudad de envío
     */
    public String getCiudadEnvio() {
        return ciudadEnvio;
    }

    /**
     * Método para obtener el código postal de envío.
     * 
     * @return el código postal de envío
     */
    public String getCodigoPostalEnvio() {
        return codigoPostalEnvio;
    }

    /**
     * Método para obtener la dirección de envío.
     * 
     * @return la dirección de envío
     */
    public String getDireccionEnvio() {
        return direccionEnvio;
    }

    /**
     * Método para saber si el pedido ha sido enviado.
     * 
     * @return true si el pedido ha sido enviado, false en caso contrario
     */
    public boolean isEnviado() {
        return enviado;
    }

    /**
     * Método para establecer el id del pedido.
     * 
     * @param id el nuevo id del pedido
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Método para establecer el usuario asociado al pedido.
     * 
     * @param usuario el nuevo usuario asociado al pedido
     */
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    /**
     * Método para añadir un ítem al pedido.
     * 
     * @param item el ítem a añadir
     */
    public void addItem(PedidoItem item) {
        items.add(item);
        item.setPedido(this);
        recalcularTotal();
    }

    /**
     * Método para eliminar un ítem del pedido.
     * 
     * @param item el ítem a eliminar
     */
    public void removeItem(PedidoItem item) {
        items.remove(item);
        item.setPedido(null);
        recalcularTotal();
    }

    /**
     * Método para establecer el total del pedido.
     * 
     * @param total el nuevo total del pedido
     */
    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    /**
     * Método para establecer la fecha de creación del pedido.
     * 
     * @param fechaCreacion la nueva fecha de creación del pedido
     */
    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    /**
     * Método para establecer el nombre del cliente.
     * 
     * @param nombreCliente el nuevo nombre del cliente
     */
    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    /**
     * Método para establecer los apellidos del cliente.
     * 
     * @param apellidosCliente los nuevos apellidos del cliente
     */
    public void setApellidosCliente(String apellidosCliente) {
        this.apellidosCliente = apellidosCliente;
    }

    /**
     * Método para establecer el email del cliente.
     * 
     * @param emailCliente el nuevo email del cliente
     */
    public void setEmailCliente(String emailCliente) {
        this.emailCliente = emailCliente;
    }

    /**
     * Método para establecer la provincia de envío.
     * 
     * @param provinciaEnvio la nueva provincia de envío
     */
    public void setProvinciaEnvio(String provinciaEnvio) {
        this.provinciaEnvio = provinciaEnvio;
    }

    /**
     * Método para establecer la ciudad de envío.
     * 
     * @param ciudadEnvio la nueva ciudad de envío
     */
    public void setCiudadEnvio(String ciudadEnvio) {
        this.ciudadEnvio = ciudadEnvio;
    }

    /**
     * Método para establecer el código postal de envío.
     * 
     * @param codigoPostalEnvio el nuevo código postal de envío
     */
    public void setCodigoPostalEnvio(String codigoPostalEnvio) {
        this.codigoPostalEnvio = codigoPostalEnvio;
    }

    /**
     * Método para establecer la dirección de envío.
     * 
     * @param direccionEnvio la nueva dirección de envío
     */
    public void setDireccionEnvio(String direccionEnvio) {
        this.direccionEnvio = direccionEnvio;
    }

    /**
     * Método para establecer si el pedido ha sido enviado.
     * 
     * @param enviado true si el pedido ha sido enviado, false en caso contrario
     */
    public void setEnviado(boolean enviado) {
        this.enviado = enviado;
    }

    /**
     * Método privado para recalcular el total del pedido.
     */
    private void recalcularTotal() {
        total = items.stream()
                .map(PedidoItem::getPrecioUnitario)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
