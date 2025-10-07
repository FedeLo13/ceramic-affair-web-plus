package es.uca.tfg.ceramic_affair_web.services;

import java.math.BigDecimal;

import es.uca.tfg.ceramic_affair_web.entities.TipoPago;

/**
 * Interfaz para validar los pagos.
 * 
 * @version 1.0
 */
public interface PagoValidator {
    TipoPago getTipo();
    boolean validar(BigDecimal importe);
}
