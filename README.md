# 🏨 Hotel Booking Management

A console-based Java application for managing hotel rooms, customer accounts, and hotel reservations.

The project simulates the core functionalities of a hotel booking system while applying clean object-oriented programming principles, layered architecture, in-memory persistence, business validation, and custom exceptions.

---

## 📌 Table of Contents

* [Overview](#-overview)
* [Project Vision](#-project-vision)
* [Learning Objectives](#-learning-objectives)
* [Features](#-features)
* [Business Rules](#-business-rules)
* [Domain Model](#-domain-model)
* [Architecture](#-architecture)
* [Project Structure](#-project-structure)
* [Technologies](#-technologies)
* [Data Storage](#-data-storage)
* [Application Flow](#-application-flow)
* [Console Interface](#-console-interface)
* [Reservation Logic](#-reservation-logic)
* [Validation & Exceptions](#-validation--exceptions)
* [Test Scenarios](#-test-scenarios)
* [Sample Data](#-sample-data)
* [Bonus Features](#-bonus-features)
* [Getting Started](#-getting-started)
* [Java Concepts Evaluated](#-java-concepts-evaluated)
* [Future Improvements](#-future-improvements)

---

## 🎯 Overview

**Hotel Booking Management** is a Java console application designed to simulate the main operations of a hotel reservation system.

Customers can:

* Create an account
* Log in and log out
* Update their profile
* Change their password
* Browse hotel rooms
* Search for available rooms
* Create reservations
* View their reservations
* View reservation details
* Modify reservations
* Cancel reservations
* Consult their reservation history

The application is intentionally implemented as a **console application with in-memory data storage**, allowing the focus to remain on Java, object-oriented programming, architecture, validation, and business logic.

---

## 💡 Project Vision

The goal is to build a clean, structured, testable, and extensible console application that reproduces the main features of a hotel reservation system.

The application follows a layered architecture:

```text
USER
  ↓
MAIN / CONSOLE
  ↓
SERVICE
  ↓
REPOSITORY
  ↓
DATA IN MEMORY
```

Each layer has a clearly defined responsibility.

---

## 🎓 Learning Objectives

This project is designed to practice and demonstrate:

### Object-Oriented Programming

* Classes and objects
* Encapsulation
* Constructors
* Getters and setters
* Enums
* Relationships between objects
* Interfaces
* Interface implementations

### Java APIs

* `List`
* `Map`
* `HashMap`
* `ArrayList`
* `Optional`
* `LocalDate`
* `LocalDateTime`
* `ChronoUnit`
* `BigDecimal`
* `Comparator`
* Streams

### Software Architecture

* Layered architecture
* Repository Pattern
* Service Layer
* Separation of responsibilities
* In-memory persistence
* Custom business exceptions

---

# ✨ Features

## 🔐 Authentication & Profile

### Registration

A customer can create an account using:

* Full name
* Email
* Phone number
* Password

Validation rules:

* Email is required
* Email must be unique
* Full name is required
* Phone number is required
* Password must contain at least 6 characters

### Login

Customers authenticate using:

```text
Email
Password
```

The application displays an error when:

* The user does not exist
* The password is incorrect

Only one user can be considered logged in at a time.

### Profile Management

Authenticated customers can update:

* Full name
* Email
* Phone number

The email must remain unique.

### Password Change

Customers can change their password by providing:

* Current password
* New password

The new password must contain at least 6 characters.

---

# 🛏️ Room Management

Each room contains:

| Property        | Type         |
| --------------- | ------------ |
| Room number     | `String`     |
| Room type       | `RoomType`   |
| Capacity        | `int`        |
| Price per night | `BigDecimal` |
| Status          | `RoomStatus` |

### Room Types

```java
SINGLE
DOUBLE
SUITE
```

### Room Statuses

```java
AVAILABLE
MAINTENANCE
```

A room with `AVAILABLE` status can be offered to customers.

A room with `MAINTENANCE` status cannot be reserved.

---

# 🔎 Availability Search

Customers can search for available rooms by entering:

* Check-in date
* Check-out date
* Number of guests

The system returns only rooms that are available for the **entire requested period**.

### Example

Existing reservation:

```text
10/09/2026 → 15/09/2026
```

New request:

```text
12/09/2026 → 17/09/2026
```

❌ The room is unavailable because the periods overlap.

However:

```text
15/09/2026 → 20/09/2026
```

✅ The reservation can be accepted because the previous guest checks out on `15/09/2026`.

---

# 📅 Reservations

## Creating a Reservation

A customer selects:

* A room
* Check-in date
* Check-out date
* Number of guests

The system validates:

1. The room exists
2. The room is available
3. The room is not under maintenance
4. The dates are valid
5. The room has sufficient capacity
6. The requested period does not overlap another active reservation

---

## 🆔 Reservation Identifier

Every reservation has:

* A UUID internally
* A human-readable reservation code

Example:

```text
RES-2026-0001
```

---

## 🌙 Number of Nights

The number of nights is calculated using:

```java
ChronoUnit.DAYS.between(checkIn, checkOut);
```

Example:

```text
Check-in  : 10/09/2026
Check-out : 13/09/2026

Number of nights = 3
```

---

## 💰 Total Price

The total price is calculated using:

```text
totalPrice = numberOfNights × pricePerNight
```

Example:

```text
Price per night : 500.00 MAD
Number of nights: 3

Total           : 1500.00 MAD
```

Financial amounts are represented using:

```java
BigDecimal
```

with two decimal places.

---

# 📋 Reservation Status

A reservation can have one of three statuses:

```java
CONFIRMED
CANCELLED
COMPLETED
```

### `CONFIRMED`

The reservation is active.

### `CANCELLED`

The customer cancelled the reservation.

The reservation is **not deleted** from the system.

### `COMPLETED`

The customer's stay has finished.

---

# ❌ Cancellation

A customer can cancel only:

* Their own reservation
* A reservation with `CONFIRMED` status

When cancelled:

```text
CONFIRMED
     ↓
CANCELLED
```

The reservation remains stored so that it can appear in the customer's history.

The associated room becomes available for that period again.

---

# ✏️ Reservation Modification

Customers can modify:

* Check-in date
* Check-out date
* Optionally, the room

Before applying the modification, the system checks:

* Availability
* Date validity
* Room capacity
* Reservation status
* Ownership of the reservation

The total price is recalculated after modification.

---

# 📜 Reservation History

Customers can view all of their reservations:

* Confirmed
* Cancelled
* Completed

Possible sorting strategies include:

### Creation date

```text
Newest → Oldest
```

### Check-in date

```text
Earliest → Latest
```

---

# 📐 Business Rules

The application enforces the following rules.

### Rule 1 — Unique Email

Two users cannot have the same email address.

### Rule 2 — Valid Dates

The check-in date must be:

```text
>= current date
```

The check-out date must be:

```text
> check-in date
```

Therefore:

```text
checkIn == checkOut
```

is forbidden.

### Rule 3 — Room Availability

A room cannot have two active reservations whose periods overlap.

### Rule 4 — Room Capacity

The number of guests cannot exceed the room's capacity.

Example:

```text
Room capacity = 2
Guests         = 3
```

❌ Reservation rejected.

### Rule 5 — Maintenance

A room with:

```java
RoomStatus.MAINTENANCE
```

cannot be reserved.

### Rule 6 — Price

The room price must satisfy:

```text
pricePerNight > 0
```

Prices use:

```java
BigDecimal
```

with two decimal places.

### Rule 7 — Ownership

A customer can only:

* View their own reservations
* Modify their own reservations
* Cancel their own reservations

### Rule 8 — Traceability

Cancelled reservations are never physically deleted.

Their status becomes:

```java
CANCELLED
```

### Rule 9 — Cancelled Reservations

A cancelled reservation cannot:

* Be modified
* Be cancelled again

### Rule 10 — Total Price

```text
total = numberOfNights × pricePerNight
```

The calculated amount is stored in the reservation.

---

# 🧩 Domain Model

## User

```text
User
├── UUID id
├── String fullName
├── String email
├── String phone
└── String password
```

## Room

```text
Room
├── String roomNumber
├── RoomType type
├── int capacity
├── BigDecimal pricePerNight
└── RoomStatus status
```

## Reservation

```text
Reservation
├── UUID id
├── String reservationCode
├── UUID userId
├── String roomNumber
├── LocalDate checkIn
├── LocalDate checkOut
├── int numberOfGuests
├── long numberOfNights
├── BigDecimal totalPrice
├── ReservationStatus status
└── LocalDateTime createdAt
```

---

# 🔢 Enums

## RoomType

```java
public enum RoomType {
    SINGLE,
    DOUBLE,
    SUITE
}
```

## RoomStatus

```java
public enum RoomStatus {
    AVAILABLE,
    MAINTENANCE
}
```

## ReservationStatus

```java
public enum ReservationStatus {
    CONFIRMED,
    CANCELLED,
    COMPLETED
}
```

---

# 🏗️ Architecture

The application follows a layered architecture.

```text
┌─────────────────────────┐
│      Main / Console     │
│                         │
│ UI / Input / Navigation │
└────────────┬────────────┘
             │
             ▼
┌─────────────────────────┐
│        Services         │
│                         │
│ Business Rules / Logic  │
└────────────┬────────────┘
             │
             ▼
┌─────────────────────────┐
│       Repositories      │
│                         │
│ Data Access / Storage   │
└────────────┬────────────┘
             │
             ▼
┌─────────────────────────┐
│     In-Memory Data      │
│                         │
│ HashMap / ArrayList     │
└─────────────────────────┘
```

---

## 🖥️ Main / Console

The `Main` layer is responsible for:

* Displaying menus
* Reading user input
* Navigation
* Displaying results
* Handling user input errors

The `Main` class should **not contain business logic**.

---

## ⚙️ Services

### `AuthService`

Responsible for:

* Registration
* Login
* Logout
* Session management
* Profile updates
* Password changes

### `RoomService`

Responsible for:

* Displaying rooms
* Searching available rooms
* Checking room status
* Finding rooms by number

### `ReservationService`

Responsible for:

* Creating reservations
* Checking availability
* Calculating nights
* Calculating total price
* Updating reservations
* Cancelling reservations
* Retrieving customer reservations

---

# 🗄️ Repositories

The project uses the Repository Pattern to separate data access from business logic.

## UserRepository

```java
void save(User user);

Optional<User> findById(UUID id);

Optional<User> findByEmail(String email);

boolean existsByEmail(String email);

List<User> findAll();
```

## RoomRepository

```java
void save(Room room);

Optional<Room> findByRoomNumber(String roomNumber);

List<Room> findAll();
```

## ReservationRepository

```java
void save(Reservation reservation);

Optional<Reservation> findById(UUID id);

Optional<Reservation> findByCode(String code);

List<Reservation> findByUserId(UUID userId);

List<Reservation> findByRoomNumber(String roomNumber);

List<Reservation> findAll();
```

---

# 💾 In-Memory Persistence

The first version of the project does not require a database.

Data is stored in Java collections.

### Users

```java
HashMap<UUID, User>
```

### Rooms

```java
HashMap<String, Room>
```

### Reservations

Either:

```java
ArrayList<Reservation>
```

or:

```java
HashMap<UUID, Reservation>
```

This approach makes the project lightweight and allows the focus to remain on application architecture and Java fundamentals.

---

# 🛠️ Utilities

Utility classes prevent the `Main` class and services from becoming overloaded.

## `InputUtils`

Handles safe input for:

* Strings
* Integers
* Dates
* Monetary amounts

## `ValidationUtils`

Handles validation for:

* Email
* Password
* Empty strings
* Phone numbers

## `DateUtils`

Handles:

* `LocalDate` parsing
* Number-of-night calculation
* Period comparisons

## `MoneyUtils`

Handles:

* `BigDecimal`
* Monetary calculations
* Two-decimal rounding

---

# 📁 Project Structure

Suggested project structure:

```text
src/
│
├── Main.java
│
├── model/
│   ├── User.java
│   ├── Room.java
│   ├── Reservation.java
│   ├── RoomType.java
│   ├── RoomStatus.java
│   └── ReservationStatus.java
│
├── repository/
│   ├── UserRepository.java
│   ├── RoomRepository.java
│   ├── ReservationRepository.java
│   │
│   └── impl/
│       ├── InMemoryUserRepository.java
│       ├── InMemoryRoomRepository.java
│       └── InMemoryReservationRepository.java
│
├── service/
│   ├── AuthService.java
│   ├── RoomService.java
│   └── ReservationService.java
│
├── exception/
│   ├── UserNotFoundException.java
│   ├── EmailAlreadyExistsException.java
│   ├── InvalidCredentialsException.java
│   ├── RoomNotFoundException.java
│   ├── RoomUnavailableException.java
│   ├── RoomCapacityExceededException.java
│   ├── ReservationNotFoundException.java
│   ├── InvalidReservationDateException.java
│   ├── UnauthorizedReservationAccessException.java
│   └── ReservationAlreadyCancelledException.java
│
└── util/
    ├── InputUtils.java
    ├── ValidationUtils.java
    ├── DateUtils.java
    └── MoneyUtils.java
```

---

# 🖥️ Application Flow

## Guest Menu

```text
========================
     HOTEL BOOKING
========================

1. Register
2. Login
0. Exit

Choice:
```

## Authenticated User Menu

```text
================================
Logged in as: Alice Dupont
================================

1. Search available rooms
2. View all rooms
3. Create reservation
4. My reservations
5. Reservation details
6. Update reservation
7. Cancel reservation
8. Update profile
9. Change password
10. Logout
0. Exit
```

Invalid input must not terminate the application.

The user should receive a clear error message and be able to continue using the application.

---

# 🔍 Example: Room Search

Input:

```text
Check-in date : 10/09/2026
Check-out date: 13/09/2026
Guests        : 2
```

Output:

```text
Available rooms

Room 102
Type: DOUBLE
Capacity: 2
Price/night: 500.00 MAD
Total for 3 nights: 1500.00 MAD
----------------------------

Room 205
Type: SUITE
Capacity: 4
Price/night: 900.00 MAD
Total for 3 nights: 2700.00 MAD
```

---

# 🧮 Reservation Availability Logic

A reservation is considered conflicting when an active reservation overlaps the requested period.

Conceptually:

```text
Existing:
        |----------|
10/09               15/09

Requested:
             |----------|
12/09                    17/09

              ❌ OVERLAP
```

But:

```text
Existing:
        |----------|
10/09               15/09

Requested:
                   |----------|
                   15/09       20/09

                   ✅ ACCEPTED
```

A cancelled reservation does not block room availability.

---

# ⚠️ Validation & Exceptions

The project recommends using custom business exceptions.

Examples:

```text
UserNotFoundException
EmailAlreadyExistsException
InvalidCredentialsException
RoomNotFoundException
RoomUnavailableException
RoomCapacityExceededException
ReservationNotFoundException
InvalidReservationDateException
UnauthorizedReservationAccessException
ReservationAlreadyCancelledException
```

Example:

```java
if (!available) {
    throw new RoomUnavailableException(
        "Room is unavailable for the selected dates."
    );
}
```

---

# 🧪 Test Scenarios

## Scenario 1 — Valid Reservation

Alice logs in and searches:

```text
10/09/2026 → 13/09/2026
2 guests
```

She selects:

```text
Room 102
500 MAD / night
```

Expected result:

```text
3 nights
Total = 1500.00 MAD
Status = CONFIRMED
```

---

## Scenario 2 — Overlapping Reservation

Bob attempts:

```text
Room 102
11/09/2026 → 14/09/2026
```

Alice already has:

```text
10/09/2026 → 13/09/2026
```

Expected result:

```text
Reservation refused.

Room 102 is unavailable for the selected dates.
```

---

## Scenario 3 — Reservation After Previous Checkout

Bob requests:

```text
13/09/2026 → 15/09/2026
```

Alice checks out:

```text
13/09/2026
```

Expected result:

```text
Reservation accepted.
```

---

## Scenario 4 — Capacity Exceeded

Alice requests:

```text
Room 102
Guests = 3
```

Room capacity:

```text
2
```

Expected result:

```text
Reservation refused.

Room capacity exceeded.
```

---

## Scenario 5 — Cancellation

Alice cancels:

```text
RES-2026-0001
```

Expected state transition:

```text
CONFIRMED
     ↓
CANCELLED
```

The reservation remains visible in her history.

---

## Scenario 6 — Room Under Maintenance

Alice attempts to reserve:

```text
Room 301
```

Expected result:

```text
Reservation refused.

Room is currently under maintenance.
```

---

# 🧪 Sample Data

## Users

### Alice Dupont

```text
Email    : alice@example.com
Password : alice123
```

### Bob Martin

```text
Email    : bob@example.com
Password : bob123
```

## Rooms

| Room | Type   | Capacity |       Price | Status      |
| ---- | ------ | -------: | ----------: | ----------- |
| 101  | SINGLE |        1 |  300.00 MAD | AVAILABLE   |
| 102  | DOUBLE |        2 |  500.00 MAD | AVAILABLE   |
| 201  | DOUBLE |        2 |  550.00 MAD | AVAILABLE   |
| 205  | SUITE  |        4 |  900.00 MAD | AVAILABLE   |
| 301  | SUITE  |        4 | 1200.00 MAD | MAINTENANCE |

---

# 🚀 Bonus Features

After completing the mandatory version, the project can be extended.

## Bonus 1 — Advanced Search

Allow customers to filter rooms by:

* Room type
* Maximum price
* Capacity

Example:

```text
Type: SUITE
Maximum price: 1000 MAD
Minimum capacity: 4
```

---

## Bonus 2 — Sorting

Rooms can be sorted by:

* Price ascending
* Price descending
* Capacity

Using:

```java
Comparator
```

and:

```java
Stream
```

---

## Bonus 3 — Statistics

Display:

* Total number of reservations
* Number of cancelled reservations
* Most reserved room
* Total revenue

---

## Bonus 4 — Administrator

Introduce:

```java
Role.CLIENT
Role.ADMIN
```

The administrator can:

* Add rooms
* Modify rooms
* Put rooms under maintenance
* Display all reservations
* Display all customers

---

# 🛠️ Getting Started

## Prerequisites

The project requires:

* Java Development Kit (JDK)
* A Java IDE or code editor
* Terminal / Command Prompt

A modern Java version is recommended.

---

## Clone the Repository

```bash
git clone <repository-url>
```

Move into the project directory:

```bash
cd hotel-booking-management
```

---

## Compile

If the project is organized as a simple Java source project:

```bash
javac -d out src/Main.java
```

Depending on the project structure, compile all Java sources:

```bash
javac -d out $(find src -name "*.java")
```

On Windows PowerShell, an equivalent approach can be used according to the configured Java project structure.

---

## Run

```bash
java -cp out Main
```

---

# 🧠 Java Concepts Evaluated

This project evaluates practical knowledge of:

* Classes
* Objects
* Encapsulation
* Constructors
* Getters / Setters
* Enums
* Collections
* `List`
* `Map`
* `HashMap`
* Interfaces
* Interface implementation
* Repository Pattern
* Service Layer
* Exceptions
* Custom exceptions
* `Optional`
* `LocalDate`
* `LocalDateTime`
* `ChronoUnit`
* `BigDecimal`
* `Comparator`
* Streams
* Validation
* Layered architecture
* Separation of responsibilities

---

# 🔮 Future Improvements

The current project intentionally uses in-memory storage. Possible future versions could introduce:

* Persistent database storage
* JDBC
* MySQL
* Authentication improvements
* Password hashing
* REST API
* Web interface
* Admin dashboard
* Automated unit tests
* Integration tests
* Advanced room filtering
* Reservation statistics
* Payment management
* Email notifications
* Multi-user concurrent sessions

These features are outside the mandatory scope of the current console application.

---

# 📊 Project Scope

### Mandatory

* [x] User registration
* [x] Login / logout
* [x] Session management
* [x] Profile management
* [x] Password change
* [x] Room listing
* [x] Room availability search
* [x] Reservation creation
* [x] Reservation modification
* [x] Reservation cancellation
* [x] Reservation history
* [x] Date validation
* [x] Capacity validation
* [x] Room status validation
* [x] Overlap detection
* [x] Price calculation
* [x] `BigDecimal` for money
* [x] Custom business exceptions
* [x] Layered architecture
* [x] In-memory repositories

### Optional

* [ ] Advanced room search
* [ ] Room sorting
* [ ] Statistics
* [ ] Administrator role
* [ ] Room administration

---

# 📜 License

This project is developed for educational purposes as part of a Java learning project.

---

# 👨‍💻 Project

**Hotel Booking Management**

A Java console application focused on:
# 👨‍💻 Auteur
Ait Youss Oussama - UM6P - YOUCODE

```text
Object-Oriented Programming
        +
Clean Architecture
        +
Business Rules
        +
In-Memory Persistence
        +
Java Best Practices
```

> **Goal:** Build a clean, maintainable, testable, and extensible hotel reservation system while demonstrating core Java development skills.
