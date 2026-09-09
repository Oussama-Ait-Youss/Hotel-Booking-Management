package com.hotel;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

import com.hotel.exception.BusinessException;
import com.hotel.model.Reservation;
import com.hotel.model.Room;
import com.hotel.model.RoomStatus;
import com.hotel.model.RoomType;
import com.hotel.model.User;
import com.hotel.repository.ReservationRepository;
import com.hotel.repository.RoomRepository;
import com.hotel.repository.UserRepository;
import com.hotel.repository.impl.InMemoryReservationRepository;
import com.hotel.repository.impl.InMemoryRoomRepository;
import com.hotel.repository.impl.InMemoryUserRepository;
import com.hotel.service.AuthService;
import com.hotel.service.ReservationService;
import com.hotel.service.RoomService;
import com.hotel.service.impl.ReservationServiceImpl;
import com.hotel.service.impl.RoomServiceImpl;
import com.hotel.util.InputUtils;

public class Main {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static void main(String[] args) {
        // 1. Repositories
        UserRepository userRepository = new InMemoryUserRepository();
        RoomRepository roomRepository = new InMemoryRoomRepository();
        ReservationRepository reservationRepository = new InMemoryReservationRepository();

        // 2. Services
        AuthService authService = new AuthService(userRepository);
        RoomService roomService = new RoomServiceImpl(roomRepository, reservationRepository);
        ReservationService reservationService = new ReservationServiceImpl(reservationRepository, roomService);

        // 3. Seeding
        seedUsers(authService);
        seedRooms(roomRepository);

        Scanner scanner = new Scanner(System.in);

        while (true) {
            try {
                if (!authService.isAuthenticated()) {
                    showPublicMenu(scanner, authService);
                } else {
                    showAuthenticatedMenu(scanner, authService, roomService, reservationService);
                }
            } catch (BusinessException | IllegalArgumentException | IllegalStateException e) {
                System.out.println("\n  [ERROR] " + e.getMessage() + "\n");
            } catch (Exception e) {
                System.out.println("\n [SYSTEM ERROR] " + e.getMessage() + "\n");
            }
        }
    }

    private static void seedUsers(AuthService authService) {
        authService.register("Alice Dupont", "alice@example.com", "0611223344", "alice123");
        authService.register("Bob Martin", "bob@example.com", "0655667788", "bob123");
    }

