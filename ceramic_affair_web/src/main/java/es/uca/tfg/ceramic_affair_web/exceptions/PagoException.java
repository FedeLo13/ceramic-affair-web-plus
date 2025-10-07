package es.uca.tfg.ceramic_affair_web.exceptions;

import org.springframework.http.HttpStatus;

/**
 * Clase para manejar excepciones relacionadas con los pagos.
 */
public class PagoException {

    /**
     * Constructor privado para evitar la instanciación de esta clase.
     */
    private PagoException() {
        throw new UnsupportedOperationException("This class cannot be instantiated.");
    }

    /**
     * Excepción lanzada cuando el pago no es válido (por ejemplo, importe incorrecto).
     */
    public static class PagoInvalido extends BusinessException {
        
        /**
         * Constructor de la excepción.
         */
        public PagoInvalido() {
            super("An error occurred during payment processing", HttpStatus.BAD_REQUEST);
        }

        /**
         * Constructor sobrecargado para mensajes personalizados.
         * @param mensaje
         */
        public PagoInvalido(String mensaje) {
            super(mensaje, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Excepción lanzada cuando el pago no se ha encontrado.
     */
    public static class NoEncontrado extends BusinessException {
        
        /**
         * Constructor de la excepción.
         */
        public NoEncontrado() {
            super("Payment not found", HttpStatus.NOT_FOUND);
        }

        /**
         * Constructor sobrecargado para mensajes personalizados.
         * @param mensaje
         */
        public NoEncontrado(String mensaje) {
            super(mensaje, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Excepción lanzada cuando no se encuentra el método de pago.
     */
    public static class MetodoPagoNoEncontrado extends BusinessException {
        
        /**
         * Constructor de la excepción.
         */
        public MetodoPagoNoEncontrado() {
            super("Payment method not found", HttpStatus.NOT_FOUND);
        }

        /**
         * Constructor sobrecargado para mensajes personalizados.
         * @param mensaje
         */
        public MetodoPagoNoEncontrado(String mensaje) {
            super(mensaje, HttpStatus.NOT_FOUND);
        }
    }
}
