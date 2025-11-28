package fr.ubo.hello.controller;

import fr.ubo.hello.bean.User;
import fr.ubo.hello.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;


    public UserController() {

        this.userService = new UserService(false);
    }

    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        try {
            List<User> users = userService.getAllUsers();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"Erreur serveur: " + e.getMessage() + "\"}");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable("id") int userId) {
        try {
            User user = userService.getUserById(userId);
            if (user != null) {
                return ResponseEntity.ok(user);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("{\"error\": \"Utilisateur non trouvé\"}");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"Erreur serveur: " + e.getMessage() + "\"}");
        }
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user) {
        try {
            if (user.getNom() == null || user.getNom().trim().isEmpty() ||
                    user.getPrenom() == null || user.getPrenom().trim().isEmpty() ||
                    user.getEmail() == null || user.getEmail().trim().isEmpty()) {

                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("{\"error\": \"Nom, prénom et email sont obligatoires\"}");
            }

            if (userService.addUser(user)) {
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body("{\"message\": \"Utilisateur créé avec succès\", \"id\": " + user.getId() + "}");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("{\"error\": \"Erreur lors de la création ou email déjà existant\"}");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"Erreur serveur: " + e.getMessage() + "\"}");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable("id") int userId, @RequestBody User user) {
        try {
            user.setId(userId);

            if (user.getNom() == null || user.getNom().trim().isEmpty() ||
                    user.getPrenom() == null || user.getPrenom().trim().isEmpty() ||
                    user.getEmail() == null || user.getEmail().trim().isEmpty()) {

                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("{\"error\": \"Nom, prénom et email sont obligatoires\"}");
            }

            if (userService.updateUser(user)) {
                return ResponseEntity.ok("{\"message\": \"Utilisateur modifié avec succès\"}");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("{\"error\": \"Erreur lors de la modification ou email déjà utilisé\"}");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"Erreur serveur: " + e.getMessage() + "\"}");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable("id") int userId) {
        try {
            if (userService.deleteUser(userId)) {
                return ResponseEntity.ok("{\"message\": \"Utilisateur supprimé avec succès\"}");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("{\"error\": \"Utilisateur non trouvé\"}");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"Erreur serveur: " + e.getMessage() + "\"}");
        }
    }
}