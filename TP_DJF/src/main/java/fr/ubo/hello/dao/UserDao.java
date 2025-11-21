package fr.ubo.hello.dao;


import fr.ubo.hello.bean.User;
import java.util.List;

public interface UserDao {
    List<User> getAllUsers();
    User getUserById(int id);
    boolean addUser(User user);
    boolean updateUser(User user);
    boolean deleteUser(int id);
    boolean emailExists(String email);


    User getUserByEmail(String email);
    List<User> searchUsersByName(String name);
}