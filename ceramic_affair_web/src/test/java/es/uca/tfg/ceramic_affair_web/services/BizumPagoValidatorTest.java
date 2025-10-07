package es.uca.tfg.ceramic_affair_web.services;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Clase de prueba para el validador de pagos con Bizum.
 * Actualmente las pruebas son triviales ya que la validación real no está implementada.
 * 
 * @version 1.0
 */
public class BizumPagoValidatorTest {

    private final BizumPagoValidator validator = new BizumPagoValidator();

    @Test
    @DisplayName("Validador - Pago con Bizum positivo")
    public void testValidarPagoConBizumPositivo() {
        assertTrue(validator.validar(java.math.BigDecimal.TEN));
        assertFalse(validator.validar(java.math.BigDecimal.ZERO));
        assertFalse(validator.validar(java.math.BigDecimal.valueOf(-5)));
    }
}
