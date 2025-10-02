package es.uca.tfg.ceramic_affair_web.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.uca.tfg.ceramic_affair_web.entities.Carrito;
import es.uca.tfg.ceramic_affair_web.entities.CarritoItem;
import es.uca.tfg.ceramic_affair_web.entities.Producto;
import es.uca.tfg.ceramic_affair_web.entities.Usuario;
import es.uca.tfg.ceramic_affair_web.exceptions.AuthException;
import es.uca.tfg.ceramic_affair_web.exceptions.CarritoException;
import es.uca.tfg.ceramic_affair_web.exceptions.ProductoException;
import es.uca.tfg.ceramic_affair_web.repositories.CarritoRepo;
import es.uca.tfg.ceramic_affair_web.repositories.ProductoRepo;
import es.uca.tfg.ceramic_affair_web.repositories.UsuarioRepo;

/**
 * Servicio para gestionar operaciones relacionadas con el carrito de compras.
 * 
 * @version 1.0
 */
@Service
public class CarritoService {

    @Autowired
    private CarritoRepo carritoRepo;

    @Autowired
    private ProductoRepo productoRepo;

    @Autowired
    private UsuarioRepo usuarioRepo;

    /**
     * Método para obtener el carrito de un usuario.
     * 
     * @param usuarioId el ID del usuario
     * @return el carrito del usuario, o vacío si no tiene carrito
     * @throws AuthException.UsuarioNoEncontrado si el usuario no existe
     */
    @Transactional(readOnly = true)
    public Optional<Carrito> getCarrito(Long usuarioId) {
        Usuario usuario = usuarioRepo.findById(usuarioId)
            .orElseThrow(() -> new AuthException.UsuarioNoEncontrado());

        return carritoRepo.findByUsuario(usuario);
    }

    /**
     * Método para añadir un producto al carrito de un usuario.
     * 
     * @param usuarioId el ID del usuario
     * @param productoId el ID del producto a añadir
     * @throws AuthException.UsuarioNoEncontrado si el usuario no existe
     * @throws ProductoException.NoEncontrado si el producto no existe
     * @throws CarritoException.ItemDuplicado si el producto ya está en el carrito
     * @throws ProductoException.SoldOut si el producto está agotado
     */
    @Transactional
    public Carrito addProducto(Long usuarioId, Long productoId) {
        // 1. Buscar al usuario y el producto en la base de datos
        Usuario usuario = usuarioRepo.findById(usuarioId)
            .orElseThrow(() -> new AuthException.UsuarioNoEncontrado());

        Producto producto = productoRepo.findByIdAndActivoTrue(productoId)
            .orElseThrow(() -> new ProductoException.NoEncontrado(productoId));

        // 2. Obtener/crear el carrito del usuario
        Carrito carrito = carritoRepo.findByUsuario(usuario)
            .orElseGet(() -> carritoRepo.save(new Carrito(usuario)));

        // 3. Añadir producto al carrito, comprobando que no esté ya y que está en stock
        boolean itemExistente = carrito.getItems().stream()
            .anyMatch(item -> item.getProducto().getId().equals(productoId));

        if (itemExistente) {
            throw new CarritoException.ItemDuplicado();
        }

        if (producto.isSoldOut()) {
            throw new ProductoException.SoldOut(productoId);
        }

        carrito.addItem(new CarritoItem(carrito, producto));

        // 4. Guardar los cambios en la base de datos
        return carritoRepo.save(carrito);
    }

    /**
     * Método para eliminar un producto del carrito de un usuario.
     * 
     * @param usuarioId el ID del usuario
     * @param productoId el ID del producto a eliminar
     * @throws AuthException.UsuarioNoEncontrado si el usuario no existe
     */
    @Transactional
    public Optional<Carrito> removeProducto(Long usuarioId, Long productoId) {
        // 1. Buscar al usuario en la base de datos
        Usuario usuario = usuarioRepo.findById(usuarioId)
            .orElseThrow(() -> new AuthException.UsuarioNoEncontrado());

        // 2. Obtener el carrito del usuario (si no tiene carrito, retornar vacío)
        Carrito carrito = carritoRepo.findByUsuario(usuario)
            .orElse(null);
        if (carrito == null) {
            return Optional.empty();
        }

        // 3. Eliminar el producto del carrito (si no está, retornar el carrito sin cambios)
        CarritoItem itemAEliminar = carrito.getItems().stream()
            .filter(item -> item.getProducto().getId().equals(productoId))
            .findFirst()
            .orElse(null);
        if (itemAEliminar == null) {
            return Optional.of(carrito);
        }

        carrito.removeItem(itemAEliminar);

        // 4. Si el carrito queda vacío, eliminarlo; si no, guardar los cambios
        if (carrito.getItems().isEmpty()) {
            carritoRepo.delete(carrito);
            return Optional.empty();
        } else {
            return Optional.of(carritoRepo.save(carrito));
        }
    }

    /**
     * Método para vaciar el carrito de un usuario.
     * 
     * @param usuarioId el ID del usuario
     * @throws AuthException.UsuarioNoEncontrado si el usuario no existe
     */
    @Transactional
    public void clearCarrito(Long usuarioId) {
        // 1. Buscar al usuario en la base de datos
        Usuario usuario = usuarioRepo.findById(usuarioId)
            .orElseThrow(() -> new AuthException.UsuarioNoEncontrado());

        // 2. Obtener el carrito del usuario
        Carrito carrito = carritoRepo.findByUsuario(usuario)
            .orElse(null);

        // 3. Si el carrito existe, eliminarlo
        if (carrito != null) {
            carritoRepo.delete(carrito);
        }
    }
}
