package es.uca.tfg.ceramic_affair_web.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import es.uca.tfg.ceramic_affair_web.entities.TipoPago;
import es.uca.tfg.ceramic_affair_web.exceptions.PagoException;

/**
 * Clase de prueba para la fábrica de validadores de pago.
 * Proporciona pruebas unitarias para la creación de validadores de pago.
 * 
 * @version 1.0
 */
public class PagoValidatorFactoryTest {

    @Test
    @DisplayName("Fábrica - Crear validador de pago")
    void testCrearValidadorPago() {
        PagoValidator bizum = new BizumPagoValidator();
        PagoValidator tarjeta = new TarjetaPagoValidator();
        PagoValidatorFactory factory = new PagoValidatorFactory(List.of(bizum, tarjeta));

        assertEquals(bizum, factory.getValidator(TipoPago.BIZUM));
        assertEquals(tarjeta, factory.getValidator(TipoPago.TARJETA));
    }

    @Test
    @DisplayName("Fábrica - Crear validador de pago no existente")
    void testCrearValidadorPagoNoExistente() {
        PagoValidatorFactory factory = new PagoValidatorFactory(List.of(new BizumPagoValidator()));

        assertThrows(PagoException.MetodoPagoNoEncontrado.class, () -> {
            factory.getValidator(TipoPago.TARJETA);
        });
    }
}
