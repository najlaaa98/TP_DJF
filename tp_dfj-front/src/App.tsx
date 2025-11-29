// App.tsx
import React, { useState, useEffect } from 'react';
import Login from './composant/Login';
import OTPVerification from './composant/OTPVerification';
import ListeUser from './composant/ListeUser';
import { authService } from './services/api';

const App: React.FC = () => {
    const [currentStep, setCurrentStep] = useState<'login' | 'otp' | 'users'>('login');
    const [userPhone, setUserPhone] = useState('');
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        checkAuthStatus();
    }, []);

    const checkAuthStatus = async () => {
        try {
            const status = await authService.getAuthStatus();
            if (status.authenticated && status.otpVerified) {
                setCurrentStep('users');
            } else if (status.authenticated && !status.otpVerified) {
                setCurrentStep('otp');
                setUserPhone(status.phone || '');
            } else {
                setCurrentStep('login');
            }
        } catch (error) {
            console.error('Erreur vérification statut:', error);
            setCurrentStep('login');
        } finally {
            setLoading(false);
        }
    };

    const handleLoginSuccess = (phone: string) => {
        setUserPhone(phone);
        setCurrentStep('otp');
    };

    const handleOTPVerificationSuccess = () => {
        setCurrentStep('users');
        checkAuthStatus();
    };

    const handleBackToLogin = () => {
        setCurrentStep('login');
        setUserPhone('');
    };

    const handleLogout = () => {
        setCurrentStep('login');
        setUserPhone('');
        checkAuthStatus();
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

            {currentStep === 'users' && (
                <ListeUser onLogout={handleLogout} />
            )}
        </div>
    );
};

export default App;