package com.financeapp.personalfinance.service;

import com.financeapp.personalfinance.model.User;
import com.financeapp.personalfinance.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    // In-memory storage (will be replaced with database in Phase 2)


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

        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        // Save user entity to DB — this assigns the ID automatically if annotated properly
        return userRepository.save(user);
    }

    // Get user by ID
    // Get user by ID
    @Transactional(readOnly = true)
    public Optional<User> getUserById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("User ID must be a positive number");
        }
        return userRepository.findById(id);
    }


    // Get user by email
    @Transactional(readOnly = true)
    public Optional<User> getUserByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        return userRepository.findByEmail(email.trim().toLowerCase());
    }

    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Update user
    public User updateUser(Long id, User updatedUser) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("User ID must be a positive number");
        }
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

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

        // Save and return updated user
        return userRepository.save(existingUser);
    }

    // Delete user
    public boolean deleteUser(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("User ID must be a positive number");
        }

        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Transactional(readOnly = true)
    public boolean emailExists(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return userRepository.findByEmail(email.trim().toLowerCase()).isPresent();
    }

    @Transactional(readOnly = true)
    public long getUserCount() {
        return userRepository.count();
    }

    @Transactional(readOnly = true)
    public List<User> searchByFirstName(String firstName) {
        if (firstName == null || firstName.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return userRepository.findByFirstNameContainingIgnoreCase(firstName.trim());
    }
}