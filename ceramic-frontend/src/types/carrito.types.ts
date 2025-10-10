import type { ProductoOutputDTO } from "./producto.types";

export interface CarritoItemDTO {
    producto: ProductoOutputDTO;
    precioUnitario: number;
}

export interface CarritoDTO {
    items: CarritoItemDTO[];
    total: number;
}