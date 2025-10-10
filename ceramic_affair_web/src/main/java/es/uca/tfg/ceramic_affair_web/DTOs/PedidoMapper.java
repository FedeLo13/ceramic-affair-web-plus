package es.uca.tfg.ceramic_affair_web.DTOs;

import java.util.List;
import java.util.stream.Collectors;

import es.uca.tfg.ceramic_affair_web.entities.Pedido;
import es.uca.tfg.ceramic_affair_web.entities.PedidoItem;

public class PedidoMapper {

    /**
     * Convierte una entidad Pedido a su correspondiente DTO.
     * 
     * @param pedido la entidad Pedido a convertir
     * @return el PedidoDTO resultante, o null si el pedido es nulo
     */
    public static PedidoDTO toDTO(Pedido pedido) {
        if (pedido == null) {
            return null;
        }

        // Mapeamos los datos del cliente/envío
        PedidoDataDTO pedidoDataDTO = new PedidoDataDTO();
        pedidoDataDTO.setNombreCliente(pedido.getNombreCliente());
        pedidoDataDTO.setApellidosCliente(pedido.getApellidosCliente());
        pedidoDataDTO.setEmailCliente(pedido.getEmailCliente());
        pedidoDataDTO.setProvincia(pedido.getProvinciaEnvio());
        pedidoDataDTO.setCiudad(pedido.getCiudadEnvio());
        pedidoDataDTO.setCodigoPostal(pedido.getCodigoPostalEnvio());
        pedidoDataDTO.setDireccion(pedido.getDireccionEnvio());

        // Mapeamos los ítems del pedido
        List<PedidoItemDTO> itemsDTO = pedido.getItems().stream()
                .map(PedidoMapper::mapPedidoItemToDTO)
                .collect(Collectors.toList());

        // Creamos y devolvemos el DTO principal
        PedidoDTO pedidoDTO = new PedidoDTO(
                pedido.getId(),
                itemsDTO,
                pedido.getTotal(),
                pedidoDataDTO,
                pedido.isEnviado()
        );

        return pedidoDTO;
    }

    /**
     * Convierte un PedidoItem a su correspondiente DTO.
     */
    private static PedidoItemDTO mapPedidoItemToDTO(PedidoItem item) {
        if (item == null) {
            return null;
        }
        PedidoItemDTO dto = new PedidoItemDTO();
        dto.setProducto(ProductoMapper.toDTO(item.getProducto()));
        dto.setPrecioUnitario(item.getPrecioUnitario());
        return dto;
    }
}

