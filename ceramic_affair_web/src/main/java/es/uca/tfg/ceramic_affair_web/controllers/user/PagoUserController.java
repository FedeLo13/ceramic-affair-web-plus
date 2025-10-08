package es.uca.tfg.ceramic_affair_web.controllers.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.uca.tfg.ceramic_affair_web.entities.Pago;
import es.uca.tfg.ceramic_affair_web.payload.ApiResponseType;
import es.uca.tfg.ceramic_affair_web.services.PagoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controlador para gestionar las operaciones de pago de los usuarios.
 * 
 * @version 1.0
 */
@RestController
@RequestMapping("/api/user/pagos")
@Tag(name = "Pago User Controller", description = "Endpoints for user payment operations")
public class PagoUserController {

    @Autowired
    private PagoService pagoService;

    @GetMapping("{userId}")
    @Operation(summary = "Obtener pagos por usuario", description = "Devuelve una lista de pagos realizados por el usuario autenticado", tags = { "Pago User Controller" })
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de pagos obtenida"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<ApiResponseType<List<Pago>>> getPagosByUsuario(@PathVariable Long userId) {
        List<Pago> pagos = pagoService.getPagosByUsuarioId(userId);
        return ResponseEntity.ok(new ApiResponseType<>(true, "Lista de pagos obtenida", pagos));
    }
}
