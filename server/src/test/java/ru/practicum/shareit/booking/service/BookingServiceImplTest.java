package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.practicum.shareit.booking.model.BookingStatus.APPROVED;
import static ru.practicum.shareit.booking.model.BookingStatus.WAITING;

/*
@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    // Мокируем все зависимости
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;

    // Внедряем моки в тестируемый сервис
    @InjectMocks
    private BookingServiceImpl bookingService;

    // Тестовые данные
    private final Long userId = 1L;
    private final Long ownerId = 2L;
    private final Long bookingId = 1L;
    private final Long itemId = 1L;
    private final LocalDateTime now = LocalDateTime.now();
    private final LocalDateTime start = now.plusDays(1);
    private final LocalDateTime end = now.plusDays(2);

    // Вспомогательный метод для создания тестового бронирования
    private Booking createTestBooking(Long id, User booker, Item item,
                                      LocalDateTime start, LocalDateTime end,
                                      BookingStatus status) {
        Booking booking = new Booking();
        booking.setId(id);
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStartDate(start);
        booking.setEndDate(end);
        booking.setStatus(status);
        return booking;
    }

    // Тест на успешное создание бронирования
    @Test
    void createBooking_ShouldCreateBookingSuccessfully() {
        // Подготовка данных
        User booker = new User();
        booker.setId(userId);

        User owner = new User();
        owner.setId(ownerId);

        Item item = new Item();
        item.setId(itemId);
        item.setOwner(owner);
        item.setAvailable(true);

        BookingDto bookingDto = new BookingDto(1L, itemId, start, end);
        Booking expectedBooking = createTestBooking(bookingId, booker, item, start, end, WAITING);

        // Настройка моков
        when(userRepository.findById(userId)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.save(any(Booking.class))).thenReturn(expectedBooking);

        // Вызов метода
        BookingDtoResponse result = bookingService.createBooking(userId, bookingDto);

        // Проверки
        assertNotNull(result);
        assertEquals(WAITING, result.getStatus());
        verify(userRepository).findById(userId);
        verify(itemRepository).findById(itemId);
        verify(bookingRepository).save(any(Booking.class));
    }

    // Тест на попытку бронирования недоступной вещи
    @Test
    void createBooking_WhenItemNotAvailable_ShouldThrowException() {
        // Подготовка
        User booker = new User();
        booker.setId(userId);

        Item item = new Item();
        item.setId(itemId);
        item.setAvailable(false);

        BookingDto bookingDto = new BookingDto(1L, itemId, start, end);

        // Настройка моков
        when(userRepository.findById(userId)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        // Проверка исключения
        assertThrows(ValidationException.class, () -> {
            bookingService.createBooking(userId, bookingDto);
        });
    }

    // Тест на подтверждение бронирования владельцем
    @Test
    void updateBookingStatus_ShouldApproveBookingSuccessfully() {
        // Подготовка
        User owner = new User();
        owner.setId(ownerId);

        User booker = new User();
        booker.setId(userId);

        Item item = new Item();
        item.setId(itemId);
        item.setOwner(owner);

        Booking booking = createTestBooking(bookingId, booker, item, start, end, WAITING);

        // Настройка моков
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        // Вызов метода
        BookingDtoResponse result = bookingService.updateBookingStatus(ownerId, bookingId, true);

        // Проверки
        assertEquals(APPROVED, result.getStatus());
    }

    // Тест на получение бронирования автором
    @Test
    void getBooking_WhenBookerRequests_ShouldReturnBooking() {
        // Подготовка
        User booker = new User();
        booker.setId(userId);

        Item item = new Item();
        item.setId(itemId);

        Booking booking = createTestBooking(bookingId, booker, item, start, end, APPROVED);

        // Настройка моков
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        // Вызов и проверка
        BookingDtoResponse result = bookingService.getBooking(userId, bookingId);
        assertNotNull(result);
    }

    // Тест на получение списка бронирований пользователя
    @Test
    void getUserBookings_ShouldReturnBookingsList() {
        // Подготовка
        User booker = new User();
        booker.setId(userId);

        Page<Booking> page = new PageImpl<>(Collections.emptyList());

        // Настройка моков
        when(userRepository.existsById(userId)).thenReturn(true);
        when(bookingRepository.findByBookerId(eq(userId), any(Pageable.class))).thenReturn(page);

        // Вызов и проверка
        Collection<BookingDtoResponse> result = bookingService.getUserBookings(userId, "ALL", 0, 10);
        assertNotNull(result);
    }
}*/
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Test
    void createBooking() {
        Long userId = 1L;
        BookingDto dto = BookingDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        User user = new User();
        user.setId(userId);

        Item item = new Item();
        item.setId(1L);
        item.setAvailable(true);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(dto.getItemId())).thenReturn(Optional.of(item));
        when(bookingRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        BookingDtoResponse response = bookingService.createBooking(userId, dto);

        assertNotNull(response);
        assertEquals(dto.getStart(), response.getStart());
        assertEquals(dto.getEnd(), response.getEnd());
    }

  /*  @Test
    void updateBookingStatus() {
        Long userId = 1L;
        Long bookingId = 1L;

        // Создаем владельца
        User owner = new User();
        owner.setId(userId);
        owner.setName("Owner");
        owner.setEmail("owner@example.com");

        // Создаем бронирующего пользователя
        User booker = new User();
        booker.setId(2L);
        booker.setName("Booker");
        booker.setEmail("booker@example.com");

        // Создаем вещь
        Item item = new Item();
        item.setId(1L);
        item.setOwner(owner);
        item.setName("Item");
        item.setAvailable(true);

        // Создаем бронирование
        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setItem(item);
        booking.setBooker(booker); // Устанавливаем бронирующего пользователя
        booking.setStartDate(LocalDateTime.now().plusDays(1));
        booking.setEndDate(LocalDateTime.now().plusDays(2));
        booking.setStatus(BookingStatus.WAITING);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // Test APPROVED
        BookingDtoResponse approvedResponse = bookingService.updateBookingStatus(userId, bookingId, true);
        assertEquals(BookingStatus.APPROVED, approvedResponse.getStatus());

        // Test REJECTED
        BookingDtoResponse rejectedResponse = bookingService.updateBookingStatus(userId, bookingId, false);
        assertEquals(BookingStatus.REJECTED, rejectedResponse.getStatus());
    }*/
}