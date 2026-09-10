package com.hotel.service;

import com.hotel.exception.EmailAlreadyExistsException;
import com.hotel.exception.InvalidCredentialsException;
import com.hotel.model.User;
import com.hotel.repository.UserRepository;
import com.hotel.util.ValidationUtils;
import com.hotel.exception.BusinessException;

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
    //update profile
    // Dans AuthService.java (ou UserService)

    public void updateProfile(UUID userId, String newName, String newEmail, String newPhone) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("Utilisateur introuvable."));

        ValidationUtils.validateNotBlank(newName, "Full name");
        ValidationUtils.validateNotBlank(newEmail, "Email");
        ValidationUtils.validateEmail(newEmail);

        String trimmedEmail = newEmail.trim().toLowerCase();

        // L'email doit être unique sauf s'il appartient déjà à cet utilisateur
        if (!trimmedEmail.equalsIgnoreCase(user.getEmail()) && userRepository.existsByEmail(trimmedEmail)) {
            throw new EmailAlreadyExistsException("Cet email est déjà utilisé par un autre compte.");
        }

        user.setFullName(newName.trim());
        user.setEmail(trimmedEmail);
        user.setPhone(newPhone != null ? newPhone.trim() : "");

        userRepository.save(user);

        // Maintient l'état de la session active synchronisé
        if (this.currentUser != null) {
            this.currentUser = user;
        }
    }

    public void changePassword(UUID userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("Utilisateur introuvable."));

        if (!user.getPassword().equals(oldPassword)) {
            throw new BusinessException("L'ancien mot de passe est incorrect.");
        }

        ValidationUtils.validatePassword(newPassword);

        user.setPassword(newPassword.trim());
        userRepository.save(user);

        if (this.currentUser != null) {
            this.currentUser.setPassword(newPassword.trim());
        }
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