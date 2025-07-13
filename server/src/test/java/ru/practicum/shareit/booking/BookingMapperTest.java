package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BookingMapperTest {

    @Test
    void toBooking() {
        User user = new User();
        user.setId(1L);

        Item item = new Item();
        item.setId(1L);

        BookingDto dto = BookingDto.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        Booking booking = BookingMapper.toBooking(user, item, dto);

        assertEquals(dto.getStart(), booking.getStartDate());
        assertEquals(dto.getEnd(), booking.getEndDate());
        assertEquals(item, booking.getItem());
        assertEquals(user, booking.getBooker());
        assertEquals(BookingStatus.WAITING, booking.getStatus());
    }

    @Test
    void toBookingDto() {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStartDate(LocalDateTime.now());
        booking.setEndDate(LocalDateTime.now().plusDays(1));

        Item item = new Item();
        item.setId(1L);
        booking.setItem(item);

        BookingDto dto = BookingMapper.toBookingDto(booking);

        assertEquals(booking.getId(), dto.getId());
        assertEquals(booking.getItem().getId(), dto.getItemId());
        assertEquals(booking.getStartDate(), dto.getStart());
        assertEquals(booking.getEndDate(), dto.getEnd());
    }

    @Test
    void toBookingDtoResponse() {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStartDate(LocalDateTime.now());
        booking.setEndDate(LocalDateTime.now().plusDays(1));
        booking.setStatus(BookingStatus.APPROVED);

        User user = new User();
        user.setId(1L);
        booking.setBooker(user);

        Item item = new Item();
        item.setId(1L);
        booking.setItem(item);

        BookingDtoResponse response = BookingMapper.toBookingDtoResponse(booking);

        assertEquals(booking.getId(), response.getId());
        assertEquals(booking.getStartDate(), response.getStart());
        assertEquals(booking.getEndDate(), response.getEnd());
        assertEquals(booking.getStatus(), response.getStatus());
    }
}