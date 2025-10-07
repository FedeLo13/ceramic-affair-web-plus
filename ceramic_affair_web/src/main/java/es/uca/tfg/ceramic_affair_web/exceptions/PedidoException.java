package es.uca.tfg.ceramic_affair_web.exceptions;

import org.springframework.http.HttpStatus;

/**
 * Clase para manejar excepciones relacionadas con los pedidos.
 */
public class PedidoException {

    /**
     * Constructor privado para evitar la instanciación de esta clase.
     */
    private PedidoException() {
        throw new UnsupportedOperationException("This class cannot be instantiated.");
    }

    /**
     * Excepción lanzada cuando se intenta acceder a un pedido que no existe.
     */
    public static class NoEncontrado extends BusinessException {
        
        /**
         * Constructor de la excepción.
         */
        public NoEncontrado() {
            super("Order not found", HttpStatus.NOT_FOUND);
        }

        /**
         * Constructor sobrecargado para mensajes personalizados.
         * @param mensaje
         */
        public NoEncontrado(String mensaje) {
            super(mensaje, HttpStatus.NOT_FOUND);
        }
    }
}
