package es.uca.tfg.ceramic_affair_web.services;

import java.util.List;

import org.springframework.stereotype.Component;

import es.uca.tfg.ceramic_affair_web.entities.TipoPago;
import es.uca.tfg.ceramic_affair_web.exceptions.PagoException;

/**
 * Fábrica para obtener validadores de pago según el tipo de pago.
 * 
 * @version 1.0
 */
@Component
public class PagoValidatorFactory {

    private final List<PagoValidator> validators;

    public PagoValidatorFactory(List<PagoValidator> validators) {
        this.validators = validators;
    }

    public PagoValidator getValidator(TipoPago tipo) {
        return validators.stream()
                .filter(v -> v.getTipo() == tipo)
                .findFirst()
                .orElseThrow(() -> new PagoException.MetodoPagoNoEncontrado());
    }
}
