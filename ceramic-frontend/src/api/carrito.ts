import type { CarritoDTO } from "../types/carrito.types";
import { fetchWithAuth, handleFetch } from "./utils";

const API_URL = 'http://localhost:8080/api/user/carrito';

// Funciones para manejar el carrito de compras

export const getCart = async (userId : string): Promise<CarritoDTO> => {
    const response = await fetchWithAuth(`${API_URL}/obtener?usuarioId=${encodeURIComponent(userId)}`, {
        method: 'GET',
    });

    return await handleFetch<CarritoDTO>(response, 'Error fetching cart');
};

export const addItemToCart = async (userId : string, productId: string): Promise<CarritoDTO> => {
    const response = await fetchWithAuth(`${API_URL}/agregar?usuarioId=${encodeURIComponent(userId)}&productoId=${encodeURIComponent(productId)}`, {
        method: 'POST',
    });

    return await handleFetch<CarritoDTO>(response, 'Error adding item to cart');
};

export const removeItemFromCart = async (userId : string, productId: string): Promise<CarritoDTO> => {
    const response = await fetchWithAuth(`${API_URL}/eliminar?usuarioId=${encodeURIComponent(userId)}&productoId=${encodeURIComponent(productId)}`, {
        method: 'DELETE',
    });

    return await handleFetch<CarritoDTO>(response, 'Error removing item from cart');
};

export const clearCart = async (userId : string): Promise<void> => {
    const response = await fetchWithAuth(`${API_URL}/vaciar?usuarioId=${encodeURIComponent(userId)}`, {
        method: 'DELETE',
    });

    return await handleFetch<void>(response, 'Error clearing cart');
};
