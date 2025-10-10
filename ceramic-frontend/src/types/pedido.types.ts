import type { ProductoOutputDTO } from "./producto.types";

export interface PedidoDataDTO {
    nombreCliente: string;
    apellidosCliente: string;
    emailCliente: string;
    provincia: string;
    ciudad: string;
    codigoPostal: string;
    direccion: string;
}

export interface PedidoItemDTO {
    producto: ProductoOutputDTO;
    precioUnitario: number;
}

export interface PedidoDTO {
    id: number;
    items: PedidoItemDTO[];
    total: number;
    pedido: PedidoDataDTO;
    enviado: boolean;
}
