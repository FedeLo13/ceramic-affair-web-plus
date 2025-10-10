import type { PedidoDTO } from "../types/pedido.types";
import { fetchWithAuth, handleFetch } from "./utils";

const API_PUBLIC = 'http://localhost:8080/api/public/pedidos';
const API_USER = 'http://localhost:8080/api/user/pedidos';
const API_ADMIN = 'http://localhost:8080/api/admin/pedidos';

// Funciones para manejar los pedidos

// ------------------ PÚBLICOS ------------------//

export const getPedidoById = async (id: number): Promise<PedidoDTO> => {
    const response = await fetch(`${API_PUBLIC}/${id}`, {
        method: 'GET',
    });

    return await handleFetch<PedidoDTO>(response, 'Error fetching order by ID');
};

// ------------------ USUARIO ------------------//

export const getPedidosByUserId = async (userId: string): Promise<PedidoDTO[]> => {
    const response = await fetchWithAuth(`${API_USER}/listar?usuarioId=${encodeURIComponent(userId)}`, {
        method: 'GET',
    });

    return await handleFetch<PedidoDTO[]>(response, 'Error fetching orders by user ID');
};

// ------------------ ADMIN ------------------//

export const getAllPedidos = async (): Promise<PedidoDTO[]> => {
    const response = await fetchWithAuth(`${API_ADMIN}/listar`, {
        method: 'GET',
    });

    return await handleFetch<PedidoDTO[]>(response, 'Error fetching all orders');
};

export const getAllPedidosEnviados = async (): Promise<PedidoDTO[]> => {
    const response = await fetchWithAuth(`${API_ADMIN}/enviados`, {
        method: 'GET',
    });

    return await handleFetch<PedidoDTO[]>(response, 'Error fetching all sent orders');
};

export const getAllPedidosNoEnviados = async (): Promise<PedidoDTO[]> => {
    const response = await fetchWithAuth(`${API_ADMIN}/no-enviados`, {
        method: 'GET',
    });

    return await handleFetch<PedidoDTO[]>(response, 'Error fetching all not sent orders');
};

export const setPedidoEnviado = async (id: number, enviado: boolean): Promise<void> => {
    const response = await fetchWithAuth(`${API_ADMIN}/${id}/enviado?enviado=${enviado}`, {
        method: 'PATCH',
    });

    return await handleFetch<void>(response, 'Error updating order status');
};