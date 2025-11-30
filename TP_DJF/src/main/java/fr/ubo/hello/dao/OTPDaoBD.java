package fr.ubo.hello.dao;

import fr.ubo.hello.bean.OTPRequest;
import java.time.LocalDateTime;
import java.sql.*;

public class OTPDaoBD implements OTPDao {
    private Connection getConnection() throws SQLException {

        return DriverManager.getConnection(
                "jdbc:mysql://mysql-db:3306/guser", "root", "1234");
    }

    @Override
    public boolean saveOTPRequest(OTPRequest otpRequest) {
        String sql = "INSERT INTO otp_requests (phone_number, otp_code, expires_at) VALUES (?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, otpRequest.getPhoneNumber());
            stmt.setString(2, otpRequest.getOtpCode());
            stmt.setTimestamp(3, Timestamp.valueOf(otpRequest.getExpiresAt()));

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        otpRequest.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Erreur sauvegarde OTP: " + e.getMessage());
        }
        return false;
    }

    @Override
    public OTPRequest findValidOTP(String phoneNumber, LocalDateTime now) {
        String sql = "SELECT * FROM otp_requests WHERE phone_number = ? AND used = 0 AND expires_at > ? AND attempts < 3 ORDER BY created_at DESC LIMIT 1";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, phoneNumber);
            stmt.setTimestamp(2, Timestamp.valueOf(now));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    OTPRequest otp = new OTPRequest();
                    otp.setId(rs.getInt("id"));
                    otp.setPhoneNumber(rs.getString("phone_number"));
                    otp.setOtpCode(rs.getString("otp_code"));
                    otp.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    otp.setExpiresAt(rs.getTimestamp("expires_at").toLocalDateTime());
                    otp.setUsed(rs.getBoolean("used"));
                    otp.setAttempts(rs.getInt("attempts"));
                    return otp;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur recherche OTP: " + e.getMessage());
        }
        return null;
    }

    @Override
    public boolean markOTPAsUsed(int otpId) {
        String sql = "UPDATE otp_requests SET used = 1 WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, otpId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Erreur marquage OTP utilisé: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean incrementOTPAttempts(int otpId) {
        String sql = "UPDATE otp_requests SET attempts = attempts + 1 WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, otpId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Erreur incrémentation tentatives: " + e.getMessage());
        }
        return false;
    }

    @Override
    public int cleanupExpiredOTPs(LocalDateTime now) {
        String sql = "DELETE FROM otp_requests WHERE expires_at <= ? OR attempts >= 3";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setTimestamp(1, Timestamp.valueOf(now));
            return stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Erreur nettoyage OTP: " + e.getMessage());
        }
        return 0;
    }

    @Override
    public int getRemainingAttempts(int otpId) {
        String sql = "SELECT attempts FROM otp_requests WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, otpId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return 3 - rs.getInt("attempts");
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur obtention tentatives: " + e.getMessage());
        }
        return 0;
    }

    @Override
    public boolean isOTPBlocked(int otpId) {
        String sql = "SELECT attempts FROM otp_requests WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, otpId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("attempts") >= 3;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur vérification blocage: " + e.getMessage());
        }
        return false;
    }

    @Override
    public int getTotalOTPRequests() {
        String sql = "SELECT COUNT(*) as total FROM otp_requests";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            System.err.println("Erreur comptage total OTP: " + e.getMessage());
        }
        return 0;
    }

    @Override
    public int getActiveOTPRequests(LocalDateTime now) {
        String sql = "SELECT COUNT(*) as active FROM otp_requests WHERE used = 0 AND expires_at > ? AND attempts < 3";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setTimestamp(1, Timestamp.valueOf(now));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("active");
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur comptage OTP actifs: " + e.getMessage());
        }
        return 0;
    }

    @Override
    public int getUsedOTPRequests() {
        String sql = "SELECT COUNT(*) as used FROM otp_requests WHERE used = 1";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("used");
            }
        } catch (SQLException e) {
            System.err.println("Erreur comptage OTP utilisés: " + e.getMessage());
        }
        return 0;
    }
}