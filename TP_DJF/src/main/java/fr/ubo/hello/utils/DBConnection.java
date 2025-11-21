package fr.ubo.hello.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    public static Connection getConnection() throws SQLException {
        try {
            String url = "jdbc:mysql://mysql-db:3306/guser";
            String user = "root";
            String password = "1234";

            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection connection = DriverManager.getConnection(url, user, password);
            System.out.println("Connexion MySQL réussie !");
            return connection;

        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver MySQL non trouvé", e);
        } catch (SQLException e) {
            System.err.println("Erreur de connexion MySQL: " + e.getMessage());
            throw e;
        }
    }
}