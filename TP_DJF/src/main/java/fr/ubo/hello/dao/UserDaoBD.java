package fr.ubo.hello.dao;

import fr.ubo.hello.bean.User;
import fr.ubo.hello.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDaoBD implements UserDao {

    @Override
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM user ORDER BY id";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                users.add(extractUserFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur BD getAllUsers: " + e.getMessage());
            e.printStackTrace();
        }
        return users;
    }

    @Override
    public User getUserById(int id) {
        String sql = "SELECT * FROM user WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractUserFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Erreur BD getUserById: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public User getUserByEmail(String email) {
        String sql = "SELECT * FROM user WHERE email = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractUserFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Erreur BD getUserByEmail: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean addUser(User user) {
        String sql = "INSERT INTO user (nom, prenom, email, telephone, password) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, user.getNom());
            pstmt.setString(2, user.getPrenom());
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, user.getTelephone());
            pstmt.setString(5, user.getPassword());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                // Récupérer l'ID généré
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        user.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Erreur BD addUser: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean updateUser(User user) {
        System.out.println("=== UserDaoBD.updateUser() ===");
        System.out.println("ID: " + user.getId() + ", Nom: " + user.getNom());

        // Vérifier si un nouveau mot de passe est fourni
        boolean hasNewPassword = user.getPassword() != null && !user.getPassword().trim().isEmpty();

        String sql;
        if (hasNewPassword) {
            sql = "UPDATE user SET nom = ?, prenom = ?, email = ?, telephone = ?, password = ? WHERE id = ?";
            System.out.println("Mise à jour AVEC nouveau mot de passe");
        } else {
            sql = "UPDATE user SET nom = ?, prenom = ?, email = ?, telephone = ? WHERE id = ?";
            System.out.println("Mise à jour SANS changer le mot de passe (conservation de l'ancien)");
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getNom());
            pstmt.setString(2, user.getPrenom());
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, user.getTelephone());

            if (hasNewPassword) {
                pstmt.setString(5, user.getPassword());
                pstmt.setInt(6, user.getId());
            } else {
                pstmt.setInt(5, user.getId());
            }

            int affectedRows = pstmt.executeUpdate();
            System.out.println("Lignes modifiées: " + affectedRows);
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Erreur BD updateUser: " + e.getMessage());
            System.err.println("SQL: " + sql);
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteUser(int id) {
        String sql = "DELETE FROM user WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur BD deleteUser: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean emailExists(String email) {
        String sql = "SELECT COUNT(*) FROM user WHERE email = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Erreur BD emailExists: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<User> searchUsersByName(String name) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM user WHERE nom LIKE ? OR prenom LIKE ? ORDER BY nom";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + name + "%");
            pstmt.setString(2, "%" + name + "%");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                users.add(extractUserFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur BD searchUsersByName: " + e.getMessage());
            e.printStackTrace();
        }
        return users;
    }

    private User extractUserFromResultSet(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setNom(rs.getString("nom"));
        user.setPrenom(rs.getString("prenom"));
        user.setEmail(rs.getString("email"));
        user.setTelephone(rs.getString("telephone"));
        user.setPassword(rs.getString("password"));
        user.setDateCreation(rs.getTimestamp("date_creation").toLocalDateTime());

        Timestamp modifTimestamp = rs.getTimestamp("date_modification");
        if (modifTimestamp != null) {
            user.setDateModification(modifTimestamp.toLocalDateTime());
        }

        return user;
    }
}