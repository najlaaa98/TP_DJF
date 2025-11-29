// services/api.ts
import axios from 'axios';
import { User, AuthRequest, AuthResponse, AuthStatus } from '../types';

const API_BASE_URL = 'http://localhost:8080/api';

// Configuration Axios pour gérer les sessions
const api = axios.create({
    baseURL: API_BASE_URL,
    withCredentials: true, // Important pour les sessions
});

class AuthService {
    // Connexion email/mot de passe
    async login(credentials: AuthRequest): Promise<AuthResponse> {
        const response = await api.post<AuthResponse>('/auth/login', credentials);
        return response.data;
    }

    // Demander l'envoi d'OTP
    async requestOTP(): Promise<AuthResponse> {
        const response = await api.post<AuthResponse>('/auth/request-otp');
        return response.data;
    }

    // Vérifier l'OTP
    async verifyOTP(otpCode: string): Promise<AuthResponse> {
        const response = await api.post<AuthResponse>('/auth/verify-otp', { otpCode });
        return response.data;
    }

    // Déconnexion
    async logout(): Promise<AuthResponse> {
        const response = await api.post<AuthResponse>('/auth/logout');
        return response.data;
    }

    // Vérifier le statut d'authentification
    async getAuthStatus(): Promise<AuthStatus> {
        const response = await api.get<AuthStatus>('/auth/status');
        return response.data;
    }
}

class UserService {
    // Récupérer tous les utilisateurs
    async getAllUsers(): Promise<User[]> {
        const response = await api.get<User[]>('/users');
        return response.data;
    }

    // Récupérer un utilisateur par ID
    async getUserById(id: number): Promise<User> {
        const response = await api.get<User>(`/users/${id}`);
        return response.data;
    }
}

export const authService = new AuthService();
export const userService = new UserService();
export default api;