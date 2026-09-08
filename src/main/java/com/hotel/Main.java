package com.hotel;

import com.hotel.exception.BusinessException;
import com.hotel.model.User;
import com.hotel.repository.UserRepository;
import com.hotel.repository.impl.InMemoryUserRepository;
import com.hotel.service.AuthService;
import com.hotel.util.InputUtils;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        UserRepository userRepository = new InMemoryUserRepository();
        AuthService authService = new AuthService(userRepository);

        // Seeding des utilisateurs initiaux de test
        authService.register("Alice Dupont", "alice@example.com", "0611223344", "alice123");
        authService.register("Bob Martin", "bob@example.com", "0655667788", "bob123");

        Scanner scanner = new Scanner(System.in);

        while (true) {
            try {
                if (!authService.isAuthenticated()) {
                    showPublicMenu(scanner, authService);
                } else {
                    showAuthenticatedMenu(scanner, authService);
                }
            } catch (BusinessException | IllegalArgumentException e) {
                System.out.println("\n  [ERROR] " + e.getMessage() + "\n");
            } catch (Exception e) {
                System.out.println("\n [SYSTEM ERROR] " + e.getMessage() + "\n");
            }
        }
    }

    private static void showPublicMenu(Scanner scanner, AuthService authService) {
        System.out.println("========================");
        System.out.println("     HOTEL BOOKING      ");
        System.out.println("========================");
        System.out.println("1. Register");
        System.out.println("2. Login");
        System.out.println("0. Exit");

        int choice = InputUtils.readInt(scanner, "Choice: ");

        switch (choice) {
            case 1 -> handleRegister(scanner, authService);
            case 2 -> handleLogin(scanner, authService);
            case 0 -> {
                System.out.println("Thank you for using Hotel Booking. Goodbye!");
                System.exit(0);
            }
            default -> System.out.println("Invalid option. Please try again.\n");
        }
    }

    private static void handleRegister(Scanner scanner, AuthService authService) {
        System.out.println("\n--- Create an Account ---");
        String name = InputUtils.readString(scanner, "Full name: ");
        String email = InputUtils.readString(scanner, "Email: ");
        String phone = InputUtils.readString(scanner, "Phone number: ");
        String password = InputUtils.readString(scanner, "Password (min 6 chars): ");

        User user = authService.register(name, email, phone, password);
        System.out.println(" Account successfully created for " + user.getFullName() + "! You can now log in.\n");
    }

    private static void handleLogin(Scanner scanner, AuthService authService) {
        System.out.println("\n--- Login ---");
        String email = InputUtils.readString(scanner, "Email: ");
        String password = InputUtils.readString(scanner, "Password: ");

        User user = authService.login(email, password);
        System.out.println(" Welcome back, " + user.getFullName() + "!\n");
    }

    private static void showAuthenticatedMenu(Scanner scanner, AuthService authService) {
        User user = authService.getCurrentUser();
        System.out.println("================================");
        System.out.println("Logged in as: " + user.getFullName() + " (" + user.getEmail() + ")");
        System.out.println("================================");
        System.out.println("1. Search available rooms (Coming next)");
        System.out.println("2. View all rooms (Coming next)");
        System.out.println("3. Create reservation (Coming next)");
        System.out.println("4. My reservations (Coming next)");
        System.out.println("9. Logout");
        System.out.println("0. Exit");

        int choice = InputUtils.readInt(scanner, "Choice: ");

        switch (choice) {
            case 1, 2, 3, 4 -> System.out.println("\n  This feature is part of the next task!\n");
            case 9 -> {
                authService.logout();
                System.out.println(" You have been logged out.\n");
            }
            case 0 -> {
                System.out.println("Goodbye!");
                System.exit(0);
            }
            default -> System.out.println("Invalid option.\n");
        }
    }
}