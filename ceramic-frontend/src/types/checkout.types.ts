import type { CarritoItemDTO } from "./carrito.types";
import type { TipoPago } from "./pago.types";
import type { PedidoDataDTO } from "./pedido.types";

export interface CheckoutDTO {
    usuarioId: number;
    items: CarritoItemDTO[];
    total: number;
    tipoPago: TipoPago;
    pedido: PedidoDataDTO;
}

export interface CheckoutResponseDTO {
    pedidoId: number;
    estado: string;
    total: number;
}