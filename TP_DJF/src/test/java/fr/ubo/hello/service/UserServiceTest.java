package fr.ubo.hello.service;

import fr.ubo.hello.bean.User;
import fr.ubo.hello.dao.UserDaoMock;
import fr.ubo.hello.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(true);
    }

    @Test
    void testGetAllUsers() {
        List<User> result = userService.getAllUsers();
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.size() >= 4);
    }

    @Test
    void testGetUserById() {
        User result = userService.getUserById(1);

        assertNotNull(result);
        assertEquals("Karima", result.getPrenom());
        assertEquals("GHAIT", result.getNom());
    }

    @Test
    void testGetUserById_NotFound() {
        User result = userService.getUserById(999);
        assertNull(result);
    }

    @Test
    void testAddUser_Success() {
        User newUser = new User("MIDDOUCH", "Salah", "salah.middouch@email.com", "0684885835", "salah789");
        boolean result = userService.addUser(newUser);
        assertTrue(result);
    }

    @Test
    void testAddUser_EmailAlreadyExists() {
        User newUser = new User("TEST", "User", "najlaa.azzaoui@gmail.com", "0612345678", "password");
        boolean result = userService.addUser(newUser);
        assertFalse(result);
    }

    @Test
    void testAddUser_InvalidData() {
        User invalidUser = new User("", "Ahmed", "ahmed@email.com", "0612345678", "ahmed123");
        boolean result = userService.addUser(invalidUser);
        assertFalse(result);
    }

    @Test
    void testDeleteUser_Success() {
        boolean result = userService.deleteUser(1);
        assertTrue(result);
    }

    @Test
    void testAuthenticate_Success() {
        User result = userService.authenticate("najlaa.azzaoui@gmail.com", "najlaa");
        assertNotNull(result);
        assertEquals("Najlaa", result.getPrenom());
    }

    @Test
    void testAuthenticate_WrongPassword() {
        User result = userService.authenticate("najlaa.azzaoui@gmail.com", "wrongpassword");
        assertNull(result);
    }
}