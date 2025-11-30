package fr.ubo.hello.service;

import fr.ubo.hello.bean.User;
import fr.ubo.hello.bean.OTPRequest;
import fr.ubo.hello.dao.UserDao;
import fr.ubo.hello.dao.UserDaoBD;
import fr.ubo.hello.dao.UserDaoMock;
import fr.ubo.hello.dao.OTPDao;
import fr.ubo.hello.dao.OTPDaoBD;
import fr.ubo.hello.dao.OTPDaoMock;

import java.util.List;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

public class UserService {
    private UserDao userDao;
    private OTPDao otpDao;

    private final ConcurrentHashMap<String, LocalDateTime> lastOTPRequestTime = new ConcurrentHashMap<>();

    public UserService() {
        this.userDao = new UserDaoBD();
        this.otpDao = new OTPDaoBD();
        System.out.println("Utilisation des DAO Base de Données");
    }

    public UserService(boolean useMock) {
        if (useMock) {
            this.userDao = new UserDaoMock();
            this.otpDao = new OTPDaoMock();
            System.out.println("Utilisation des DAO Mock");
        } else {
            this.userDao = new UserDaoBD();
            this.otpDao = new OTPDaoBD();
            System.out.println("Utilisation des DAO Base de Données");
        }
    }

    public UserService(UserDao userDao) {
        this.userDao = userDao;
        this.otpDao = new OTPDaoBD(); // Par défaut, OTP en base
    }

    public UserService(UserDao userDao, OTPDao otpDao) {
        this.userDao = userDao;
        this.otpDao = otpDao;
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
        this.otpDao = new OTPDaoMock();
        System.out.println("Passage aux DAO Mock");
    }

    public void switchToDatabase() {
        this.userDao = new UserDaoBD();
        this.otpDao = new OTPDaoBD();
        System.out.println("Passage aux DAO Base de Données");
    }



    public void saveOTPRequest(OTPRequest otpRequest) {
        try {
            boolean success = otpDao.saveOTPRequest(otpRequest);
            if (success) {
                System.out.println("OTP sauvegardé avec succès pour: " + otpRequest.getPhoneNumber());
            } else {
                System.err.println("Échec de sauvegarde OTP pour: " + otpRequest.getPhoneNumber());
            }
        } catch (Exception e) {
            System.err.println("Erreur sauvegarde OTP: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public OTPRequest findValidOTP(String phoneNumber, LocalDateTime now) {
        try {
            OTPRequest otp = otpDao.findValidOTP(phoneNumber, now);
            if (otp != null) {
                System.out.println("OTP valide trouvé pour: " + phoneNumber);
            } else {
                System.out.println("Aucun OTP valide trouvé pour: " + phoneNumber);
            }
            return otp;
        } catch (Exception e) {
            System.err.println("Erreur recherche OTP: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }


    public void markOTPAsUsed(int otpId) {
        try {
            boolean success = otpDao.markOTPAsUsed(otpId);
            if (success) {
                System.out.println("OTP marqué comme utilisé: " + otpId);
            } else {
                System.err.println("Échec marquage OTP comme utilisé: " + otpId);
            }
        } catch (Exception e) {
            System.err.println("Erreur marquage OTP utilisé: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public void incrementOTPAttempts(int otpId) {
        try {
            boolean success = otpDao.incrementOTPAttempts(otpId);
            if (success) {
                System.out.println("Tentative OTP incrémentée: " + otpId);
            }
        } catch (Exception e) {
            System.err.println("Erreur incrémentation tentatives OTP: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public boolean canRequestOTP(String phoneNumber) {
        LocalDateTime lastRequest = lastOTPRequestTime.get(phoneNumber);
        if (lastRequest == null) {
            return true;
        }

        LocalDateTime now = LocalDateTime.now();
        boolean canRequest = lastRequest.plusSeconds(30).isBefore(now);

        if (!canRequest) {
            long secondsRemaining = java.time.Duration.between(now, lastRequest.plusSeconds(30)).getSeconds();
            System.out.println("Délai OTP non respecté pour " + phoneNumber +
                    ". Attendez encore " + secondsRemaining + " secondes.");
        }

        return canRequest;
    }


    public void recordOTPRequestTime(String phoneNumber) {
        lastOTPRequestTime.put(phoneNumber, LocalDateTime.now());
        System.out.println("Temps OTP enregistré pour: " + phoneNumber);
    }


    public void cleanupExpiredOTPs() {
        try {
            int deletedCount = otpDao.cleanupExpiredOTPs(LocalDateTime.now());
            if (deletedCount > 0) {
                System.out.println("Nettoyage OTP: " + deletedCount + " OTPs expirés supprimés");
            }
        } catch (Exception e) {
            System.err.println("Erreur nettoyage OTP expirés: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public int getRemainingAttempts(int otpId) {
        try {
            return otpDao.getRemainingAttempts(otpId);
        } catch (Exception e) {
            System.err.println("Erreur obtention tentatives restantes: " + e.getMessage());
            return 3;
        }
    }


    public boolean isOTPBlocked(int otpId) {
        try {
            return otpDao.isOTPBlocked(otpId);
        } catch (Exception e) {
            System.err.println("Erreur vérification blocage OTP: " + e.getMessage());
            return false;
        }
    }


    public String getOTPStats() {
        try {
            int totalRequests = otpDao.getTotalOTPRequests();
            int activeRequests = otpDao.getActiveOTPRequests(LocalDateTime.now());
            int usedRequests = otpDao.getUsedOTPRequests();

            return String.format("Statistiques OTP - Total: %d, Actifs: %d, Utilisés: %d",
                    totalRequests, activeRequests, usedRequests);
        } catch (Exception e) {
            return "Erreur lors de la récupération des statistiques OTP: " + e.getMessage();
        }
    }
}