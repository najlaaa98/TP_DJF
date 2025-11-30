// App.tsx
import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import Login from './composant/Login';
import OTPVerification from './composant/OTPVerification';
import ListeUser from './composant/ListeUser';
import CreateUser from './composant/CreateUser';
import ModifierUser from './composant/ModifierUser';
import { authService } from './services/api';

const App: React.FC = () => {
    const [currentStep, setCurrentStep] = useState<'login' | 'otp' | 'users'>('login');
    const [userPhone, setUserPhone] = useState('');
    const [loading, setLoading] = useState(true);
    const [isAuthenticated, setIsAuthenticated] = useState(false);

    useEffect(() => {
        checkAuthStatus();
    }, []);

    const checkAuthStatus = async () => {
        try {
            const status = await authService.getAuthStatus();
            if (status.authenticated && status.otpVerified) {
                setIsAuthenticated(true);
                setCurrentStep('users');
            } else if (status.authenticated && !status.otpVerified) {
                setCurrentStep('otp');
                setUserPhone(status.phone || '');
            } else {
                setCurrentStep('login');
                setIsAuthenticated(false);
            }
        } catch (error) {
            console.error('Erreur vérification statut:', error);
            setCurrentStep('login');
            setIsAuthenticated(false);
        } finally {
            setLoading(false);
        }
    };

    const handleLoginSuccess = (phone: string) => {
        setUserPhone(phone);
        setCurrentStep('otp');
    };

    const handleOTPVerificationSuccess = () => {
        setIsAuthenticated(true);
        setCurrentStep('users');
    };

    const handleBackToLogin = () => {
        setCurrentStep('login');
        setUserPhone('');
        setIsAuthenticated(false);
    };

    const handleLogout = () => {
        setCurrentStep('login');
        setUserPhone('');
        setIsAuthenticated(false);
    };

    if (loading) {
        return (
            <div className="container d-flex justify-content-center align-items-center min-vh-100">
                <div className="spinner-border" role="status">
                    <span className="visually-hidden">Chargement...</span>
                </div>
            </div>
        );
    }

    // Si l'utilisateur n'est pas authentifié, afficher le flux d'authentification
    if (!isAuthenticated && currentStep !== 'users') {
        return (
            <div className="App">
                {currentStep === 'login' && (
                    <Login onLoginSuccess={handleLoginSuccess} />
                )}

                {currentStep === 'otp' && (
                    <OTPVerification
                        phone={userPhone}
                        onVerificationSuccess={handleOTPVerificationSuccess}
                        onBack={handleBackToLogin}
                    />
                )}
            </div>
        );
    }

    // Si l'utilisateur est authentifié, afficher le routage
    return (
        <Router>
            <div className="App">
                <Routes>
                    <Route
                        path="/"
                        element={<ListeUser onLogout={handleLogout} />}
                    />
                    <Route
                        path="/create-user"
                        element={<CreateUser />}
                    />
                    <Route
                        path="/edit-user/:id"
                        element={<ModifierUser />}
                    />
                    <Route
                        path="*"
                        element={<Navigate to="/" replace />}
                    />
                </Routes>
            </div>
        </Router>
    );
};

export default App;