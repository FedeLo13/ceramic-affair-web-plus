import { fetchWithAuth, handleFetch } from "./utils";
import type { CambioDTO, LoginDTO, LoginResponse, OlvidoDTO, RecuperacionDTO } from "../types/auth-user.types";

const API_AUTH = 'http://localhost:8080/api/public/auth';
const API_USER = 'http://localhost:8080/api/user';

// Funciones para manejar la autenticación y el usuario

// ------------------ PÚBLICOS ------------------//

export const loginUser = async (loginData: LoginDTO): Promise<LoginResponse> => {
    const response = await fetch(`${API_AUTH}/login/user`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(loginData),
    });

    return await handleFetch<LoginResponse>(response, 'Error during user login');
};

export const loginAdmin = async (loginData: LoginDTO): Promise<LoginResponse> => {
    const response = await fetch(`${API_AUTH}/login/admin`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(loginData),
    });

    return await handleFetch<LoginResponse>(response, 'Error during admin login');
};

export const registerUser = async (registerData: LoginDTO): Promise<string> => {
    const response = await fetch(`${API_AUTH}/registro`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(registerData),
    });

    return await handleFetch<string>(response, 'Error during user registration');
};

export const verifyEmail = async (token: string): Promise<void> => {
    const response = await fetch(`${API_AUTH}/verificar?token=${encodeURIComponent(token)}`, {
        method: 'GET',
    });

    return await handleFetch<void>(response, 'Error during email verification');
};

export const requestPasswordReset = async (resetData: OlvidoDTO): Promise<string> => {
    const response = await fetch(`${API_AUTH}/olvido`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(resetData),
    });

    return await handleFetch<string>(response, 'Error during password reset request');
};

export const resetPassword = async (recoveryData: RecuperacionDTO): Promise<string> => {
    const response = await fetch(`${API_AUTH}/reset`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(recoveryData),
    });

    return await handleFetch<string>(response, 'Error during password reset');
};

// ------------------ USUARIO AUTENTICADO ------------------//

export const changePassword = async (changeData: CambioDTO): Promise<string> => {
    const response = await fetchWithAuth(`${API_USER}/cambio`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(changeData),
    });

    return await handleFetch<string>(response, 'Error during password change');
};
