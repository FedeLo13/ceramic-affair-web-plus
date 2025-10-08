package es.uca.tfg.ceramic_affair_web.controllers.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.uca.tfg.ceramic_affair_web.DTOs.CheckoutDTO;
import es.uca.tfg.ceramic_affair_web.DTOs.CheckoutResponseDTO;
import es.uca.tfg.ceramic_affair_web.payload.ApiResponseType;
import es.uca.tfg.ceramic_affair_web.services.CheckoutService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Controlador para gestionar el proceso de checkout.
 * 
 * @version 1.0
 */
@RestController
@RequestMapping("/api/public/checkout")
@Tag(name = "Checkout", description = "Controlador para el proceso de checkout")
public class CheckoutController {

    @Autowired
    private CheckoutService checkoutService;

    @PostMapping("/procesar")
    @Operation(summary = "Procesar el checkout", description = "Procesa el checkout para un usuario registrado o invitado", tags = { "Checkout" })
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Checkout procesado con éxito"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos, carrito no válido o pago inválido"),
        @ApiResponse(responseCode = "404", description = "Usuario o método de pago no encontrado"),
        @ApiResponse(responseCode = "409", description = "Conflicto en carrito"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<ApiResponseType<CheckoutResponseDTO>> procesarCheckout(@Valid @RequestBody CheckoutDTO checkoutDTO) {
        CheckoutResponseDTO response = checkoutService.checkout(checkoutDTO);
        return ResponseEntity.ok(new ApiResponseType<>(true, "Checkout procesado con éxito", response));
    }
}
