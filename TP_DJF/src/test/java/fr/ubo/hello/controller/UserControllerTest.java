package fr.ubo.hello.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.ubo.hello.bean.User;
import fr.ubo.hello.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserControllerTest {

    private UserService userService;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private UserController userController;
    private StringWriter responseWriter;

    @BeforeEach
    void setUp() throws Exception {
        userService = mock(UserService.class);
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);

        userController = new UserController();

        var serviceField = UserController.class.getDeclaredField("userService");
        serviceField.setAccessible(true);
        serviceField.set(userController, userService);

        var mapperField = UserController.class.getDeclaredField("objectMapper");
        mapperField.setAccessible(true);
        mapperField.set(userController, new ObjectMapper());

        responseWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));
    }

    @Test
    void testDoGet_AllUsers() throws Exception {
        when(request.getPathInfo()).thenReturn(null);


        userController.doGet(request, response);

        verify(response).setContentType("application/json");
        verify(response).setCharacterEncoding("UTF-8");
    }

    @Test
    void testDoGet_UserById() throws Exception {
        // Given
        when(request.getPathInfo()).thenReturn("/1");
        User user = new User("AZZAOUI", "Najlaa", "najlaa.azzaoui@gmail.com", "0698931812", "najlaa");
        user.setId(1);
        when(userService.getUserById(1)).thenReturn(user);

        userController.doGet(request, response);

        String responseContent = responseWriter.toString();
        assertTrue(responseContent.contains("Najlaa") || responseContent.contains("AZZAOUI"));
    }

    @Test
    void testDoPost_CreateUser_Success() throws Exception {
        when(request.getPathInfo()).thenReturn(null);

        User newUser = new User("MIDDOUCH", "Salah", "salah.middouch@gmail.com", "0684885835", "salah789");
        String userJson = new ObjectMapper().writeValueAsString(newUser);
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(userJson)));
        when(userService.addUser(any(User.class))).thenReturn(true);


        userController.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_CREATED);
    }
}