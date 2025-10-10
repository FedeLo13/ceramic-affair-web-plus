package es.uca.tfg.ceramic_affair_web.controllers.admin;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
 * Controlador para la gestión de pedidos en el panel de administración.
 * Proporciona endpoints para ver y gestionar pedidos.
 * 
 * @version 1.0
 */
@RestController
@RequestMapping("/api/admin/pedidos")
@Tag(name = "Pedidos Admin", description = "Controlador para la gestión de pedidos en el panel de administración")
public class PedidoAdminController {

    @Autowired
    private PedidoService pedidoService;

    @GetMapping("/listar")
    @Operation(summary = "Listar todos los pedidos", description = "Obtiene una lista de todos los pedidos realizados", tags = { "Pedidos Admin" })
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de pedidos obtenida con éxito"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<ApiResponseType<List<PedidoDTO>>> listarPedidos() {
        List<Pedido> pedidos = pedidoService.getAllPedidos();
        List<PedidoDTO> pedidosDTO = pedidos.stream()
                .map(PedidoMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(new ApiResponseType<>(true, "Lista de pedidos obtenida con éxito", pedidosDTO));
    }

    @GetMapping("/enviados")
    @Operation(summary = "Listar todos los pedidos enviados", description = "Obtiene una lista de todos los pedidos que han sido enviados", tags = { "Pedidos Admin" })
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de pedidos enviados obtenida con éxito"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<ApiResponseType<List<PedidoDTO>>> listarPedidosEnviados() {
        List<Pedido> pedidos = pedidoService.getPedidosEnviados();
        List<PedidoDTO> pedidosDTO = pedidos.stream()
                .map(PedidoMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(new ApiResponseType<>(true, "Lista de pedidos enviados obtenida con éxito", pedidosDTO));
    }

    @GetMapping("/no-enviados")
    @Operation(summary = "Listar todos los pedidos no enviados", description = "Obtiene una lista de todos los pedidos que no han sido enviados", tags = { "Pedidos Admin" })
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de pedidos no enviados obtenida con éxito"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<ApiResponseType<List<PedidoDTO>>> listarPedidosNoEnviados() {
        List<Pedido> pedidos = pedidoService.getPedidosNoEnviados();
        List<PedidoDTO> pedidosDTO = pedidos.stream()
                .map(PedidoMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(new ApiResponseType<>(true, "Lista de pedidos no enviados obtenida con éxito", pedidosDTO));
    }

    @PatchMapping("/{id}/enviado")
    @Operation(summary = "Marcar un pedido como enviado", description = "Marca el pedido con el ID proporcionado como enviado", tags = { "Pedidos Admin" })
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Pedido marcado como enviado con éxito"),
        @ApiResponse(responseCode = "404", description = "Pedido no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<Void> marcarPedidoEnviado(@PathVariable Long id, @RequestParam boolean enviado) {
        pedidoService.setPedidoEnviado(id, enviado);
        return ResponseEntity.noContent().build();
    }
}
