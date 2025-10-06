package es.uca.tfg.ceramic_affair_web.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import es.uca.tfg.ceramic_affair_web.entities.Pago;

/**
 * Repositorio para la entidad Pago.
 * Proporciona métodos para realizar operaciones CRUD en la base de datos.
 * 
 * @version 1.0
 */
@Repository
public interface PagoRepo extends JpaRepository<Pago, Long> {

    /**
     * Método para obtener los pagos de un usuario específico.
     * 
     * @param usuarioId el ID del usuario
     * @return la lista de pagos del usuario
     */
    List<Pago> findByUsuarioId(Long usuarioId);

    /**
     * Método para obtener el pago de un pedido específico.
     * 
     * @param pedidoId el ID del pedido
     * @return el pago asociado al pedido, o vacío si no existe
     */
    Optional<Pago> findByPedidoId(Long pedidoId);

}
