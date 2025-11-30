package fr.ubo.hello.service;

import fr.ubo.hello.bean.OTPRequest;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class OTPService {
    private final UserService userService;
    private final SMSService smsService;

    private final ConcurrentHashMap<String, LocalDateTime> lastRequestTime = new ConcurrentHashMap<>();

    private static final int OTP_EXPIRATION_MINUTES = 2;
    private static final int OTP_RETRY_DELAY_SECONDS = 30;
    private static final boolean DEV_MODE = true;

    public OTPService(UserService userService) {
        this.userService = userService;
        this.smsService = new SMSService();
    }

    public String requestOTP(String phoneNumber) {
        String cleanPhone = cleanPhoneNumber(phoneNumber);

        if (!userService.canRequestOTP(cleanPhone)) {
            throw new RuntimeException("Veuillez attendre " + OTP_RETRY_DELAY_SECONDS + " secondes entre deux demandes");
        }

        String otpCode = generateOTP();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(OTP_EXPIRATION_MINUTES);

        OTPRequest otpRequest = new OTPRequest(cleanPhone, otpCode, expiresAt);
        userService.saveOTPRequest(otpRequest);

        System.out.println(" OTP Généré: " + otpCode + " pour " + cleanPhone);

        boolean sent = false;
        String smsMessage = "Votre code de vérification: " + otpCode + " - Expire dans " + OTP_EXPIRATION_MINUTES + " minutes";

        System.out.println(" Tentative d'envoi SMS via serveur...");

        try {
            sent = smsService.sendSMS(cleanPhone, smsMessage);

            if (!sent) {
                System.out.println("Premier envoi échoué, tentative de retry...");
                sent = smsService.sendSMS(cleanPhone, smsMessage);
            }
        } catch (Exception e) {
            System.err.println("Exception lors de l'envoi SMS: " + e.getMessage());
        }

        if (sent) {
            userService.recordOTPRequestTime(cleanPhone);
            System.out.println("OTP envoyé avec succès par SMS");
            return "OTP envoyé avec succès par SMS";
        } else {
            System.out.println("Mode développement activé - SMS non envoyé");
            System.out.println("=== MODE DÉVELOPPEMENT ===");
            System.out.println("OTP Généré: " + otpCode);
            System.out.println(" Pour le numéro: " + cleanPhone);
            System.out.println("Expire à: " + expiresAt);
            System.out.println("=== FIN MODE DÉVELOPPEMENT ===");

            userService.recordOTPRequestTime(cleanPhone);
            return "OTP généré (mode développement): " + otpCode;
        }
    }

    public boolean verifyOTP(String phoneNumber, String otpCode) {
        String cleanPhone = cleanPhoneNumber(phoneNumber);

        OTPRequest otpRequest = userService.findValidOTP(cleanPhone, LocalDateTime.now());

        if (otpRequest != null && otpRequest.getOtpCode().equals(otpCode)) {
            userService.markOTPAsUsed(otpRequest.getId());
            return true;
        }

        if (otpRequest != null) {
            userService.incrementOTPAttempts(otpRequest.getId());
        }

        return false;
    }

    private String cleanPhoneNumber(String phoneNumber) {
        if (phoneNumber == null) return null;
        return phoneNumber.replaceAll("[^0-9+]", "");
    }

    private String generateOTP() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }
}