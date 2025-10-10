export interface PagoDTO {
    id: number;
    usuarioId: number | null;
    pedidoId: number | null;
    importe: number;
    tipoPago: string;
}

export type TipoPago = 'BIZUM' | 'TARJETA';