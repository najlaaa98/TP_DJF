import api from './api';

export interface AuthRequest {
    email?: string;
    password?: string;
    otpCode?: string;
    phoneNumber?: string;
    userId?: number;
}

export interface AuthResponse {
    message: string;
    user?: string;
    user_id?: number;
    user_email?: string;
    phone_number?: string;
    otp_sent?: boolean;
    success?: boolean;
    users?: any[];
    error?: string;
}

class AuthService {
    private currentUser: any = null;

    async login(email: string, password: string): Promise<AuthResponse> {
        try {
            const response = await api.post('/auth/login', {
                email,
                password
            });

            // Stocker les infos utilisateur pour l'étape OTP
            if (response.data.user_id) {
                this.currentUser = {
                    id: response.data.user_id,
                    email: response.data.user_email,
                    phone: response.data.phone_number
                };
            }

            return response.data;
        } catch (error: any) {
            throw new Error(error.response?.data?.error || 'Erreur de connexion');
        }
    }

    async verifyOTP(phoneNumber: string, otpCode: string, userId?: number): Promise<AuthResponse> {
        try {
            const response = await api.post('/auth/verify-otp', {
                phoneNumber,
                otpCode,
                userId: userId || this.currentUser?.id
            });

            // Si succès, marquer comme authentifié
            if (response.data.success) {
                this.setAuthenticated(true);
            }

            return response.data;
        } catch (error: any) {
            throw new Error(error.response?.data?.error || 'Erreur lors de la vérification OTP');
        }
    }

    async getUsers(): Promise<any[]> {
        try {
            const response = await api.get('/users');
            return response.data;
        } catch (error: any) {
            throw new Error(error.response?.data?.error || 'Erreur lors de la récupération des utilisateurs');
        }
    }

    async logout(): Promise<void> {
        try {
            await api.post('/auth/logout');
        } catch (error) {
            console.error('Erreur lors de la déconnexion:', error);
        } finally {
            this.clearAuth();
            this.currentUser = null;
        }
    }

    // Gestion du stockage local
    isAuthenticated(): boolean {
        return localStorage.getItem('isAuthenticated') === 'true';
    }

    setAuthenticated(status: boolean): void {
        if (status) {
            localStorage.setItem('isAuthenticated', 'true');
        } else {
            localStorage.removeItem('isAuthenticated');
        }
    }

    clearAuth(): void {
        localStorage.removeItem('isAuthenticated');
        this.currentUser = null;
    }

    // Getter pour l'utilisateur courant
    getCurrentUser() {
        return this.currentUser;
    }
}

export const authService = new AuthService();