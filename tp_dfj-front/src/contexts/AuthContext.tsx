// contexts/AuthContext.tsx
import React, { createContext, useContext, useState, useEffect, ReactNode } from 'react';
import { AuthStatus } from '../types';
import { authService } from '../services/api';

interface AuthContextType {
    authStatus: AuthStatus | null;
    loading: boolean;
    checkAuthStatus: () => Promise<void>;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const useAuth = () => {
    const context = useContext(AuthContext);
    if (context === undefined) {
        throw new Error('useAuth must be used within an AuthProvider');
    }
    return context;
};

interface AuthProviderProps {
    children: ReactNode;
}

export const AuthProvider: React.FC<AuthProviderProps> = ({ children }) => {
    const [authStatus, setAuthStatus] = useState<AuthStatus | null>(null);
    const [loading, setLoading] = useState(true);

    const checkAuthStatus = async () => {
        try {
            const status = await authService.getAuthStatus();
            setAuthStatus(status);
        } catch (error) {
            console.error('Erreur vérification statut auth:', error);
            setAuthStatus({ authenticated: false });
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        checkAuthStatus();
    }, []);

    return (
        <AuthContext.Provider value={{ authStatus, loading, checkAuthStatus }}>
            {children}
        </AuthContext.Provider>
    );
};