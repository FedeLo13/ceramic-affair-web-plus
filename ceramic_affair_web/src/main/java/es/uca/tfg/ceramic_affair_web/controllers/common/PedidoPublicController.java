package es.uca.tfg.ceramic_affair_web.controllers.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.uca.tfg.ceramic_affair_web.DTOs.PedidoDTO;
import es.uca.tfg.ceramic_affair_web.DTOs.PedidoMapper;
import es.uca.tfg.ceramic_affair_web.entities.Pedido;
import es.uca.tfg.ceramic_affair_web.payload.ApiResponseType;
import es.uca.tfg.ceramic_affair_web.services.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controlador para los endpoints públicos relacionados con la entidad Pedido.
 * Proporciona endpoints para obtener pedidos.
 * 
 * @version 1.0
 */
@RestController
@RequestMapping("/api/public/pedidos")
@Tag(name = "Pedidos Public", description = "Controlador para la gestión de pedidos")
public class PedidoPublicController {

    @Autowired
    private PedidoService pedidoService;

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un pedido por su ID", description = "Devuelve el pedido correspondiente al ID proporcionado", tags = { "Pedidos Public" })
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pedido encontrado"),
        @ApiResponse(responseCode = "404", description = "Pedido no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<ApiResponseType<PedidoDTO>> obtenerPedido(@PathVariable Long id) {
        Pedido pedido = pedidoService.getPedidoById(id);
        PedidoDTO pedidoDTO = PedidoMapper.toDTO(pedido);
        return ResponseEntity.ok(new ApiResponseType<>(true, "Pedido encontrado", pedidoDTO));
    }
}
