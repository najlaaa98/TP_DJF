package fr.ubo.hello.controller;

import fr.ubo.hello.bean.AuthRequest;
import fr.ubo.hello.bean.User;
import fr.ubo.hello.service.OTPService;
import fr.ubo.hello.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final OTPService otpService;
    private final UserService userService;

    public AuthController() {
        this.userService = new UserService();
        this.otpService = new OTPService(userService);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request, HttpSession session) {
        try {
            if (request.getEmail() == null || request.getEmail().trim().isEmpty() ||
                    request.getPassword() == null || request.getPassword().trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("{\"error\": \"Email et mot de passe requis\"}");
            }

            User user = userService.authenticate(request.getEmail(), request.getPassword());

            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("{\"error\": \"Email ou mot de passe incorrect\"}");
            }

            if (user.getTelephone() == null || user.getTelephone().trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("{\"error\": \"Aucun numéro de téléphone associé à ce compte\"}");
            }

            session.setAttribute("user", user);
            session.setAttribute("userId", user.getId());
            session.setAttribute("userPhone", user.getTelephone());

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Connexion réussie");
            response.put("user", user.getPrenom() + " " + user.getNom());
            response.put("phone", user.getTelephone());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"Erreur serveur: " + e.getMessage() + "\"}");
        }
    }

    @PostMapping("/request-otp")
    public ResponseEntity<?> requestOTP(HttpSession session) {
        try {
            User user = (User) session.getAttribute("user");
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("{\"error\": \"Non authentifié\"}");
            }

            String userPhone = user.getTelephone();

            if (userPhone == null || userPhone.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("{\"error\": \"Aucun numéro de téléphone associé à ce compte\"}");
            }

            String result = otpService.requestOTP(userPhone);

            Map<String, Object> response = new HashMap<>();
            response.put("message", result);
            response.put("phone", userPhone);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"error\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"Erreur serveur: " + e.getMessage() + "\"}");
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOTP(@RequestBody AuthRequest request, HttpSession session) {
        try {
            // Vérifier que l'utilisateur est connecté
            User user = (User) session.getAttribute("user");
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("{\"error\": \"Non authentifié\"}");
            }

            String userPhone = user.getTelephone();

            if (request.getOtpCode() == null || request.getOtpCode().trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("{\"error\": \"Code OTP requis\"}");
            }

            boolean isValid = otpService.verifyOTP(userPhone, request.getOtpCode());

            if (isValid) {
                session.setAttribute("otpVerified", true);
                return ResponseEntity.ok("{\"message\": \"Authentification OTP réussie\"}");
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("{\"error\": \"Code OTP invalide\"}");
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"Erreur serveur: " + e.getMessage() + "\"}");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok("{\"message\": \"Déconnexion réussie\"}");
    }

    @GetMapping("/status")
    public ResponseEntity<?> getAuthStatus(HttpSession session) {
        User user = (User) session.getAttribute("user");
        Boolean otpVerified = (Boolean) session.getAttribute("otpVerified");

        Map<String, Object> response = new HashMap<>();

        if (user == null) {
            response.put("authenticated", false);
        } else {
            response.put("authenticated", true);
            response.put("user", user.getPrenom() + " " + user.getNom());
            response.put("otpVerified", otpVerified != null && otpVerified);
            response.put("phone", user.getTelephone());
        }

        return ResponseEntity.ok(response);
    }

    private String cleanPhoneNumber(String phoneNumber) {
        if (phoneNumber == null) return null;
        return phoneNumber.replaceAll("[^0-9+]", "");
    }
}