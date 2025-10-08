package es.uca.tfg.ceramic_affair_web.controllers.common;

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
 * Controlador para los endpoints públicos relacionados con la entidad Pago.
 * Proporciona endpoints para gestionar pagos.
 * 
 * @version 1.0
 */
@RestController
@RequestMapping("/api/public/pagos")
@Tag(name = "Pagos Public", description = "Controlador para la gestión de pagos")
public class PagoPublicController {

    @Autowired
    private PagoService pagoService;

    @GetMapping("/{pedidoId}")
    @Operation(summary = "Obtener un pago por el id del pedido", description = "Devuelve el pago correspondiente al ID del pedido proporcionado", tags = { "Pagos Public" })
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pago encontrado"),
        @ApiResponse(responseCode = "404", description = "Pedido no encontrado o Pago no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<ApiResponseType<Pago>> obtenerPagoPorPedido(@PathVariable Long pedidoId) {
        Pago pago = pagoService.getPagoByPedidoId(pedidoId);
        return ResponseEntity.ok(new ApiResponseType<>(true, "Pago encontrado", pago));
    }
}
