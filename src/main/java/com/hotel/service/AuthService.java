package com.hotel.service;

import com.hotel.exception.EmailAlreadyExistsException;
import com.hotel.exception.InvalidCredentialsException;
import com.hotel.model.User;
import com.hotel.repository.UserRepository;
import com.hotel.util.ValidationUtils;

import java.util.UUID;

public class AuthService {
    private final UserRepository userRepository;
    private User currentUser;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User register(String fullName, String email, String phone, String password) {
        ValidationUtils.validateNotBlank(fullName, "Full name");
        ValidationUtils.validateNotBlank(phone, "Phone number");
        ValidationUtils.validateEmail(email);
        ValidationUtils.validatePassword(password);

        String normalizedEmail = email.trim().toLowerCase();
        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new EmailAlreadyExistsException("Email already exists: " + email);
        }

        User newUser = new User(
                UUID.randomUUID(),
                fullName.trim(),
                normalizedEmail,
                phone.trim(),
                password
        );

        userRepository.save(newUser);
        return newUser;
    }

    public User login(String email, String password) {
        ValidationUtils.validateNotBlank(email, "Email");
        ValidationUtils.validateNotBlank(password, "Password");

        String normalizedEmail = email.trim().toLowerCase();
        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password."));

        if (!user.getPassword().equals(password)) {
            throw new InvalidCredentialsException("Invalid email or password.");
        }

        this.currentUser = user;
        return user;
    }

    public void logout() {
        this.currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isAuthenticated() {
        return currentUser != null;
    }
}