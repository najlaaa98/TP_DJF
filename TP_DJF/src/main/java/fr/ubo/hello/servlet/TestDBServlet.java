package fr.ubo.hello.servlet;


import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import fr.ubo.hello.utils.DBConnection;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

@WebServlet("/test-db")
public class TestDBServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();

        out.println("<html><body>");
        out.println("<h2>Test de connexion MySQL</h2>");

        try (Connection con = DBConnection.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT 1 as result")) {

            if (rs.next()) {
                out.println("<h3 style='color: green;'>Connexion à la base MySQL OK</h3>");
                out.println("<p>Résultat de la requête: " + rs.getInt("result") + "</p>");
            }

        } catch (Exception e) {
            out.println("<h3 style='color: red;'> Erreur de connexion</h3>");
            out.println("<p><strong>Message:</strong> " + e.getMessage() + "</p>");
            out.println("<p><strong>Type:</strong> " + e.getClass().getName() + "</p>");


            e.printStackTrace();
        }

        out.println("</body></html>");
    }
}