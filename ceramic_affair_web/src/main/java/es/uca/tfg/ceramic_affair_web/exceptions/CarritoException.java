package es.uca.tfg.ceramic_affair_web.exceptions;

import org.springframework.http.HttpStatus;

/**
 * Clase para manejar excepciones relacionadas con el carrito de compras.
 */
public class CarritoException {

    /**
     * Constructor privado para evitar la instanciación de esta clase.
     */
    private CarritoException() {
        throw new UnsupportedOperationException("This class cannot be instantiated.");
    }

    /**
     * Excepción lanzada cuando se intenta añadir un producto que ya está en el carrito.
     */
    public static class ItemDuplicado extends BusinessException {
        /**
         * Constructor de la excepción.
         */
        public ItemDuplicado() {
            super("Product already in cart", HttpStatus.BAD_REQUEST);
        }

        /**
         * Constructor sobrecargado para mensajes personalizados.
         * @param mensaje
         */
        public ItemDuplicado(String mensaje) {
            super(mensaje, HttpStatus.BAD_REQUEST);
        }
    }
}
