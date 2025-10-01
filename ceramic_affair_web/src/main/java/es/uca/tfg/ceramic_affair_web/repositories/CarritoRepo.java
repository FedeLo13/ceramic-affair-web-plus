package es.uca.tfg.ceramic_affair_web.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import es.uca.tfg.ceramic_affair_web.entities.Carrito;
import es.uca.tfg.ceramic_affair_web.entities.Usuario;

/**
 * Repositorio para la entidad Carrito.
 * Proporciona métodos para realizar operaciones CRUD en la base de datos.
 * 
 * @version 1.0
 */
@Repository
public interface CarritoRepo extends JpaRepository<Carrito, Long> {
    
    /**
     * Método para encontrar un carrito a partir del usuario asociado.
     * 
     * @param usuario el usuario
     * @return el carrito asociado al usuario, o null si no existe
     */
    Optional<Carrito> findByUsuario(Usuario usuario);
}