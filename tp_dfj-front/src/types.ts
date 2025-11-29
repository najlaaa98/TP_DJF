// types.ts
export interface User {
    id: number;
    nom: string;
    prenom: string;
    email: string;
    telephone: string;
    password?: string;
}

export interface AuthRequest {
    email?: string;
    password?: string;
    otpCode?: string;
}

export interface AuthResponse {
    message: string;
    user?: string;
    phone?: string;
}

export interface AuthStatus {
    authenticated: boolean;
    user?: string;
    otpVerified?: boolean;
    phone?: string;
}