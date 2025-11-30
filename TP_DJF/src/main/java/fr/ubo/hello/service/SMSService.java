package fr.ubo.hello.service;

import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import java.util.*;

public class SMSService {
    private static final String SMS_SERVER_URL = "http://dosipa.univ-brest.fr";
    private static final String API_KEY = "DOSITPDJF";
    private final RestTemplate restTemplate;

    public SMSService() {
        this.restTemplate = new RestTemplate();
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000);
        factory.setReadTimeout(10000);
        restTemplate.setRequestFactory(factory);
    }

    public boolean isServerAvailable() {
        try {

            HttpHeaders headers = new HttpHeaders();
            headers.set("x-api-key", API_KEY);
            headers.set("accept", "application/json");

            Map<String, String> body = new HashMap<>();
            body.put("to", "0600000000");
            body.put("message", "health check");

            HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(
                    SMS_SERVER_URL + "/send-sms",
                    request,
                    String.class
            );

            boolean available = response.getStatusCode().is2xxSuccessful();
            System.out.println("Serveur SMS disponible: " + available);
            return available;

        } catch (Exception e) {
            System.err.println("Serveur SMS inaccessible: " + e.getMessage());
            return false;
        }
    }

    public boolean sendSMS(String phoneNumber, String message) {
        System.out.println("ENVOI SMS vers: " + phoneNumber);

        String cleanPhone = cleanPhoneNumber(phoneNumber);
        System.out.println("Numéro nettoyé: " + cleanPhone);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("x-api-key", API_KEY);
            headers.set("accept", "application/json");

            Map<String, String> body = new HashMap<>();
            body.put("to", cleanPhone);
            body.put("message", message);

            HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

            String fullUrl = SMS_SERVER_URL + "/send-sms";
            System.out.println("Envoi vers: " + fullUrl);

            ResponseEntity<String> response = restTemplate.postForEntity(fullUrl, request, String.class);

            System.out.println("Réponse - Status: " + response.getStatusCode());
            System.out.println("Body: " + response.getBody());

            boolean success = response.getStatusCode().is2xxSuccessful();

            if (success) {
                System.out.println("SMS envoyé avec succès");
            } else {
                System.out.println("Échec - Status: " + response.getStatusCode());
            }

            return success;

        } catch (Exception e) {
            System.err.println("Erreur: " + e.getClass().getSimpleName());
            System.err.println("Message: " + e.getMessage());

            if (e instanceof org.springframework.web.client.HttpClientErrorException) {
                org.springframework.web.client.HttpClientErrorException httpEx =
                        (org.springframework.web.client.HttpClientErrorException) e;
                System.err.println("Body erreur: " + httpEx.getResponseBodyAsString());
            }

            return false;
        }
    }

    private String cleanPhoneNumber(String phoneNumber) {
        if (phoneNumber == null) return null;

        String clean = phoneNumber.replaceAll("[^0-9+]", "");

        if (clean.startsWith("+33")) {
            clean = "0" + clean.substring(3);
        } else if (clean.startsWith("0033")) {
            clean = "0" + clean.substring(4);
        } else if (clean.startsWith("33") && clean.length() == 11) {
            clean = "0" + clean.substring(2);
        }

        return clean;
    }
}