    private static void seedRooms(RoomRepository roomRepository) {
        roomRepository.save(new Room("101", RoomType.SINGLE, 1, BigDecimal.valueOf(50.0), RoomStatus.AVAILABLE));
        roomRepository.save(new Room("102", RoomType.DOUBLE, 2, BigDecimal.valueOf(85.0), RoomStatus.AVAILABLE));
        roomRepository.save(new Room("201", RoomType.DOUBLE, 2, BigDecimal.valueOf(90.0), RoomStatus.AVAILABLE));
        roomRepository.save(new Room("301", RoomType.SUITE, 4, BigDecimal.valueOf(180.0), RoomStatus.AVAILABLE));
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

    private static void showAuthenticatedMenu(Scanner scanner, AuthService authService,
                                              RoomService roomService, ReservationService reservationService) {
        User user = authService.getCurrentUser();
        System.out.println("================================");
        System.out.println("Logged in as: " + user.getFullName() + " (" + user.getEmail() + ")");
        System.out.println("================================");
        System.out.println("1. View all rooms");
        System.out.println("2. Search available rooms");
        System.out.println("3. Book a room");
        System.out.println("4. My reservations");
        System.out.println("5. Cancel a reservation");
        System.out.println("9. Logout");
        System.out.println("0. Exit");

        int choice = InputUtils.readInt(scanner, "Choice: ");

        switch (choice) {
            case 1 -> handleViewAllRooms(roomService);
            case 2 -> handleSearchRooms(scanner, roomService);
            case 3 -> handleCreateReservation(scanner, user, reservationService);
            case 4 -> handleViewMyReservations(user, reservationService);
            case 5 -> handleCancelReservation(scanner, user, reservationService);
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

    private static void handleViewAllRooms(RoomService roomService) {
        System.out.println("\n--- All Hotel Rooms ---");
        List<Room> rooms = roomService.getAllRooms();
        if (rooms.isEmpty()) {
            System.out.println("No rooms configured in the hotel.\n");
            return;
        }
        rooms.forEach(r -> System.out.printf("Room %-4s | %-8s | Cap: %d pers | %.2f $/night%n",
                r.getRoomNumber(), r.getType(), r.getCapacity(), r.getPricePerNight()));
        System.out.println();
    }

    private static void handleSearchRooms(Scanner scanner, RoomService roomService) {
        System.out.println("\n--- Search Available Rooms ---");
        LocalDate checkIn = readValidDate(scanner, "Check-in date (yyyy-MM-dd): ");
        LocalDate checkOut = readValidDate(scanner, "Check-out date (yyyy-MM-dd): ");
        int guests = InputUtils.readInt(scanner, "Number of guests (0 for any): ");

        List<Room> available = guests > 0
                ? roomService.getAvailableRooms(checkIn, checkOut, guests)
                : roomService.getAvailableRooms(checkIn, checkOut);

        if (available.isEmpty()) {
            System.out.println("No rooms available for these dates.\n");
            return;
        }

        System.out.println("\nAvailable rooms:");
        available.forEach(r -> System.out.printf("Room %-4s | %-8s | Cap: %d pers | %.2f $/night%n",
                r.getRoomNumber(), r.getType(), r.getCapacity(), r.getPricePerNight()));
        System.out.println();
    }

    private static void handleCreateReservation(Scanner scanner, User user, ReservationService reservationService) {
        System.out.println("\n--- Book a Room ---");
        String roomNumber = InputUtils.readString(scanner, "Room number: ");
        LocalDate checkIn = readValidDate(scanner, "Check-in date (yyyy-MM-dd): ");
        LocalDate checkOut = readValidDate(scanner, "Check-out date (yyyy-MM-dd): ");

        Reservation res = reservationService.createReservation(user.getUUID(), roomNumber, checkIn, checkOut);
        System.out.println(" Reservation confirmed!");
        System.out.printf("Code: %s | Room: %s | Total: %.2f $%n%n",
                res.getReservationCode(), res.getRoomNumber(), res.getTotalPrice());
    }

    private static void handleViewMyReservations(User user, ReservationService reservationService) {
        System.out.println("\n--- My Reservations ---");
        List<Reservation> reservations = reservationService.getReservationsByUserId(user.getUUID());
        if (reservations.isEmpty()) {
            System.out.println("You have no reservations.\n");
            return;
        }

        reservations.forEach(res -> System.out.printf("Code: %-12s | Room: %-4s | %s -> %s | Status: %-9s | Total: %.2f $%n",
                res.getReservationCode(), res.getRoomNumber(),
                res.getCheckIn(), res.getCheckOut(),
                res.getStatus(), res.getTotalPrice()));
        System.out.println();
    }

    private static void handleCancelReservation(Scanner scanner, User user, ReservationService reservationService) {
        System.out.println("\n--- Cancel Reservation ---");
        String code = InputUtils.readString(scanner, "Enter reservation code: ");

        Reservation reservation = reservationService.getReservationByCode(code)
                .orElseThrow(() -> new BusinessException("Reservation not found: " + code));

        if (!reservation.getUserId().equals(user.getUUID())) {
            throw new BusinessException("You are not authorized to cancel this reservation.");
        }

        boolean cancelled = reservationService.cancelReservation(reservation.getId());
        if (cancelled) {
            System.out.println(" Reservation " + code + " successfully cancelled.\n");
        } else {
            System.out.println(" Could not cancel this reservation.\n");
        }
    }

    private static LocalDate readValidDate(Scanner scanner, String prompt) {
        while (true) {
            String input = InputUtils.readString(scanner, prompt);
            try {
                return LocalDate.parse(input, DATE_FORMATTER);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid format. Please use yyyy-MM-dd (ex: 2026-10-15).");
            }
        }
    }
}