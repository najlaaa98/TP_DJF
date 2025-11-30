// components/OTPVerification.tsx
import React, { useState, useEffect } from 'react';
import { authService } from '../services/api';

interface OTPVerificationProps {
    phone: string;
    onVerificationSuccess: () => void;
    onBack: () => void;
}

const OTPVerification: React.FC<OTPVerificationProps> = ({
                                                             phone,
                                                             onVerificationSuccess,
                                                             onBack
                                                         }) => {
    const [otpCode, setOtpCode] = useState('');
    const [loading, setLoading] = useState(false);
    const [sending, setSending] = useState(false);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');
    const [countdown, setCountdown] = useState(0);

    useEffect(() => {
        requestOTP();
    }, []);

    useEffect(() => {
        let timer: NodeJS.Timeout;
        if (countdown > 0) {
            timer = setTimeout(() => setCountdown(countdown - 1), 1000);
        }
        return () => clearTimeout(timer);
    }, [countdown]);

    const requestOTP = async () => {
        setSending(true);
        setError('');
        setSuccess('');

        try {
            const response = await authService.requestOTP();
            setSuccess(response.message);
            setCountdown(30);
        } catch (error: unknown) {
            if (error instanceof Error) {
                setError(error.message);
            } else {
                setError('Erreur lors de l\'envoi de l\'OTP');
            }
        } finally {
            setSending(false);
        }
    };

    const verifyOTP = async (e: React.FormEvent) => {
        e.preventDefault();
        setLoading(true);
        setError('');

        try {
            await authService.verifyOTP(otpCode);
            onVerificationSuccess();
        } catch (error: unknown) {
            if (error instanceof Error) {
                setError(error.message);
            } else {
                setError('Code OTP invalide');
            }
        } finally {
            setLoading(false);
        }
    };

    const canResend = countdown === 0 && !sending;

    return (
        <div className="container mt-5">
            <div className="row justify-content-center">
                <div className="col-md-6 col-lg-4">
                    <div className="card">
                        <div className="card-body">
                            <h5 className="card-title text-center mb-4">Vérification OTP</h5>

                            <div className="alert alert-info text-center" role="alert">
                                Code envoyé au : <strong>{phone}</strong>
                            </div>

                            {error && (
                                <div className="alert alert-danger" role="alert">
                                    {error}
                                </div>
                            )}

                            {success && (
                                <div className="alert alert-success" role="alert">
                                    {success}
                                </div>
                            )}

                            <form onSubmit={verifyOTP}>
                                <div className="mb-3">
                                    <label htmlFor="otpCode" className="form-label">Code OTP</label>
                                    <input
                                        type="text"
                                        className="form-control"
                                        id="otpCode"
                                        value={otpCode}
                                        onChange={(e) => setOtpCode(e.target.value.replace(/\D/g, ''))}
                                        required
                                        maxLength={6}
                                        placeholder="Entrez le code à 6 chiffres"
                                    />
                                    <div className="form-text">
                                        Le code expire après 2 minutes
                                    </div>
                                </div>

                                <div className="d-grid gap-2">
                                    <button
                                        type="submit"
                                        className="btn btn-primary"
                                        disabled={loading || otpCode.length !== 6}
                                    >
                                        {loading ? (
                                            <>
                        <span className="spinner-border spinner-border-sm me-2" role="status">
                          <span className="visually-hidden">Chargement...</span>
                        </span>
                                                Vérification...
                                            </>
                                        ) : (
                                            'Vérifier le code'
                                        )}
                                    </button>

                                    <button
                                        type="button"
                                        className="btn btn-outline-secondary"
                                        onClick={requestOTP}
                                        disabled={!canResend}
                                    >
                                        {sending ? (
                                            <>
                        <span className="spinner-border spinner-border-sm me-2" role="status">
                          <span className="visually-hidden">Chargement...</span>
                        </span>
                                                Envoi...
                                            </>
                                        ) : countdown > 0 ? (
                                            `Renvoyer (${countdown}s)`
                                        ) : (
                                            'Renvoyer le code'
                                        )}
                                    </button>

                                    <button
                                        type="button"
                                        className="btn btn-link text-decoration-none"
                                        onClick={onBack}
                                    >
                                        ← Retour à la connexion
                                    </button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default OTPVerification;