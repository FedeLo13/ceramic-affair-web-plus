export interface LoginDTO {
    email: string;
    password: string;
    recaptchaToken: string;
}

export interface LoginResponse {
    token: string;
}

export interface OlvidoDTO {
    email: string;
    recaptchaToken: string;
}

export interface RecuperacionDTO {
    token: string;
    nuevaPassword: string;
}

export interface CambioDTO {
    email: string;
    token: string;
    antiguaPassword: string;
    nuevaPassword: string;
}