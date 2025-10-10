import type { CheckoutDTO, CheckoutResponseDTO } from "../types/checkout.types";
import { handleFetch } from "./utils";

const API_URL = 'http://localhost:8080/api/public/checkout';

// Función para manejar el proceso de checkout

export const checkout = async (checkoutData: CheckoutDTO): Promise<CheckoutResponseDTO> => {
    const response = await fetch(`${API_URL}/procesar`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(checkoutData),
    });

    return await handleFetch<CheckoutResponseDTO>(response, 'Error during checkout process');
};