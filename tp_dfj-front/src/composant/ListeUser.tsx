import axios from "axios";
import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";

interface User {
  id: number;
  nom: string;
  prenom: string;
  email: string;
  telephone: string;
}

export default function ListeUser() {
  const [data, setData] = useState<User[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [deleteLoading, setDeleteLoading] = useState<number | null>(null);
  const navigate = useNavigate();  

  useEffect(() => {
    fetchUsers();
  }, []);

  const fetchUsers = async () => {
    try {
      setLoading(true);
      const res = await axios.get<User[]>("http://localhost:8080/api/users");
      setData(res.data);
      setError("");
    } catch (err: any) {
      console.error("Erreur:", err);
      setError("Erreur lors du chargement des utilisateurs");
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (userId: number) => {
    if (!window.confirm("Êtes-vous sûr de vouloir supprimer cet utilisateur ?")) {
      return;
    }

    setDeleteLoading(userId);
    try {
      await axios.delete(`http://localhost/api/users/${userId}`);
      
      // Mettre à jour la liste localement sans recharger
      setData(prev => prev.filter(user => user.id !== userId));
      setError("");
    } catch (err: any) {
      console.error("Erreur lors de la suppression:", err);
      setError("Erreur lors de la suppression de l'utilisateur");
    } finally {
      setDeleteLoading(null);
    }
  };

  const handleEdit = (userId: number) => {
    navigate(`/edit/${userId}`);
  };

  if (loading) {
    return (
      <div className="container mt-4">
        <div className="d-flex justify-content-center">
          <div className="spinner-border" role="status">
            <span className="visually-hidden">Chargement...</span>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="container mt-4">
      <div className="d-flex justify-content-between align-items-center mb-4">
        <h2>Liste des utilisateurs</h2>
        <button 
          className="btn btn-primary"
          onClick={() => navigate("/create")}
        >
          Ajouter un utilisateur
        </button>
      </div>

      {error && (
        <div className="alert alert-danger" role="alert">
          {error}
        </div>
      )}

      <div className="row">
        {data.length === 0 ? (
          <div className="col-12">
            <div className="alert alert-info">
              Aucun utilisateur trouvé.
            </div>
          </div>
        ) : (
          <div className="table-responsive">
            <table className="table table-striped table-hover">
              <thead className="table-dark">
                <tr>
                  <th>Nom</th>
                  <th>Prénom</th>
                  <th>Email</th>
                  <th>Téléphone</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {data.map((user) => (
                  <tr key={user.id}>
                    <td>{user.nom}</td>
                    <td>{user.prenom}</td>
                    <td>{user.email}</td>
                    <td>{user.telephone}</td>
                    <td>
                      <div className="btn-group" role="group">
                        <button
                          type="button"
                          className="btn btn-warning btn-sm"
                          onClick={() => handleEdit(user.id)}
                          title="Modifier"
                        >
                          <i className="bi bi-pencil"></i> Modifier
                        </button>
                        <button
                          type="button"
                          className="btn btn-danger btn-sm"
                          onClick={() => handleDelete(user.id)}
                          disabled={deleteLoading === user.id}
                          title="Supprimer"
                        >
                          {deleteLoading === user.id ? (
                            <>
                              <span className="spinner-border spinner-border-sm me-1" role="status"></span>
                              Suppression...
                            </>
                          ) : (
                            <>
                              <i className="bi bi-trash"></i> Supprimer
                            </>
                          )}
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
}