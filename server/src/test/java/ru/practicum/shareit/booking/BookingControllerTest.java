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
}