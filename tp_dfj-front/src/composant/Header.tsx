import React, { useState, useEffect } from 'react';
import { authService } from '../services/authService';

interface HeaderProps {
    onLogout: () => void;
}

interface UserInfo {
    name: string;
    phone: string;
}

const Header: React.FC<HeaderProps> = ({ onLogout }) => {
    const [userInfo, setUserInfo] = useState<UserInfo | null>(null);
    const [showDropdown, setShowDropdown] = useState(false);

    useEffect(() => {
        loadUserInfo();
    }, []);

    const loadUserInfo = async () => {
        try {
            const status = await authService.getAuthStatus();
            if (status.authenticated && status.user) {
                setUserInfo({
                    name: status.user,
                    phone: status.phone || 'Non renseigné'
                });
            }
        } catch (error) {
            console.error('Erreur chargement info utilisateur:', error);
        }
    };

    const handleLogout = async () => {
        try {
            await authService.logout();
        } finally {
            onLogout();
            setShowDropdown(false);
        }
    };

    const toggleDropdown = () => {
        setShowDropdown(!showDropdown);
    };

    return (
        <nav className="navbar navbar-expand-lg navbar-dark bg-primary shadow-lg">
            <div className="container-fluid">
                {/* Logo et Titre */}
                <div className="navbar-brand d-flex align-items-center">
                    <div className="bg-white rounded-circle p-2 me-3">
                        <i className="bi bi-shield-check text-primary fs-4"></i>
                    </div>
                    <div>
                        <h1 className="h4 mb-0 text-white fw-bold">Système OTP</h1>
                        <small className="text-white-50">Gestion des Utilisateurs</small>
                    </div>
                </div>

                {/* Menu de navigation */}
                <div className="navbar-nav me-auto">
                    <a className="nav-link text-white" href="/">
                        <i className="bi bi-house me-1"></i>
                        Accueil
                    </a>
                    <a className="nav-link text-white" href="/">
                        <i className="bi bi-people me-1"></i>
                        Utilisateurs
                    </a>
                    <a className="nav-link text-white" href="/create">
                        <i className="bi bi-person-plus me-1"></i>
                        Nouvel Utilisateur
                    </a>
                </div>

                {/* Section utilisateur avec dropdown */}
                <div className="navbar-nav ms-auto">
                    <div className="nav-item dropdown">
                        <button
                            className="btn btn-outline-light dropdown-toggle d-flex align-items-center"
                            onClick={toggleDropdown}
                            type="button"
                            aria-expanded={showDropdown}
                        >
                            <div className="bg-white text-primary rounded-circle d-flex align-items-center justify-content-center me-2"
                                 style={{ width: '32px', height: '32px' }}>
                                <i className="bi bi-person-fill"></i>
                            </div>
                            <div className="text-start">
                                <div className="fw-semibold" style={{ fontSize: '0.9rem' }}>
                                    {userInfo?.name || 'Utilisateur'}
                                </div>
                                <div className="text-white-50" style={{ fontSize: '0.75rem' }}>
                                    {userInfo?.phone || ''}
                                </div>
                            </div>
                        </button>

                        {/* Dropdown Menu */}
                        {showDropdown && (
                            <div className="dropdown-menu show dropdown-menu-end shadow border-0"
                                 style={{ marginTop: '10px', minWidth: '250px' }}>
                                {/* En-tête du dropdown */}
                                <div className="dropdown-header bg-light py-3">
                                    <div className="d-flex align-items-center">
                                        <div className="bg-primary text-white rounded-circle d-flex align-items-center justify-content-center me-3"
                                             style={{ width: '40px', height: '40px' }}>
                                            <i className="bi bi-person-fill fs-5"></i>
                                        </div>
                                        <div>
                                            <h6 className="mb-0 fw-bold text-dark">{userInfo?.name || 'Utilisateur'}</h6>
                                            <small className="text-muted">{userInfo?.phone || 'Non connecté'}</small>
                                        </div>
                                    </div>
                                </div>

                                <div className="dropdown-divider"></div>

                                {/* État OTP */}
                                <div className="px-3 py-2">
                                    <div className="d-flex justify-content-between align-items-center">
                                        <span className="text-muted small">Vérification OTP</span>
                                        <span className="badge bg-success small">
                      <i className="bi bi-check-circle me-1"></i>
                      Activée
                    </span>
                                    </div>
                                </div>

                                <div className="dropdown-divider"></div>

                                {/* Actions */}
                                <button className="dropdown-item d-flex align-items-center" type="button">
                                    <i className="bi bi-person me-2 text-primary"></i>
                                    Mon Profil
                                </button>
                                <button className="dropdown-item d-flex align-items-center" type="button">
                                    <i className="bi bi-gear me-2 text-primary"></i>
                                    Paramètres
                                </button>
                                <button className="dropdown-item d-flex align-items-center" type="button">
                                    <i className="bi bi-shield-lock me-2 text-primary"></i>
                                    Sécurité
                                </button>

                                <div className="dropdown-divider"></div>

                                {/* Statistiques */}
                                <div className="px-3 py-2">
                                    <small className="text-muted d-block">Session active</small>
                                    <small className="text-success">
                                        <i className="bi bi-circle-fill me-1" style={{ fontSize: '0.5rem' }}></i>
                                        Connecté
                                    </small>
                                </div>

                                <div className="dropdown-divider"></div>

                                {/* Déconnexion */}
                                <button
                                    className="dropdown-item d-flex align-items-center text-danger"
                                    onClick={handleLogout}
                                    type="button"
                                >
                                    <i className="bi bi-box-arrow-right me-2"></i>
                                    Se déconnecter
                                </button>
                            </div>
                        )}
                    </div>
                </div>
            </div>

            {/* Overlay pour fermer le dropdown en cliquant à l'extérieur */}
            {showDropdown && (
                <div
                    className="position-fixed top-0 start-0 w-100 h-100"
                    onClick={() => setShowDropdown(false)}
                    style={{ zIndex: 1040 }}
                ></div>
            )}
        </nav>
    );
};

export default Header;