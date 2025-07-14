package ru.practicum.shareit.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.client.BookingClient;
import ru.practicum.shareit.dto.BookingDto;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {

    @Mock
    private BookingClient bookingClient;

    @InjectMocks
    private BookingController bookingController;

    private BookingDto bookingDto;
    private ResponseEntity<Object> responseEntity;

    @BeforeEach
    void setUp() {
        bookingDto = new BookingDto();
        bookingDto.setItemId(1L);
        responseEntity = new ResponseEntity<>(HttpStatus.OK);
    }

    @Test
    void createBooking_ValidData_ReturnsOk() {
        when(bookingClient.createBooking(anyLong(), any(BookingDto.class)))
                .thenReturn(responseEntity);

        ResponseEntity<Object> response = bookingController.createBooking(1L, bookingDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(bookingClient).createBooking(1L, bookingDto);
    }

    @Test
    void updateBookingStatus_Approved_ReturnsOk() {
        when(bookingClient.updateBookingStatus(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(responseEntity);

        ResponseEntity<Object> response = bookingController.updateBookingStatus(1L, 1L, true);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(bookingClient).updateBookingStatus(1L, 1L, true);
    }

    @Test
    void getBooking_ExistingBooking_ReturnsOk() {
        when(bookingClient.getBooking(anyLong(), anyLong()))
                .thenReturn(responseEntity);

        ResponseEntity<Object> response = bookingController.getBooking(1L, 1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(bookingClient).getBooking(1L, 1L);
    }

    @Test
    void getUserBookings_ValidState_ReturnsOk() {
        when(bookingClient.getUserBookings(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(responseEntity);

        ResponseEntity<Object> response = bookingController.getUserBookings(1L, "ALL", 0, 10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(bookingClient).getUserBookings(1L, "ALL", 0, 10);
    }

    @Test
    void getOwnerBookings_ValidState_ReturnsOk() {
        when(bookingClient.getOwnerBookings(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(responseEntity);

        ResponseEntity<Object> response = bookingController.getOwnerBookings(1L, "ALL", 0, 10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(bookingClient).getOwnerBookings(1L, "ALL", 0, 10);
    }
}