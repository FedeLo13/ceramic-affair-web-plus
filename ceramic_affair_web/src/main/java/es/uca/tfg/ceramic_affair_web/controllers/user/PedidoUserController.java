package es.uca.tfg.ceramic_affair_web.controllers.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import es.uca.tfg.ceramic_affair_web.entities.Pedido;
import es.uca.tfg.ceramic_affair_web.payload.ApiResponseType;
import es.uca.tfg.ceramic_affair_web.services.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controlador para la gestión de pedidos por parte del usuario.
 * Este controlador maneja los endpoints específicos para los usuarios relacionados con los pedidos.
 * 
 * @version 1.0
 */
@RestController
@RequestMapping("/api/user/pedidos")
@Tag(name = "Pedidos User", description = "Controlador para la gestión de pedidos por parte del usuario")
public class PedidoUserController {

    @Autowired
    private PedidoService pedidoService;

    @GetMapping("/listar")
    @Operation(summary = "Listar pedidos del usuario", description = "Devuelve una lista de pedidos realizados por el usuario autenticado", tags = { "Pedidos User" })
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pedidos obtenidos exitosamente"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    private ResponseEntity<ApiResponseType<List<Pedido>>> listarPedidosUsuario(@RequestParam Long usuarioId) {
        List<Pedido> pedidos = pedidoService.getPedidosByUsuarioId(usuarioId);
        return ResponseEntity.ok(new ApiResponseType<>(true, "Pedidos obtenidos exitosamente", pedidos));
    }
}
