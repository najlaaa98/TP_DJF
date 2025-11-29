// components/ListeUser.tsx
import React, { useState, useEffect } from 'react';
import type { User } from '../types';
import { userService, authService } from '../services/api';

interface ListeUserProps {
    onLogout: () => void;
}

const ListeUser: React.FC<ListeUserProps> = ({ onLogout }) => {
    const [users, setUsers] = useState<User[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        const loadUsers = async () => {
            try {
                const usersData = await userService.getAllUsers();
                setUsers(usersData);
            } catch (error: unknown) {
                if (error instanceof Error) {
                    setError(error.message || 'Erreur lors du chargement des utilisateurs');
                } else {
                    setError('Erreur lors du chargement des utilisateurs');
                }
            } finally {
                setLoading(false);
            }
        };

        loadUsers();
    }, []);

    const handleLogout = async () => {
        try {
            await authService.logout();
        } catch (error: unknown) {
            console.error('Erreur lors de la déconnexion:', error);
        } finally {
            onLogout();
        }
    };

    if (loading) {
        return (
            <div className="container mt-5 text-center">
                <div className="spinner-border" role="status">
                    <span className="visually-hidden">Chargement...</span>
                </div>
            </div>
        );
    }

    return (
        <div className="container mt-4">
            <div className="d-flex justify-content-between align-items-center mb-4">
                <h2>Liste des utilisateurs</h2>
                <button
                    className="btn btn-outline-danger"
                    onClick={handleLogout}
                >
                    Déconnexion
                </button>
            </div>

            {error && (
                <div className="alert alert-danger" role="alert">
                    {error}
                </div>
            )}

            <div className="card">
                <div className="card-body">
                    <div className="table-responsive">
                        <table className="table table-striped table-hover">
                            <thead>
                            <tr>
                                <th>ID</th>
                                <th>Nom</th>
                                <th>Prénom</th>
                                <th>Email</th>
                                <th>Téléphone</th>
                            </tr>
                            </thead>
                            <tbody>
                            {users.map((user) => (
                                <tr key={user.id}>
                                    <td>{user.id}</td>
                                    <td>{user.nom}</td>
                                    <td>{user.prenom}</td>
                                    <td>{user.email}</td>
                                    <td>{user.telephone}</td>
                                </tr>
                            ))}
                            </tbody>
                        </table>
                    </div>

                    {users.length === 0 && (
                        <div className="text-center text-muted py-4">
                            Aucun utilisateur trouvé
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
};

export default ListeUser;