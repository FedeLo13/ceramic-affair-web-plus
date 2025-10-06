package es.uca.tfg.ceramic_affair_web.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import es.uca.tfg.ceramic_affair_web.entities.Pedido;

/**
 * Repositorio para la entidad Pedido.
 * Proporciona métodos para realizar operaciones CRUD en la base de datos.
 * 
 * @version 1.0
 */
@Repository
public interface PedidoRepo extends JpaRepository<Pedido, Long> {

    /**
     * Método para obtener los pedidos de un usuario específico.
     * 
     * @param usuarioId el ID del usuario
     * @return la lista de pedidos del usuario
     */
    List<Pedido> findByUsuarioId(Long usuarioId);

    /**
     * Método para obtener todos los pedidos enviados ordenados por fecha de creación descendente.
     * 
     * @return la lista de pedidos enviados
     */
    List<Pedido> findByEnviadoTrueOrderByFechaCreacionDesc();

    /**
     * Método para obtener todos los pedidos no enviados ordenados por fecha de creación descendente.
     * 
     * @return la lista de pedidos no enviados
     */
    List<Pedido> findByEnviadoFalseOrderByFechaCreacionDesc();
}
