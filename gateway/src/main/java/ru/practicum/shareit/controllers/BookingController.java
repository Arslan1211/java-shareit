package ru.practicum.shareit.controllers;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.client.BookingClient;
import ru.practicum.shareit.dto.BookingDto;

import static ru.practicum.shareit.constant.Constants.X_SHARER_USER_ID;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@Validated
@RequiredArgsConstructor
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> createBooking(
            @RequestHeader(X_SHARER_USER_ID) @Positive Long userId,
            @Valid @RequestBody BookingDto bookingDto
    ) {
        log.info("POST /bookings - создание бронирования от пользователя с ID={}, данные: {}", userId, bookingDto);
        return bookingClient.createBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateBookingStatus(
            @RequestHeader(X_SHARER_USER_ID) @Positive Long userId,
            @PathVariable @Positive Long bookingId,
            @RequestParam boolean approved
    ) {
        log.info("PATCH /bookings/{} - обновление статуса бронирования от пользователя с ID={}, новый статус: {}",
                bookingId, userId, approved ? "APPROVED" : "REJECTED");
        return bookingClient.updateBookingStatus(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(
            @RequestHeader(X_SHARER_USER_ID) @Positive Long userId,
            @PathVariable @Positive Long bookingId
    ) {
        log.info("GET /bookings/{} - получение бронирования по ID от пользователя с ID={}", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getUserBookings(
            @RequestHeader(X_SHARER_USER_ID) @Positive Long userId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size
    ) {
        log.info("GET /bookings?state={}&from={}&size={} - получение списка бронирований пользователя с ID={}",
                state, from, size, userId);
        return bookingClient.getUserBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getOwnerBookings(
            @RequestHeader(X_SHARER_USER_ID) @Positive Long userId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size
    ) {
        log.info("GET /bookings/owner?state={}&from={}&size={} - получение списка бронирований владельца с ID={}",
                state, from, size, userId);
        return bookingClient.getOwnerBookings(userId, state, from, size);
    }
}
