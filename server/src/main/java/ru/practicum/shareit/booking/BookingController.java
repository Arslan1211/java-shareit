package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.Collection;

import static ru.practicum.shareit.constant.Constants.X_SHARER_USER_ID;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDtoResponse createBooking(
            @RequestHeader(X_SHARER_USER_ID) Long userId,
            @RequestBody BookingDto bookingDto
    ) {
        log.info("POST /bookings - создание бронирования от пользователя с ID={}, данные: {}", userId, bookingDto);
        return bookingService.createBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoResponse updateBookingStatus(
            @RequestHeader(X_SHARER_USER_ID) Long userId,
            @PathVariable Long bookingId,
            @RequestParam boolean approved
    ) {
        log.info("PATCH /bookings/{} - обновление статуса бронирования от пользователя с ID={}, новый статус: {}",
                bookingId, userId, approved ? "APPROVED" : "REJECTED");
        return bookingService.updateBookingStatus(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDtoResponse getBooking(
            @RequestHeader(X_SHARER_USER_ID) Long userId,
            @PathVariable Long bookingId
    ) {
        log.info("GET /bookings/{} - получение бронирования по ID от пользователя с ID={}", bookingId, userId);
        return bookingService.getBooking(userId, bookingId);
    }

    @GetMapping
    public Collection<BookingDtoResponse> getUserBookings(
            @RequestHeader(X_SHARER_USER_ID) Long userId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size
    ) {
        log.info("GET /bookings?state={}&from={}&size={} - получение списка бронирований пользователя с ID={}",
                state, from, size, userId);
        return bookingService.getUserBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    public Collection<BookingDtoResponse> getOwnerBookings(
            @RequestHeader(X_SHARER_USER_ID) Long userId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size
    ) {
        log.info("GET /bookings/owner?state={}&from={}&size={} - получение списка бронирований владельца с ID={}",
                state, from, size, userId);
        return bookingService.getOwnerBookings(userId, state, from, size);
    }
}
