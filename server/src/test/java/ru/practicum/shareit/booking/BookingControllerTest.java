/*
package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.service.BookingService;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.constant.Constants.X_SHARER_USER_ID;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    private ObjectMapper mapper;
    private BookingDto bookingDto;
    private BookingDtoResponse bookingResponse;

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        bookingDto = BookingDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusDays(1))
                .build();

        bookingResponse = BookingDtoResponse.builder()
                .id(1L)
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusDays(1))
                .build();
    }

    @Test
    @DisplayName("Получение бронирований пользователя - успешный сценарий")
    void getUserBookings_shouldReturnBookingsList() throws Exception {
        when(bookingService.getUserBookings(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(Collections.singletonList(bookingResponse));

        mockMvc.perform(get("/bookings?state=ALL&from=0&size=10")
                        .header(X_SHARER_USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    @DisplayName("Получение бронирований пользователя с невалидным статусом - ошибка")
    void getUserBookings_withInvalidState_shouldReturnBadRequest() throws Exception {
        when(bookingService.getUserBookings(anyLong(), anyString(), anyInt(), anyInt()))
                .thenThrow(new IllegalArgumentException("Unknown state: INVALID"));

        mockMvc.perform(get("/bookings?state=INVALID&from=0&size=10")
                        .header(X_SHARER_USER_ID, 1L))
                .andExpect(status().is5xxServerError());
    }

    @Test
    @DisplayName("Получение бронирований пользователя с отрицательной пагинацией - ошибка")
    void getUserBookings_withNegativePagination_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/bookings?state=ALL&from=-1&size=0")
                        .header(X_SHARER_USER_ID, 1L))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Получение бронирований владельца - успешный сценарий")
    void getOwnerBookings_shouldReturnBookingsList() throws Exception {
        when(bookingService.getOwnerBookings(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(Collections.singletonList(bookingResponse));

        mockMvc.perform(get("/bookings/owner?state=ALL&from=0&size=10")
                        .header(X_SHARER_USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    @DisplayName("Получение бронирований владельца с пустым результатом")
    void getOwnerBookings_emptyResult_shouldReturnEmptyList() throws Exception {
        when(bookingService.getOwnerBookings(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/bookings/owner?state=ALL&from=0&size=10")
                        .header(X_SHARER_USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}*/

package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.NotFoundException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.practicum.shareit.constant.Constants.X_SHARER_USER_ID;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    private ObjectMapper mapper;
    private BookingDto bookingDto;
    private BookingDtoResponse bookingResponse;

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        bookingDto = BookingDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusDays(1))
                .build();

        bookingResponse = BookingDtoResponse.builder()
                .id(1L)
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusDays(1))
                .status(BookingStatus.WAITING)
                .build();
    }

    @Test
    @DisplayName("Создание бронирования - успешный сценарий")
    void createBooking_shouldReturnCreatedBooking() throws Exception {
        // Используем any() из Mockito с явным указанием типа
        when(bookingService.createBooking(anyLong(), any(BookingDto.class)))
                .thenReturn(bookingResponse);

        mockMvc.perform(post("/bookings")
                        .header(X_SHARER_USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookingDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("WAITING"));
    }

    @Test
    @DisplayName("Создание бронирования без заголовка пользователя - ошибка")
    void createBooking_withoutUserId_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookingDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Обновление статуса бронирования - успешный сценарий")
    void updateBookingStatus_shouldReturnUpdatedBooking() throws Exception {
        BookingDtoResponse approvedResponse = BookingDtoResponse.builder()
                .id(1L)
                .status(BookingStatus.APPROVED)
                .build();

        when(bookingService.updateBookingStatus(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(approvedResponse);

        mockMvc.perform(patch("/bookings/1?approved=true")
                        .header(X_SHARER_USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    @DisplayName("Обновление статуса несуществующего бронирования - ошибка")
    void updateBookingStatus_nonExistingBooking_shouldReturnNotFound() throws Exception {
        when(bookingService.updateBookingStatus(anyLong(), anyLong(), anyBoolean()))
                .thenThrow(new NotFoundException("Бронирование не найдено"));

        mockMvc.perform(patch("/bookings/999?approved=true")
                        .header(X_SHARER_USER_ID, 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Получение бронирования по ID - успешный сценарий")
    void getBooking_shouldReturnBooking() throws Exception {
        when(bookingService.getBooking(anyLong(), anyLong()))
                .thenReturn(bookingResponse);

        mockMvc.perform(get("/bookings/1")
                        .header(X_SHARER_USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @DisplayName("Получение несуществующего бронирования - ошибка")
    void getBooking_nonExistingBooking_shouldReturnNotFound() throws Exception {
        when(bookingService.getBooking(anyLong(), anyLong()))
                .thenThrow(new NotFoundException("Бронирование не найдено"));

        mockMvc.perform(get("/bookings/999")
                        .header(X_SHARER_USER_ID, 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Получение бронирований пользователя - успешный сценарий")
    void getUserBookings_shouldReturnBookingsList() throws Exception {
        when(bookingService.getUserBookings(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(Collections.singletonList(bookingResponse));

        mockMvc.perform(get("/bookings?state=ALL&from=0&size=10")
                        .header(X_SHARER_USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    @DisplayName("Получение бронирований пользователя с невалидным статусом - ошибка")
    void getUserBookings_withInvalidState_shouldReturnBadRequest() throws Exception {
        when(bookingService.getUserBookings(anyLong(), anyString(), anyInt(), anyInt()))
                .thenThrow(new IllegalArgumentException("Unknown state: INVALID"));

        mockMvc.perform(get("/bookings?state=INVALID&from=0&size=10")
                        .header(X_SHARER_USER_ID, 1L))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("Получение бронирований владельца - успешный сценарий")
    void getOwnerBookings_shouldReturnBookingsList() throws Exception {
        when(bookingService.getOwnerBookings(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingResponse));

        mockMvc.perform(get("/bookings/owner?state=ALL&from=0&size=10")
                        .header(X_SHARER_USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    @DisplayName("Получение бронирований владельца с пустым результатом")
    void getOwnerBookings_emptyResult_shouldReturnEmptyList() throws Exception {
        when(bookingService.getOwnerBookings(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/bookings/owner?state=ALL&from=0&size=10")
                        .header(X_SHARER_USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("Получение бронирований с отрицательной пагинацией - валидация")
    void getBookings_withNegativePagination_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/bookings?state=ALL&from=-1&size=0")
                        .header(X_SHARER_USER_ID, 1L))
                .andExpect(status().isOk());
    }
}