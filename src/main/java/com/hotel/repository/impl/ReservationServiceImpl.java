package com.hotel.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.hotel.model.Reservation;
import com.hotel.model.ReservationStatus;
import com.hotel.model.Room;
import com.hotel.repository.ReservationRepository;
import com.hotel.service.ReservationService;
import com.hotel.service.RoomService;

public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final RoomService roomService;

    public ReservationServiceImpl(ReservationRepository reservationRepository, RoomService roomService) {
        this.reservationRepository = reservationRepository;
        this.roomService = roomService;
    }

    @Override
    public Reservation createReservation(UUID userId, String roomNumber, LocalDate checkIn, LocalDate checkOut) {
        // 1. Validation de sécurité sur les données d'entrée
        if (userId == null || roomNumber == null || checkIn == null || checkOut == null) {
            throw new IllegalArgumentException("Tous les paramètres de réservation sont obligatoires.");
        }

        if (checkIn.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La date de check-in ne peut pas être dans le passé.");
        }

        if (!checkOut.isAfter(checkIn)) {
            throw new IllegalArgumentException("La date de check-out doit être strictement postérieure au check-in.");
        }

        // 2. Vérification de l'existence de la chambre
        Room room = roomService.getRoomByNumber(roomNumber)
                .orElseThrow(() -> new IllegalArgumentException("Chambre introuvable : " + roomNumber));

        // 3. Vérification de la disponibilité temporelle anti-surbooking
        if (!roomService.isRoomAvailable(roomNumber, checkIn, checkOut)) {
            throw new IllegalStateException("La chambre " + roomNumber + " n'est pas disponible pour ces dates.");
        }

        // 4. Calcul du nombre de nuits et du montant total
        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
        BigDecimal totalPrice = room.getPricePerNight().multiply(BigDecimal.valueOf(nights));

        // 5. Génération des identifiants (UUID technique et code lisible)
        UUID reservationId = UUID.randomUUID();
        String reservationCode = "RES-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        // 6. Instanciation et persistance
        Reservation reservation = new Reservation(
                reservationId,
                reservationCode,
                userId,
                roomNumber,
                checkIn,
                checkOut,
                1,
                Math.toIntExact(nights),
                totalPrice,
                ReservationStatus.CONFIRMED,
                LocalDate.now()
        );

        reservationRepository.save(reservation);
        return reservation;
    }

    @Override
    public boolean cancelReservation(UUID reservationId) {
        if (reservationId == null) {
            return false;
        }

        Optional<Reservation> optionalReservation = reservationRepository.findById(reservationId);
        if (optionalReservation.isEmpty()) {
            return false;
        }

        Reservation reservation = optionalReservation.get();

        // Règle métier : interdire l'annulation si le séjour a déjà commencé
        if (LocalDate.now().isAfter(reservation.getCheckIn())) {
            throw new IllegalStateException("Impossible d'annuler une réservation dont le séjour a déjà débuté.");
        }

        reservation.setStatus(ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);
        return true;
    }

    @Override
    public Optional<Reservation> getReservationById(UUID id) {
        if (id == null) return Optional.empty();
        return reservationRepository.findById(id);
    }

    @Override
    public Optional<Reservation> getReservationByCode(String code) {
        if (code == null) return Optional.empty();
        return reservationRepository.findByCode(code);
    }

    @Override
    public List<Reservation> getReservationsByUserId(UUID userId) {
        if (userId == null) return List.of();
        return reservationRepository.findByUserId(userId);
    }

    @Override
    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }
    @Override
    public Reservation modifyReservation(UUID reservationId, UUID userId, String newRoomNumber,
                                         LocalDate newCheckIn, LocalDate newCheckOut, int guests) {
        if (reservationId == null || userId == null || newCheckIn == null || newCheckOut == null) {
            throw new IllegalArgumentException("Paramètres de modification invalides.");
        }

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Réservation introuvable."));

        // 1. Contrôle d'autorisation
        if (!reservation.getUserId().equals(userId)) {
            throw new IllegalStateException("Vous n'êtes pas autorisé à modifier cette réservation.");
        }

        // 2. Contrôle du statut
        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new IllegalStateException("Impossible de modifier une réservation déjà annulée.");
        }

        // 3. Validation des dates
        if (newCheckIn.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La date de check-in ne peut pas être dans le passé.");
        }
        if (!newCheckOut.isAfter(newCheckIn)) {
            throw new IllegalArgumentException("La date de check-out doit être strictement postérieure au check-in.");
        }

        // 4. Chambre ciblée (conserve l'actuelle si non spécifiée)
        String targetRoom = (newRoomNumber != null && !newRoomNumber.isBlank())
                ? newRoomNumber.trim()
                : reservation.getRoomNumber();

        Room room = roomService.getRoomByNumber(targetRoom)
                .orElseThrow(() -> new IllegalArgumentException("Chambre introuvable : " + targetRoom));

        // 5. Capacité requise
        if (guests > 0 && room.getCapacity() < guests) {
            throw new IllegalStateException("La chambre " + targetRoom + " ne peut pas accueillir " + guests + " personnes.");
        }

        // 6. Vérification de disponibilité anti-surbooking (sans auto-collision)
        boolean hasConflict = reservationRepository.findByRoomNumber(targetRoom).stream()
                .filter(r -> !r.getId().equals(reservationId)) // Exclure la réservation elle-même
                .filter(r -> r.getStatus() != ReservationStatus.CANCELLED) // Ignorer les annulées
                .anyMatch(r -> r.overlapsWith(newCheckIn, newCheckOut));

        if (hasConflict) {
            throw new IllegalStateException("La chambre " + targetRoom + " n'est pas disponible pour ces dates.");
        }

        // 7. Recalcul des nuits et du montant total
        long nights = ChronoUnit.DAYS.between(newCheckIn, newCheckOut);
        BigDecimal newTotalPrice = room.getPricePerNight().multiply(BigDecimal.valueOf(nights));

        // 8. Application des modifications
        reservation.setRoomNumber(targetRoom);
        reservation.setCheckIn(newCheckIn);
        reservation.setCheckOut(newCheckOut);
        reservation.setNumberOfNights(Math.toIntExact(nights)); // ou le setter correspondant
        reservation.setTotalPrice(newTotalPrice);

        reservationRepository.save(reservation);
        return reservation;
    }
}