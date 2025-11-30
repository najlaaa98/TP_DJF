import axios from "axios";
import { useState, useEffect } from "react";
import { useNavigate, useParams } from "react-router-dom";

interface UserForm {
  nom: string;
  prenom: string;
  email: string;
  telephone: string;
  password: string;
}

export default function ModifierUser() {
  const [formData, setFormData] = useState<UserForm>({
    nom: "",
    prenom: "",
    email: "",
    telephone: "",
    password: ""
  });
  
  const [loading, setLoading] = useState(false);
  const [loadingUser, setLoadingUser] = useState(true);
  const [error, setError] = useState("");
  const [showPassword, setShowPassword] = useState(false);
  const navigate = useNavigate();
  const { id } = useParams<{ id: string }>();

  useEffect(() => {
    const loadUser = async () => {
      if (!id) {
        setError("ID utilisateur manquant dans l'URL");
        setLoadingUser(false);
        return;
      }

      try {
        const response = await axios.get(`http://localhost:8080/api/users/${id}`);
        
        if (response.data) {
          setFormData({
            nom: response.data.nom || "",
            prenom: response.data.prenom || "",
            email: response.data.email || "",
            telephone: response.data.telephone || "",
            password: "" // Ne pas pré-remplir le mot de passe pour sécurité
          });
        } else {
          setError("Aucune donnée reçue du serveur");
        }
      } catch (err: any) {
        console.error("Erreur détaillée:", err);
        if (err.response?.status === 404) {
          setError(`Utilisateur avec ID ${id} non trouvé`);
        } else if (err.response?.data?.error) {
          setError(`Erreur serveur: ${err.response.data.error}`);
        } else {
          setError("Erreur de connexion au serveur");
        }
      } finally {
        setLoadingUser(false);
      }
    };

    loadUser();
  }, [id]);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
    
    if (error) setError("");
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!id) return;

    setLoading(true);
    setError("");

    // Validation
    if (!formData.nom.trim() || !formData.prenom.trim() || !formData.email.trim()) {
      setError("Le nom, prénom et email sont obligatoires");
      setLoading(false);
      return;
    }

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(formData.email)) {
      setError("Veuillez entrer un email valide");
      setLoading(false);
      return;
    }

    try {
      // Préparer les données (ne pas envoyer le password s'il est vide)
      const updateData: any = {
        nom: formData.nom,
        prenom: formData.prenom,
        email: formData.email,
        telephone: formData.telephone
      };

      // Ajouter le password seulement s'il est modifié
      if (formData.password.trim()) {
        updateData.password = formData.password;
      }

      await axios.put(`http://localhost:8080/api/users/${id}`, updateData);
      navigate("/");
      
    } catch (err: any) {
      console.error("Erreur modification:", err);
      setError(err.response?.data?.error || "Erreur lors de la modification");
    } finally {
      setLoading(false);
    }
  };

  const handleCancel = () => {
    navigate("/");
  };

  if (loadingUser) {
    return (
      <div className="container mt-4">
        <div className="d-flex justify-content-center align-items-center" style={{ minHeight: '50vh' }}>
          <div className="text-center">
            <div className="spinner-border text-primary mb-3" role="status">
              <span className="visually-hidden">Chargement...</span>
            </div>
            <p>Chargement de l'utilisateur...</p>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="container mt-4">
      <div className="row justify-content-center">
        <div className="col-md-8">
          <div className="card">
            <div className="card-header bg-warning text-dark">
              <h3 className="card-title mb-0">
                <i className="bi bi-pencil-square me-2"></i>
                Modifier l'utilisateur {id && `#${id}`}
              </h3>
            </div>
            
            <div className="card-body">
              {error && (
                <div className="alert alert-danger d-flex align-items-center" role="alert">
                  <i className="bi bi-exclamation-triangle-fill me-2"></i>
                  <div>{error}</div>
                </div>
              )}

              {!error && (
                <form onSubmit={handleSubmit}>
                  <div className="row">
                    <div className="col-md-6">
                      <div className="form-group mb-3">
                        <label htmlFor="nom" className="form-label">
                          Nom *
                        </label>
                        <input
                          type="text"
                          className="form-control"
                          id="nom"
                          name="nom"
                          value={formData.nom}
                          onChange={handleChange}
                          required
                          disabled={loading}
                        />
                      </div>
                    </div>
                    
                    <div className="col-md-6">
                      <div className="form-group mb-3">
                        <label htmlFor="prenom" className="form-label">
                          Prénom *
                        </label>
                        <input
                          type="text"
                          className="form-control"
                          id="prenom"
                          name="prenom"
                          value={formData.prenom}
                          onChange={handleChange}
                          required
                          disabled={loading}
                        />
                      </div>
                    </div>
                  </div>

                  <div className="form-group mb-3">
                    <label htmlFor="email" className="form-label">
                      Email *
                    </label>
                    <input
                      type="email"
                      className="form-control"
                      id="email"
                      name="email"
                      value={formData.email}
                      onChange={handleChange}
                      required
                      disabled={loading}
                    />
                  </div>

                  <div className="form-group mb-3">
                    <label htmlFor="telephone" className="form-label">
                      Téléphone
                    </label>
                    <input
                      type="tel"
                      className="form-control"
                      id="telephone"
                      name="telephone"
                      value={formData.telephone}
                      onChange={handleChange}
                      disabled={loading}
                    />
                  </div>

                  <div className="form-group mb-4">
                    <label htmlFor="password" className="form-label">
                      Nouveau mot de passe
                    </label>
                    <input
                      type="password"
                      className="form-control"
                      id="password"
                      name="password"
                      placeholder="Laissez vide pour ne pas modifier"
                      value={formData.password}
                      onChange={handleChange}
                      disabled={loading}
                    />
                    <div className="form-text">
                      Laissez vide pour conserver le mot de passe actuel
                    </div>
                  </div>

                  <div className="d-flex gap-2 justify-content-end border-top pt-3">
                    <button
                      type="button"
                      className="btn btn-outline-secondary"
                      onClick={handleCancel}
                      disabled={loading}
                    >
                      <i className="bi bi-arrow-left me-1"></i>
                      Retour
                    </button>
                    
                    <button
                      type="submit"
                      className="btn btn-warning"
                      disabled={loading}
                    >
                      {loading ? (
                        <>
                          <span className="spinner-border spinner-border-sm me-2" role="status"></span>
                          Modification...
                        </>
                      ) : (
                        <>
                          <i className="bi bi-check-lg me-1"></i>
                          Modifier
                        </>
                      )}
                    </button>
                  </div>
                </form>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}