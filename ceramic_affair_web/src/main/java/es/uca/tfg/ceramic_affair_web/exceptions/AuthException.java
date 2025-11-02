package es.uca.tfg.ceramic_affair_web.exceptions;

import org.springframework.http.HttpStatus;

/**
 * Clase para manejar excepciones relacionadas con la autenticación.
 */
public class AuthException {

    /**
     * Constructor privado para evitar la instanciación de esta clase.
     */
    private AuthException() {
        throw new UnsupportedOperationException("This class cannot be instantiated.");
    }

    /**
     * Excepción lanzada cuando las credenciales son inválidas.
     */
    public static class CredencialesInvalidas extends BusinessException {
        /**
         * Constructor de la excepción.
         */
        public CredencialesInvalidas() {
            super("Invalid credentials", HttpStatus.UNAUTHORIZED);
        }

        /**
         * Constructor overloaded para mensajes personalizados.
         * @param mensaje
         */
        public CredencialesInvalidas(String mensaje) {
            super(mensaje, HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * Excepción lanzada cuando ya existe un usuario con el mismo email.
     */
    public static class EmailYaRegistrado extends BusinessException {
        /**
         * Constructor de la excepción.
         */
        public EmailYaRegistrado() {
            super("Email already registered", HttpStatus.CONFLICT);
        }

        /**
         * Constructor overloaded para mensajes personalizados.
         * @param mensaje
         */
        public EmailYaRegistrado(String mensaje) {
            super(mensaje, HttpStatus.CONFLICT);
        }
    }

    /**
     * Excepción lanzada cuando el usuario no ha verificado su cuenta.
     */
    public static class VerificacionPendiente extends BusinessException {
        /**
         * Constructor de la excepción.
         */
        public VerificacionPendiente() {
            super("Email verification pending", HttpStatus.BAD_REQUEST);
        }

        /**
         * Constructor overloaded para mensajes personalizados.
         * @param mensaje
         */
        public VerificacionPendiente(String mensaje) {
            super(mensaje, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Excepción lanzada cuando no se encuentra el usuario.
     */
    public static class UsuarioNoEncontrado extends BusinessException {
        /**
         * Constructor de la excepción.
         */
        public UsuarioNoEncontrado() {
            super("User not found", HttpStatus.NOT_FOUND);
        }

        /**
         * Constructor overloaded para mensajes personalizados.
         * @param mensaje
         */
        public UsuarioNoEncontrado(String mensaje) {
            super(mensaje, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Excepción lanzada cuando el token de verificación ha expirado.
     */
    public static class TokenExpirado extends BusinessException {
        /**
         * Constructor de la excepción.
         */
        public TokenExpirado() {
            super("Verification token has expired", HttpStatus.GONE);
        }

        /**
         * Constructor overloaded para mensajes personalizados.
         * @param mensaje
         */
        public TokenExpirado(String mensaje) {
            super(mensaje, HttpStatus.GONE);
        }
    }

    /**
     * Excepción específica lanzada cuando la contraseña antigua es incorrecta a la hora de cambiar la contraseña.
     */
    public static class AntiguaPasswordInvalida extends BusinessException {
        /**
         * Constructor de la excepción.
         */
        public AntiguaPasswordInvalida() {
            super("Old password is incorrect", HttpStatus.FORBIDDEN);
        }

        /**
         * Constructor overloaded para mensajes personalizados.
         * @param mensaje
         */
        public AntiguaPasswordInvalida(String mensaje) {
            super(mensaje, HttpStatus.FORBIDDEN);
        }
    }
}
