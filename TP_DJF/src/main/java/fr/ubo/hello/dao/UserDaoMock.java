package fr.ubo.hello.dao;

import fr.ubo.hello.bean.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class UserDaoMock implements UserDao {
    private List<User> users;
    private AtomicInteger idCounter;

    public UserDaoMock() {
        this.users = new ArrayList<>();
        this.idCounter = new AtomicInteger(1);
        initializeMockData();
    }

    private void initializeMockData() {
        addUser(new User("GHAIT", "Karima", "karima.ghait@email.com", "0123456789", "password123"));
        addUser(new User("OMAHA", "Mariam", "mariam.obaha@email.com", "0987654321", "password456"));
        addUser(new User("SAMIH", "Achraf", "achraf.samih@email.com", "0654321987", "password789"));
        addUser(new User("AZZAOUI", "Najlaa", "najlaa.azzaoui@gmail.com", "0698931812", "najlaa"));
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users); // Retourne une copie
    }

    @Override
    public User getUserById(int id) {
        return users.stream()
                .filter(user -> user.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    public User getUserByEmail(String email) {
        return users.stream()
                .filter(user -> user.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElse(null);
    }

    @Override
    public boolean addUser(User user) {
        if (emailExists(user.getEmail())) {
            return false;
        }

        user.setId(idCounter.getAndIncrement());
        user.setDateCreation(LocalDateTime.now());
        user.setDateModification(LocalDateTime.now());

        users.add(user);
        System.out.println("Mock: Utilisateur ajouté - " + user.getEmail());
        return true;
    }

    @Override
    public boolean updateUser(User user) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId() == user.getId()) {
                String newEmail = user.getEmail();
                boolean emailConflict = users.stream()
                        .filter(u -> u.getId() != user.getId())
                        .anyMatch(u -> u.getEmail().equalsIgnoreCase(newEmail));

                if (emailConflict) {
                    return false;
                }

                user.setDateModification(LocalDateTime.now());
                users.set(i, user);
                System.out.println("Mock: Utilisateur modifié - ID: " + user.getId());
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean deleteUser(int id) {
        boolean removed = users.removeIf(user -> user.getId() == id);
        if (removed) {
            System.out.println("Mock: Utilisateur supprimé - ID: " + id);
        }
        return removed;
    }

    @Override
    public boolean emailExists(String email) {
        return users.stream()
                .anyMatch(user -> user.getEmail().equalsIgnoreCase(email));
    }

    @Override
    public List<User> searchUsersByName(String name) {
        String searchTerm = name.toLowerCase();
        return users.stream()
                .filter(user -> user.getNom().toLowerCase().contains(searchTerm) ||
                        user.getPrenom().toLowerCase().contains(searchTerm))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }
}