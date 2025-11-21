package fr.ubo.hello.service;

import fr.ubo.hello.bean.User;
import fr.ubo.hello.dao.UserDao;
import fr.ubo.hello.dao.UserDaoBD;
import fr.ubo.hello.dao.UserDaoMock;

import java.util.List;

public class UserService {
    private UserDao userDao;


    public UserService() {
        this.userDao = new UserDaoBD();
    }

    public UserService(boolean useMock) {
        if (useMock) {
            this.userDao = new UserDaoMock();
            System.out.println("Utilisation du DAO Mock");
        } else {
            this.userDao = new UserDaoBD();
            System.out.println("Utilisation du DAO Base de Données");
        }
    }


    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }


    public List<User> getAllUsers() {
        return userDao.getAllUsers();
    }

    public User getUserById(int id) {
        return userDao.getUserById(id);
    }

    public boolean addUser(User user) {
        if (user.getNom() == null || user.getNom().trim().isEmpty() ||
                user.getPrenom() == null || user.getPrenom().trim().isEmpty() ||
                user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            return false;
        }

        if (userDao.emailExists(user.getEmail())) {
            return false;
        }

        return userDao.addUser(user);
    }

    public boolean updateUser(User user) {
        System.out.println("UserService: updateUser() appelé pour ID: " + user.getId());
        try {
            if (user.getNom() == null || user.getNom().trim().isEmpty() ||
                    user.getPrenom() == null || user.getPrenom().trim().isEmpty() ||
                    user.getEmail() == null || user.getEmail().trim().isEmpty()) {
                System.out.println("UserService: Validation échouée - champs obligatoires manquants");
                return false;
            }

            User existingUser = userDao.getUserById(user.getId());
            if (existingUser == null) {
                System.out.println("UserService: Utilisateur non trouvé pour mise à jour, ID: " + user.getId());
                return false;
            }

            if (!existingUser.getEmail().equals(user.getEmail())) {
                if (userDao.emailExists(user.getEmail())) {
                    System.out.println("UserService: Email déjà utilisé par un autre utilisateur: " + user.getEmail());
                    return false;
                }
            }

            if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
                user.setPassword(existingUser.getPassword());
                System.out.println("UserService: Conservation de l'ancien mot de passe");
            } else {
                System.out.println("UserService: Utilisation du nouveau mot de passe");
            }

            boolean result = userDao.updateUser(user);
            System.out.println("UserService: updateUser résultat: " + result);
            return result;
        } catch (Exception e) {
            System.err.println("UserService - Erreur updateUser: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteUser(int id) {
        return userDao.deleteUser(id);
    }

    public User authenticate(String email, String password) {
        User user = userDao.getUserByEmail(email);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }


    public void switchToMock() {
        this.userDao = new UserDaoMock();
        System.out.println("passage au DAO Mock");
    }

    public void switchToDatabase() {
        this.userDao = new UserDaoBD();
        System.out.println("Passage au DAO Base de Données");
    }
}