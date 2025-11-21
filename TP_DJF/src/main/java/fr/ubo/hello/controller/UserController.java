package fr.ubo.hello.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.ubo.hello.bean.User;
import fr.ubo.hello.service.UserService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/api/users/*")
public class UserController extends HttpServlet {
    private UserService userService;
    private ObjectMapper objectMapper;

    @Override
    public void init() throws ServletException {
        this.userService = new UserService(false); // false = BD, true = Mock
        this.objectMapper = new ObjectMapper();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        setupCorsHeaders(response);

        String pathInfo = request.getPathInfo();

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                List<User> users = userService.getAllUsers();
                String jsonResponse = objectMapper.writeValueAsString(users);
                response.getWriter().write(jsonResponse);

            } else {
                String[] pathParts = pathInfo.split("/");
                if (pathParts.length == 2) {
                    int userId = Integer.parseInt(pathParts[1]);
                    User user = userService.getUserById(userId);

                    if (user != null) {
                        String jsonResponse = objectMapper.writeValueAsString(user);
                        response.getWriter().write(jsonResponse);
                    } else {
                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        response.getWriter().write("{\"error\": \"Utilisateur non trouvé\"}");
                    }
                } else {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write("{\"error\": \"URL invalide\"}");
                }
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"ID invalide\"}");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"Erreur serveur: " + e.getMessage() + "\"}");
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        setupCorsHeaders(response);

        try {
            // Lire le JSON du body de la requête
            User user = objectMapper.readValue(request.getReader(), User.class);

            // Validation basique
            if (user.getNom() == null || user.getNom().trim().isEmpty() ||
                    user.getPrenom() == null || user.getPrenom().trim().isEmpty() ||
                    user.getEmail() == null || user.getEmail().trim().isEmpty()) {

                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\": \"Nom, prénom et email sont obligatoires\"}");
                return;
            }

            if (userService.addUser(user)) {
                response.setStatus(HttpServletResponse.SC_CREATED);
                response.getWriter().write("{\"message\": \"Utilisateur créé avec succès\", \"id\": " + user.getId() + "}");
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\": \"Erreur lors de la création ou email déjà existant\"}");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"Erreur serveur: " + e.getMessage() + "\"}");
            e.printStackTrace();
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        setupCorsHeaders(response);

        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"ID manquant\"}");
            return;
        }

        try {
            String[] pathParts = pathInfo.split("/");
            if (pathParts.length == 2) {
                int userId = Integer.parseInt(pathParts[1]);

                User user = objectMapper.readValue(request.getReader(), User.class);
                user.setId(userId);

                if (user.getNom() == null || user.getNom().trim().isEmpty() ||
                        user.getPrenom() == null || user.getPrenom().trim().isEmpty() ||
                        user.getEmail() == null || user.getEmail().trim().isEmpty()) {

                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write("{\"error\": \"Nom, prénom et email sont obligatoires\"}");
                    return;
                }

                if (userService.updateUser(user)) {
                    response.getWriter().write("{\"message\": \"Utilisateur modifié avec succès\"}");
                } else {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write("{\"error\": \"Erreur lors de la modification ou email déjà utilisé\"}");
                }
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\": \"URL invalide\"}");
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"ID invalide\"}");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"Erreur serveur: " + e.getMessage() + "\"}");
            e.printStackTrace();
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        setupCorsHeaders(response);

        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"ID manquant\"}");
            return;
        }

        try {
            String[] pathParts = pathInfo.split("/");
            if (pathParts.length == 2) {
                int userId = Integer.parseInt(pathParts[1]);

                if (userService.deleteUser(userId)) {
                    response.getWriter().write("{\"message\": \"Utilisateur supprimé avec succès\"}");
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.getWriter().write("{\"error\": \"Utilisateur non trouvé\"}");
                }
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\": \"URL invalide\"}");
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"ID invalide\"}");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"Erreur serveur: " + e.getMessage() + "\"}");
            e.printStackTrace();
        }
    }


    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        setupCorsHeaders(response);
        response.setStatus(HttpServletResponse.SC_OK);
    }

    private void setupCorsHeaders(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        response.setHeader("Access-Control-Max-Age", "3600");
    }
}