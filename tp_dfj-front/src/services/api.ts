// services/api.ts
import axios from 'axios';
import { User, AuthRequest, AuthResponse, AuthStatus } from '../types';

const API_BASE_URL = 'http://localhost:8080/api';

// Configuration Axios pour gérer les sessions
const api = axios.create({
    baseURL: API_BASE_URL,
    withCredentials: true,
});

// Intercepteur pour gérer les erreurs de manière uniforme
api.interceptors.response.use(
    (response) => response,
    (error) => {
        if (error.response) {
            // Le serveur a répondu avec un code d'erreur
            const message = error.response.data?.error || error.response.data?.message || 'Erreur inconnue';
            throw new Error(message);
        } else if (error.request) {
            // La requête a été faite mais aucune réponse n'a été reçue
            throw new Error('Serveur inaccessible. Vérifiez votre connexion.');
        } else {
            // Une erreur s'est produite lors de la configuration de la requête
            throw new Error('Erreur de configuration de la requête.');
        }
    }
);

class AuthService {
    async login(credentials: AuthRequest): Promise<AuthResponse> {
        const response = await api.post<AuthResponse>('/auth/login', credentials);
        return response.data;
    }

    async requestOTP(): Promise<AuthResponse> {
        const response = await api.post<AuthResponse>('/auth/request-otp');
        return response.data;
    }

    async verifyOTP(otpCode: string): Promise<AuthResponse> {
        const response = await api.post<AuthResponse>('/auth/verify-otp', { otpCode });
        return response.data;
    }

    async logout(): Promise<AuthResponse> {
        const response = await api.post<AuthResponse>('/auth/logout');
        return response.data;
    }

    async getAuthStatus(): Promise<AuthStatus> {
        const response = await api.get<AuthStatus>('/auth/status');
        return response.data;
    }
}

class UserService {
    async getAllUsers(): Promise<User[]> {
        const response = await api.get<User[]>('/users');
        return response.data;
    }

    async getUserById(id: number): Promise<User> {
        const response = await api.get<User>(`/users/${id}`);
        return response.data;
    }
    async deleteUser(id: number): Promise<void> {
        await api.delete(`/users/${id}`);
    }
}

export const authService = new AuthService();
export const userService = new UserService();
export default api;