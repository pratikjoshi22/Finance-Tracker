package com.financeapp.personalfinance.service;

import com.financeapp.personalfinance.model.User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class UserService {

    // In-memory storage (will be replaced with database in Phase 2)
    private final Map<Long, User> users = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    // Create a new user
    public User createUser(User user) {
        // Validate required fields
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (user.getFirstName() == null || user.getFirstName().trim().isEmpty()) {
            throw new IllegalArgumentException("First name is required");
        }

        // Check if email already exists
        if (emailExists(user.getEmail())) {
            throw new IllegalArgumentException("User with this email already exists");
        }

        // Generate ID and save
        user.setId(idGenerator.getAndIncrement());
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        users.put(user.getId(), user);
        return user;
    }

    // Get user by ID
    public Optional<User> getUserById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    // Get user by email
    public Optional<User> getUserByEmail(String email) {
        return users.values().stream()
                .filter(user -> user.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }

    // Get all users
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    // Update user
    public User updateUser(Long id, User updatedUser) {
        User existingUser = users.get(id);
        if (existingUser == null) {
            throw new RuntimeException("User not found with id: " + id);
        }

        // Check if email is being changed and new email already exists
        if (!existingUser.getEmail().equals(updatedUser.getEmail()) &&
                emailExists(updatedUser.getEmail())) {
            throw new IllegalArgumentException("User with this email already exists");
        }

        // Update fields
        existingUser.setFirstName(updatedUser.getFirstName());
        existingUser.setLastName(updatedUser.getLastName());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setPhoneNumber(updatedUser.getPhoneNumber());
        existingUser.setUpdatedAt(LocalDateTime.now());

        return existingUser;
    }

    // Delete user
    public boolean deleteUser(Long id) {
        return users.remove(id) != null;
    }

    // Check if email exists
    private boolean emailExists(String email) {
        return users.values().stream()
                .anyMatch(user -> user.getEmail().equalsIgnoreCase(email));
    }

    // Get total user count
    public long getUserCount() {
        return users.size();
    }

    // Search users by name
    public List<User> searchUsersByName(String searchTerm) {
        return users.values().stream()
                .filter(user ->
                        user.getFirstName().toLowerCase().contains(searchTerm.toLowerCase()) ||
                                user.getLastName().toLowerCase().contains(searchTerm.toLowerCase()))
                .toList();
    }
}