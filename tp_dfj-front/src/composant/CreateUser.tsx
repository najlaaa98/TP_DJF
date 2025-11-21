import axios from "axios";
import { useState } from "react";
import { useNavigate } from "react-router-dom";

interface UserForm {
  nom: string;
  prenom: string;
  email: string;
  telephone: string;
  password: string;
}

export default function CreateUser() {
  const [formData, setFormData] = useState<UserForm>({
    nom: "",
    prenom: "",
    email: "",
    telephone: "",
    password: "" 
  });
  
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [showPassword, setShowPassword] = useState(false); 
  const navigate = useNavigate();

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
    setLoading(true);
    setError("");

    if (!formData.nom.trim() || !formData.prenom.trim() || !formData.email.trim() || !formData.password.trim()) {
      setError("Le nom, prénom, email et mot de passe sont obligatoires");
      setLoading(false);
      return;
    }

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(formData.email)) {
      setError("Veuillez entrer un email valide");
      setLoading(false);
      return;
    }

    if (formData.password.length < 6) {
      setError("Le mot de passe doit contenir au moins 6 caractères");
      setLoading(false);
      return;
    }

    try {
      const response = await axios.post("http://localhost:8080/api/users", formData, {
        headers: {
          'Content-Type': 'application/json'
        }
      });

      if (response.status === 201) {
        navigate("/");
      }
    } catch (err: any) {
      console.error("Erreur lors de la création:", err);
      
      if (err.response) {
        const errorMessage = err.response.data.error || "Erreur lors de la création de l'utilisateur";
        setError(errorMessage);
      } else if (err.request) {
        setError("Erreur de connexion au serveur");
      } else {
        setError("Une erreur inattendue s'est produite");
      }
    } finally {
      setLoading(false);
    }
  };

  const handleCancel = () => {
    navigate("/");
  };

  const togglePasswordVisibility = () => {
    setShowPassword(!showPassword);
  };

  return (
    <div className="container-fluid mt-4">
      <div className="row justify-content-center">
        <div className="col-12 col-xl-10">
          <div className="card shadow-lg">
            <div className="card-header bg-primary text-white">
              <h3 className="card-title mb-0">
                <i className="bi bi-person-plus me-2"></i>
                Créer un nouvel utilisateur
              </h3>
            </div>
            
            <div className="card-body p-4">
              {error && (
                <div className="alert alert-danger d-flex align-items-center" role="alert">
                  <i className="bi bi-exclamation-triangle-fill me-2"></i>
                  <div>{error}</div>
                </div>
              )}

              <form onSubmit={handleSubmit}>
                <div className="row">
                  <div className="col-md-6">
                    <div className="form-group mb-4">
                      <label htmlFor="nom" className="form-label fw-bold">
                        Nom <span className="text-danger">*</span>
                      </label>
                      <input
                        type="text"
                        className="form-control form-control-lg"
                        id="nom"
                        name="nom"
                        placeholder="Entrez le nom"
                        value={formData.nom}
                        onChange={handleChange}
                        required
                        disabled={loading}
                      />
                    </div>

                    <div className="form-group mb-4">
                      <label htmlFor="prenom" className="form-label fw-bold">
                        Prénom <span className="text-danger">*</span>
                      </label>
                      <input
                        type="text"
                        className="form-control form-control-lg"
                        id="prenom"
                        name="prenom"
                        placeholder="Entrez le prénom"
                        value={formData.prenom}
                        onChange={handleChange}
                        required
                        disabled={loading}
                      />
                    </div>

                    <div className="form-group mb-4">
                      <label htmlFor="email" className="form-label fw-bold">
                        Email <span className="text-danger">*</span>
                      </label>
                      <input
                        type="email"
                        className="form-control form-control-lg"
                        id="email"
                        name="email"
                        placeholder="Entrez l'email"
                        value={formData.email}
                        onChange={handleChange}
                        required
                        disabled={loading}
                      />
                    </div>
                  </div>

                  <div className="col-md-6">
                    <div className="form-group mb-4">
                      <label htmlFor="password" className="form-label fw-bold">
                        Mot de passe <span className="text-danger">*</span>
                      </label>
                      <div className="input-group">
                        <input
                          type={showPassword ? "text" : "password"}
                          className="form-control form-control-lg"
                          id="password"
                          name="password"
                          placeholder="Entrez le mot de passe (min. 6 caractères)"
                          value={formData.password}
                          onChange={handleChange}
                          required
                          disabled={loading}
                          minLength={6}
                        />
                        <button
                          type="button"
                          className="btn btn-outline-secondary btn-lg"
                          onClick={togglePasswordVisibility}
                          disabled={loading}
                        >
                          {showPassword ? (
                            <i className="bi bi-eye-slash"></i>
                          ) : (
                            <i className="bi bi-eye"></i>
                          )}
                        </button>
                      </div>
                      <div className="form-text mt-2">
                        <i className="bi bi-info-circle me-1"></i>
                        Le mot de passe doit contenir au moins 6 caractères.
                      </div>
                    </div>

                    <div className="form-group mb-4">
                      <label htmlFor="telephone" className="form-label fw-bold">
                        Téléphone
                      </label>
                      <input
                        type="tel"
                        className="form-control form-control-lg"
                        id="telephone"
                        name="telephone"
                        placeholder="Entrez le numéro de téléphone"
                        value={formData.telephone}
                        onChange={handleChange}
                        disabled={loading}
                      />
                    </div>
                  </div>
                </div>

                <div className="row mt-4">
                  <div className="col-12">
                    <div className="d-flex gap-3 justify-content-end border-top pt-4">
                      <button
                        type="button"
                        className="btn btn-secondary btn-lg px-4"
                        onClick={handleCancel}
                        disabled={loading}
                      >
                        <i className="bi bi-arrow-left me-2"></i>
                        Annuler
                      </button>
                      
                      <button
                        type="submit"
                        className="btn btn-primary btn-lg px-4"
                        disabled={loading}
                      >
                        {loading ? (
                          <>
                            <span className="spinner-border spinner-border-sm me-2" role="status"></span>
                            Création en cours...
                          </>
                        ) : (
                          <>
                            <i className="bi bi-check-lg me-2"></i>
                            Créer l'utilisateur
                          </>
                        )}
                      </button>
                    </div>
                  </div>
                </div>
              </form>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}