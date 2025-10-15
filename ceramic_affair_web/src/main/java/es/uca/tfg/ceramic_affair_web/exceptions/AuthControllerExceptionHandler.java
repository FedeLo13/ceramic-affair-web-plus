package es.uca.tfg.ceramic_affair_web.exceptions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import es.uca.tfg.ceramic_affair_web.controllers.common.AuthController;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Clase para manejar algunas excepciones específicas del AuthController.
 * 
 * @version 1.0
 */
@ControllerAdvice(assignableTypes = AuthController.class)
@Order(1)
public class AuthControllerExceptionHandler {

    @Autowired
    private GlobalExceptionHandler globalExceptionHandler;

    @ExceptionHandler({AuthException.UsuarioNoEncontrado.class, AuthException.TokenExpirado.class})
    public ResponseEntity<?> handleAuthExceptions(Exception ex, HttpServletRequest request) {
        String path = request.getRequestURI();

        // Si viene del enlace de verificación, redirigir al frontend
        if (path.contains("/verificar")) {
            String status = (ex instanceof AuthException.UsuarioNoEncontrado)
                ? "user_not_found"
                : "token_expired";
            return redirectToFrontend(status);
        }

        // Si no viene del endpoint de verificación, dejar que el manejador global lo procese
        if(ex instanceof BusinessException bex) {
            return globalExceptionHandler.handleBusinessException(bex, request);
        }

        // Si no fuera BusinessException (esto no debería ocurrir), relanzar
        throw new RuntimeException(ex);
    }

    private ResponseEntity<Void> redirectToFrontend(String status) {
        String frontendUrl = "http://localhost:5173/confirmation?status=" + status;
        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", frontendUrl)
                .build();
    }
}
