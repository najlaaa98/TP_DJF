// components/ListeUser.tsx
import React, { useState, useEffect } from 'react';
import type { User } from '../types';
import { userService, authService } from '../services/api';
import { useNavigate } from 'react-router-dom';

interface ListeUserProps {
    onLogout: () => void;
}

const ListeUser: React.FC<ListeUserProps> = ({ onLogout }) => {
    const [users, setUsers] = useState<User[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [deleteLoading, setDeleteLoading] = useState<number | null>(null);
    const navigate = useNavigate();

    useEffect(() => {
        loadUsers();
    }, []);

    const loadUsers = async () => {
        try {
            const usersData = await userService.getAllUsers();
            setUsers(usersData);
        } catch (error: unknown) {
            if (error instanceof Error) {
                setError(error.message);
            } else {
                setError('Erreur lors du chargement des utilisateurs');
            }
        } finally {
            setLoading(false);
        }
    };

    const handleDeleteUser = async (userId: number) => {
        if (!window.confirm('Êtes-vous sûr de vouloir supprimer cet utilisateur ?')) {
            return;
        }

        setDeleteLoading(userId);
        try {
            await userService.deleteUser(userId);
            // Recharger la liste après suppression
            await loadUsers();
        } catch (error: unknown) {
            if (error instanceof Error) {
                setError(error.message);
            } else {
                setError('Erreur lors de la suppression');
            }
        } finally {
            setDeleteLoading(null);
        }
    };

    const handleEditUser = (userId: number) => {
        navigate(`/edit-user/${userId}`);
    };

    const handleAddUser = () => {
        navigate('/create-user');
    };

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
                <div className="d-flex gap-2">
                    <button
                        className="btn btn-success"
                        onClick={handleAddUser}
                    >
                        <i className="bi bi-person-plus me-1"></i>
                        Ajouter
                    </button>
                    <button
                        className="btn btn-outline-danger"
                        onClick={handleLogout}
                    >
                        Déconnexion
                    </button>
                </div>
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
                                <th width="150">Actions</th>
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
                                    <td>
                                        <div className="btn-group btn-group-sm" role="group">
                                            <button
                                                type="button"
                                                className="btn btn-outline-primary"
                                                onClick={() => handleEditUser(user.id)}
                                                title="Modifier"
                                            >
                                                <i className="bi bi-pencil"></i>
                                            </button>
                                            <button
                                                type="button"
                                                className="btn btn-outline-danger"
                                                onClick={() => handleDeleteUser(user.id)}
                                                disabled={deleteLoading === user.id}
                                                title="Supprimer"
                                            >
                                                {deleteLoading === user.id ? (
                                                    <span className="spinner-border spinner-border-sm" role="status">
                              <span className="visually-hidden">Chargement...</span>
                            </span>
                                                ) : (
                                                    <i className="bi bi-trash"></i>
                                                )}
                                            </button>
                                        </div>
                                    </td>
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