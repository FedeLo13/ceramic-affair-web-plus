package es.uca.tfg.ceramic_affair_web.services;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Clase de prueba para el validador de pagos con tarjeta.
 * Actualmente las pruebas son triviales ya que la validación real no está implementada.
 * 
 * @version 1.0
 */
public class TarjetaPagoValidatorTest {

    private final TarjetaPagoValidator validator = new TarjetaPagoValidator();

    @Test
    @DisplayName("Validador - Pago con tarjeta positivo")
    public void testValidarPagoConTarjetaPositivo() {
        assertTrue(validator.validar(BigDecimal.TEN));
        assertFalse(validator.validar(BigDecimal.ZERO));
        assertFalse(validator.validar(BigDecimal.valueOf(-5)));
    }
}
