package fr.ubo.hello.controller;

import fr.ubo.hello.service.SMSService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap; // IMPORT AJOUTÉ
import java.util.Map;     // IMPORT AJOUTÉ

@RestController
@RequestMapping("/api/debug")
public class DebugController {

    @GetMapping("/sms-test")
    public ResponseEntity<?> testSMSConnection() {
        try {
            SMSService smsService = new SMSService();

            boolean isHealthy = smsService.isServerAvailable();
            boolean testSend = smsService.sendSMS("+33612345678", "Test OTP System");

            String result = "Health: " + (isHealthy ? "OK" : "FAIL") +
                    ", Send: " + (testSend ? "OK" : "FAIL");

            return ResponseEntity.ok("{\"message\": \"Test SMS exécuté: " + result + "\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"Erreur lors du test: \" + e.getMessage()}");
        }
    }

    @GetMapping("/sms-health")
    public ResponseEntity<?> checkSMSHealth() {
        try {
            SMSService smsService = new SMSService();
            boolean isHealthy = smsService.isServerAvailable();

            return ResponseEntity.ok("{\"healthy\": " + isHealthy + "}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"Erreur health check: \" + e.getMessage()}");
        }
    }

    // NOUVEL ENDPOINT CORRIGÉ
    @PostMapping("/test-sms-with-otp")
    public ResponseEntity<?> testSMSWithOTP(@RequestBody Map<String, String> request) {
        try {
            String phoneNumber = request.get("phoneNumber");
            String otpCode = request.get("otpCode") != null ? request.get("otpCode") : "229952";

            SMSService smsService = new SMSService();

            String message = "Votre code de vérification: " + otpCode + " - Expire dans 2 minutes";

            System.out.println("Test SMS avec OTP réel");
            System.out.println(" Téléphone: " + phoneNumber);
            System.out.println("OTP: " + otpCode);
            System.out.println("Message: " + message);

            boolean sent = smsService.sendSMS(phoneNumber, message);

            Map<String, Object> response = new HashMap<>();
            response.put("sms_sent", sent);
            response.put("phone_number", phoneNumber);
            response.put("otp_code", otpCode);
            response.put("message_content", message);

            if (sent) {
                response.put("status", "SUCCESS");
                response.put("details", "SMS avec OTP envoyé avec succès");
            } else {
                response.put("status", "FAILED");
                response.put("details", "Échec de l'envoi du SMS avec OTP");
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"Erreur test: \" + e.getMessage()}");
        }
    }

    @GetMapping("/full-sms-test")
    public ResponseEntity<?> fullSMSTest() {
        try {
            SMSService smsService = new SMSService();

            StringBuilder result = new StringBuilder();
            result.append("=== TEST COMPLET SMS ===\n\n");

            boolean health = smsService.isServerAvailable();
            result.append("1. Health Check: ").append(health ? "OK" : " FAIL").append("\n");

            if (!health) {
                result.append("Serveur inaccessible - test arrêté");
                return ResponseEntity.ok("{\"message\": \"" + result.toString() + "\"}");
            }

            // 2. Test d'envoi
            result.append("2. Test d'envoi SMS:\n");
            String testPhone = "+33612345678";
            String testMessage = "Test OTP - " + System.currentTimeMillis();

            boolean sent = smsService.sendSMS(testPhone, testMessage);
            result.append("   - Résultat: ").append(sent ? "ENVOYÉ" : " ÉCHEC").append("\n");
            result.append("   - Téléphone: ").append(testPhone).append("\n");
            result.append("   - Message: ").append(testMessage).append("\n");

            result.append("\n=== FIN DU TEST ===\n");
            result.append("Vérifiez les logs pour les détails techniques");

            return ResponseEntity.ok("{\"message\": \"" + result.toString() + "\"}");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"Test échoué: \" + e.getMessage()}");
        }
    }
}