package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private BookingController bookingController;

    private final Long userId = 1L;
    private final Long bookingId = 1L;
    private final BookingDto bookingDto = new BookingDto();
    private final BookingDtoResponse bookingDtoResponse = new BookingDtoResponse();

    @Test
    void createBooking_ShouldReturnCreatedBooking() {
        when(bookingService.createBooking(anyLong(), any(BookingDto.class)))
                .thenReturn(bookingDtoResponse);

        BookingDtoResponse response = bookingController.createBooking(userId, bookingDto);

        assertNotNull(response);
        assertEquals(bookingDtoResponse, response);
        verify(bookingService).createBooking(userId, bookingDto);
    }

    @Test
    void updateBookingStatus_WhenApproved_ShouldReturnUpdatedBooking() {
        boolean approved = true;
        when(bookingService.updateBookingStatus(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(bookingDtoResponse);

        BookingDtoResponse response = bookingController.updateBookingStatus(userId, bookingId, approved);

        assertNotNull(response);
        assertEquals(bookingDtoResponse, response);
        verify(bookingService).updateBookingStatus(userId, bookingId, approved);
    }

    @Test
    void updateBookingStatus_WhenRejected_ShouldReturnUpdatedBooking() {
        boolean approved = false;
        when(bookingService.updateBookingStatus(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(bookingDtoResponse);

        BookingDtoResponse response = bookingController.updateBookingStatus(userId, bookingId, approved);

        assertNotNull(response);
        assertEquals(bookingDtoResponse, response);
        verify(bookingService).updateBookingStatus(userId, bookingId, approved);
    }

    @Test
    void getBooking_ShouldReturnBooking() {
        when(bookingService.getBooking(anyLong(), anyLong()))
                .thenReturn(bookingDtoResponse);

        BookingDtoResponse response = bookingController.getBooking(userId, bookingId);

        assertNotNull(response);
        assertEquals(bookingDtoResponse, response);
        verify(bookingService).getBooking(userId, bookingId);
    }

    @Test
    void getUserBookings_ShouldReturnListOfBookings() {
        List<BookingDtoResponse> expectedBookings = Collections.singletonList(bookingDtoResponse);
        when(bookingService.getUserBookings(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(expectedBookings);

        Collection<BookingDtoResponse> response = bookingController.getUserBookings(userId, "ALL", 0, 10);

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(expectedBookings, response);
        verify(bookingService).getUserBookings(userId, "ALL", 0, 10);
    }

    @Test
    void getOwnerBookings_ShouldReturnListOfBookings() {
        List<BookingDtoResponse> expectedBookings = Collections.singletonList(bookingDtoResponse);
        when(bookingService.getOwnerBookings(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(expectedBookings);

        Collection<BookingDtoResponse> response = bookingController.getOwnerBookings(userId, "ALL", 0, 10);

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(expectedBookings, response);
        verify(bookingService).getOwnerBookings(userId, "ALL", 0, 10);
    }

}
