// components/Login.tsx
import React, { useState } from 'react';
import type { AuthRequest } from '../types';
import { authService } from '../services/api';

interface LoginProps {
    onLoginSuccess: (phone: string) => void;
}

const Login: React.FC<LoginProps> = ({ onLoginSuccess }) => {
    const [credentials, setCredentials] = useState<AuthRequest>({
        email: '',
        password: ''
    });
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setLoading(true);
        setError('');

        try {
            const response = await authService.login(credentials);
            onLoginSuccess(response.phone || '');
        } catch (error: unknown) {
            if (error instanceof Error) {
                // Maintenant on reçoit le message d'erreur du backend
                setError(error.message);
            } else {
                setError('Erreur de connexion inattendue');
            }
        } finally {
            setLoading(false);
        }
    };

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setCredentials({
            ...credentials,
            [e.target.name]: e.target.value
        });
    };

    return (
        <div className="container mt-5">
            <div className="row justify-content-center">
                <div className="col-md-6 col-lg-4">
                    <div className="card">
                        <div className="card-body">
                            <h5 className="card-title text-center mb-4">Connexion</h5>

                            {error && (
                                <div className="alert alert-danger" role="alert">
                                    {error}
                                </div>
                            )}

                            <form onSubmit={handleSubmit}>
                                <div className="mb-3">
                                    <label htmlFor="email" className="form-label">Email</label>
                                    <input
                                        type="email"
                                        className="form-control"
                                        id="email"
                                        name="email"
                                        value={credentials.email}
                                        onChange={handleChange}
                                        required
                                        placeholder="Entrez votre email"
                                    />
                                </div>

                                <div className="mb-3">
                                    <label htmlFor="password" className="form-label">Mot de passe</label>
                                    <input
                                        type="password"
                                        className="form-control"
                                        id="password"
                                        name="password"
                                        value={credentials.password}
                                        onChange={handleChange}
                                        required
                                        placeholder="Entrez votre mot de passe"
                                    />
                                </div>

                                <button
                                    type="submit"
                                    className="btn btn-primary w-100"
                                    disabled={loading}
                                >
                                    {loading ? (
                                        <>
                      <span className="spinner-border spinner-border-sm me-2" role="status">
                        <span className="visually-hidden">Chargement...</span>
                      </span>
                                            Connexion...
                                        </>
                                    ) : (
                                        'Se connecter'
                                    )}
                                </button>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default Login;