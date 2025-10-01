package es.uca.tfg.ceramic_affair_web.controllers.user;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import es.uca.tfg.ceramic_affair_web.DTOs.CarritoDTO;
import es.uca.tfg.ceramic_affair_web.DTOs.CarritoItemDTO;
import es.uca.tfg.ceramic_affair_web.DTOs.ProductoMapper;
import es.uca.tfg.ceramic_affair_web.entities.Carrito;
import es.uca.tfg.ceramic_affair_web.payload.ApiResponseType;
import es.uca.tfg.ceramic_affair_web.services.CarritoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controlador para la gestión del carrito de compras.
 * Este controlador maneja las operaciones relacionadas con el carrito de compras, como agregar, eliminar y listar productos.
 * 
 * @version 1.0
 */
@RestController
@RequestMapping("/api/user/carrito")
@Tag(name = "Carrito", description = "Controlador para la gestión del carrito de compras")
public class CarritoController {

    @Autowired
    private CarritoService carritoService;

    @GetMapping("/obtener")
    @Operation(summary = "Obtener el carrito del usuario", description = "Devuelve el carrito de compras del usuario autenticado", tags = { "Carrito" })
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Carrito obtenido exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos proporcionados"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<ApiResponseType<CarritoDTO>> obtenerCarrito(@RequestParam Long usuarioId) {
        Optional<Carrito> carritoOpt = carritoService.getCarrito(usuarioId);

        CarritoDTO carritoDTO = carritoOpt.map(carrito -> {
            List<CarritoItemDTO> itemsDTO = carrito.getItems().stream()
                .map(item -> new CarritoItemDTO(
                    ProductoMapper.toDTO(item.getProducto()),
                    item.getPrecioUnitario()
                ))
                .toList();
            return new CarritoDTO(itemsDTO, carrito.getTotal());
        }).orElse(new CarritoDTO(List.of(), BigDecimal.ZERO));

        return ResponseEntity.ok(new ApiResponseType<>(true, "Carrito obtenido exitosamente", carritoDTO));
    }

    @PostMapping("/agregar")
    @Operation(summary = "Agregar un producto al carrito", description = "Agrega un producto al carrito de compras del usuario autenticado", tags = { "Carrito" })
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Producto agregado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Producto ya está en el carrito o no está disponible"),
        @ApiResponse(responseCode = "404", description = "Usuario o producto no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<ApiResponseType<CarritoDTO>> agregarProducto(@RequestParam Long usuarioId, @RequestParam Long productoId) {
        Carrito carrito = carritoService.addProducto(usuarioId, productoId);

        List<CarritoItemDTO> itemsDTO = carrito.getItems().stream()
            .map(item -> new CarritoItemDTO(
                ProductoMapper.toDTO(item.getProducto()),
                item.getPrecioUnitario()
            ))
            .toList();
        CarritoDTO carritoDTO = new CarritoDTO(itemsDTO, carrito.getTotal());

        return ResponseEntity.ok(new ApiResponseType<>(true, "Producto agregado exitosamente", carritoDTO));
    }

    @DeleteMapping("/eliminar")
    @Operation(summary = "Eliminar un producto del carrito", description = "Elimina un producto del carrito de compras del usuario autenticado", tags = { "Carrito" })
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Producto eliminado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<ApiResponseType<CarritoDTO>> eliminarProducto(@RequestParam Long usuarioId, @RequestParam Long productoId) {
        Optional<Carrito> carritoOpt = carritoService.removeProducto(usuarioId, productoId);

        CarritoDTO carritoDTO = carritoOpt.map(carrito -> {
            List<CarritoItemDTO> itemsDTO = carrito.getItems().stream()
                .map(item -> new CarritoItemDTO(
                    ProductoMapper.toDTO(item.getProducto()),
                    item.getPrecioUnitario()
                ))
                .toList();
            return new CarritoDTO(itemsDTO, carrito.getTotal());
        }).orElse(new CarritoDTO(List.of(), BigDecimal.ZERO));

        return ResponseEntity.ok(new ApiResponseType<>(true, "Producto eliminado exitosamente", carritoDTO));
    }

    @DeleteMapping("/vaciar")
    @Operation(summary = "Vaciar el carrito", description = "Vacía el carrito de compras del usuario autenticado", tags = { "Carrito" })
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Carrito vaciado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<ApiResponseType<Void>> vaciarCarrito(@RequestParam Long usuarioId) {
        carritoService.clearCarrito(usuarioId);
        return ResponseEntity.ok(new ApiResponseType<>(true, "Carrito vaciado exitosamente", null));
    }
}
