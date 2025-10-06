package es.uca.tfg.ceramic_affair_web.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import es.uca.tfg.ceramic_affair_web.entities.Producto;
import jakarta.persistence.LockModeType;


/**
 * Repositorio para la entidad Producto.
 * Proporciona métodos para realizar operaciones CRUD en la base de datos.
 * 
 * @version 1.1
 */
@Repository
public interface ProductoRepo extends JpaRepository<Producto, Long>, JpaSpecificationExecutor<Producto> {
    /**
     * Método para obtener todos los prductos activos
     * 
     * @return una lista de productos activos
     */
    List<Producto> findByActivoTrue();

    /**
     * Método para buscar productos por id y activo
     * 
     * @param id el ID del producto
     * @return un Optional que contiene el producto si se encuentra y está activo, o vacío si no
     */
    Optional<Producto> findByIdAndActivoTrue(Long id);

    /**
     * Método para buscar y bloquear un producto por su ID.
     * 
     * @param id el ID del producto
     * @return un Optional que contiene el producto si se encuentra, o vacío si no
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Producto p WHERE p.id = :id")
    Optional<Producto> findAndLockById(@Param("id") Long id);
}
