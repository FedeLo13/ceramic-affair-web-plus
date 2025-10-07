package es.uca.tfg.ceramic_affair_web.services;

import java.math.BigDecimal;

import es.uca.tfg.ceramic_affair_web.entities.TipoPago;

/**
 * Clase para validar pagos realizados mediante tarjeta.
 * Actualmente no implementa realmente la validación por tarjeta.
 * 
 * @version 1.0
 */
public class TarjetaPagoValidator implements PagoValidator {

    @Override
    public TipoPago getTipo() {
        return TipoPago.TARJETA;
    }

    @Override
    public boolean validar(BigDecimal importe) {
        // Aquí se implementaría la lógica real de validación con tarjeta.
        // Por ahora, simplemente devolvemos true si el importe es positivo.
        return importe.compareTo(BigDecimal.ZERO) > 0;
    }

}
