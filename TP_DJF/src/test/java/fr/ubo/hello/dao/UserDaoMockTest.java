package fr.ubo.hello.dao;

import fr.ubo.hello.bean.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserDaoMockTest {

    private UserDaoMock userDaoMock;

    @BeforeEach
    void setUp() {
        userDaoMock = new UserDaoMock();
    }

    @Test
    void testGetAllUsers() {
        List<User> users = userDaoMock.getAllUsers();

        assertNotNull(users);
        assertFalse(users.isEmpty());
        assertTrue(users.size() >= 4);
    }

    @Test
    void testGetUserById() {
        User user = userDaoMock.getUserById(1);

        assertNotNull(user);
        assertEquals(1, user.getId());
        assertEquals("Karima", user.getPrenom());
    }

    @Test
    void testGetUserById_NotFound() {
        User user = userDaoMock.getUserById(999);

        assertNull(user);
    }

    @Test
    void testAddUser_Success() {
        User newUser = new User("BENNANI", "Leila", "leila.bennani@email.com", "0678901234", "leila123");

        boolean result = userDaoMock.addUser(newUser);

        assertTrue(result);
    }

    @Test
    void testDeleteUser() {
        boolean result = userDaoMock.deleteUser(1);
        assertTrue(result);
        assertNull(userDaoMock.getUserById(1));
    }
}