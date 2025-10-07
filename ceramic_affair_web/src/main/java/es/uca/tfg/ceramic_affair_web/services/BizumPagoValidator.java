package es.uca.tfg.ceramic_affair_web.services;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import es.uca.tfg.ceramic_affair_web.entities.TipoPago;

/**
 * Clase para validar pagos realizados mediante Bizum.
 * Actualmente no implementa realmente la validación por Bizum.
 * 
 * @version 1.0
 */
@Service
public class BizumPagoValidator implements PagoValidator {

    @Override
    public TipoPago getTipo() {
        return TipoPago.BIZUM;
    }

    @Override
    public boolean validar(BigDecimal importe) {
        // Aquí se implementaría la lógica real de validación con Bizum.
        // Por ahora, simplemente devolvemos true si el importe es positivo.
        return importe.compareTo(BigDecimal.ZERO) > 0;
    }

}
