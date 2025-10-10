import type { PagoDTO } from "../types/pago.types";
import { fetchWithAuth, handleFetch } from "./utils";

const API_PUBLIC = 'http://localhost:8080/api/public/pagos';
const API_USER = 'http://localhost:8080/api/user/pagos';

// Funciones para manejar los pagos

// ------------------ PÚBLICOS ------------------//

export const getPagoByPedidoId = async (pedidoId: number): Promise<PagoDTO> => {
    const response = await fetch(`${API_PUBLIC}/${pedidoId}`, {
        method: 'GET',
    });

    return await handleFetch<PagoDTO>(response, 'Error fetching payment by order ID');
};

// ------------------ USUARIO ------------------//

export const getPagosByUsuarioId = async (userId: number): Promise<PagoDTO[]> => {
    const response = await fetchWithAuth(`${API_USER}/${userId}`, {
        method: 'GET',
    });

    return await handleFetch<PagoDTO[]>(response, 'Error fetching payments by user ID');
};