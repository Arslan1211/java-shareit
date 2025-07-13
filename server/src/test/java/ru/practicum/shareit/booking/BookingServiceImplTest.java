package ru.practicum.shareit.booking;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.practicum.shareit.booking.model.BookingStatus.*;

@MockitoSettings(strictness = Strictness.LENIENT)
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

    private final Long userId = 1L;
    private final Long ownerId = 2L;
    private final Long bookingId = 1L;
    private final Long itemId = 1L;
    private final LocalDateTime now = LocalDateTime.now();
    private final LocalDateTime start = now.plusDays(1);
    private final LocalDateTime end = now.plusDays(2);
    private final Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "startDate"));

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

    @Test
    void createBooking() {
        BookingDto dto = BookingDto.builder()
                .itemId(1L)
                .start(now.plusDays(1))
                .end(now.plusDays(2))
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

    @Test
    void createBooking_ShouldCreateBookingSuccessfully() {
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

        when(userRepository.findById(userId)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.save(any(Booking.class))).thenReturn(expectedBooking);

        BookingDtoResponse result = bookingService.createBooking(userId, bookingDto);

        assertNotNull(result);
        assertEquals(WAITING, result.getStatus());
        verify(userRepository).findById(userId);
        verify(itemRepository).findById(itemId);
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void createBooking_WhenItemNotAvailable_ShouldThrowException() {
        User booker = new User();
        booker.setId(userId);

        Item item = new Item();
        item.setId(itemId);
        item.setAvailable(false);

        BookingDto bookingDto = new BookingDto(1L, itemId, start, end);

        when(userRepository.findById(userId)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        assertThrows(ValidationException.class, () -> bookingService.createBooking(userId, bookingDto));
    }

    @Test
    void updateBookingStatus_ShouldApproveBookingSuccessfully() {
        User owner = new User();
        owner.setId(ownerId);

        User booker = new User();
        booker.setId(userId);

        Item item = new Item();
        item.setId(itemId);
        item.setOwner(owner);

        Booking booking = createTestBooking(bookingId, booker, item, start, end, WAITING);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        BookingDtoResponse result = bookingService.updateBookingStatus(ownerId, bookingId, true);

        assertEquals(APPROVED, result.getStatus());
    }

    @Test
    void getBooking_WhenBookerRequests_ShouldReturnBooking() {
        User booker = new User();
        booker.setId(userId);

        Item item = new Item();
        item.setId(itemId);

        Booking booking = createTestBooking(bookingId, booker, item, start, end, APPROVED);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        BookingDtoResponse result = bookingService.getBooking(userId, bookingId);
        assertNotNull(result);
    }

    @Test
    void getUserBookings_ShouldReturnBookingsList() {
        User booker = new User();
        booker.setId(userId);

        Page<Booking> page = new PageImpl<>(Collections.emptyList());

        when(userRepository.existsById(userId)).thenReturn(true);
        when(bookingRepository.findByBookerId(eq(userId), any(Pageable.class))).thenReturn(page);

        Collection<BookingDtoResponse> result = bookingService.getUserBookings(userId, "ALL", 0, 10);
        assertNotNull(result);
    }

    @Test
    @DisplayName("Получение WAITING бронирований для арендатора")
    void getUserBookings_WaitingState_ShouldReturnWaitingBookings() {
        // Подготовка данных
        User booker = new User();
        booker.setId(userId);

        Item item = new Item();
        item.setId(itemId);

        Booking waitingBooking = createTestBooking(bookingId, booker, item,
                now.plusDays(1), now.plusDays(2), WAITING);

        Page<Booking> page = new PageImpl<>(List.of(waitingBooking));

        // Настройка моков
        when(userRepository.existsById(userId)).thenReturn(true);
        when(bookingRepository.findByBookerIdAndStatus(
                eq(userId), eq(WAITING), any(Pageable.class)))
                .thenReturn(page);

        // Выполнение
        Collection<BookingDtoResponse> result = bookingService.getUserBookings(
                userId, "WAITING", 0, 10);

        // Проверки
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(bookingRepository).findByBookerIdAndStatus(
                eq(userId), eq(WAITING), any(Pageable.class));
    }

    @Test
    @DisplayName("Получение REJECTED бронирований для владельца")
    void getOwnerBookings_RejectedState_ShouldReturnRejectedBookings() {
        // Подготовка данных
        User owner = new User();
        owner.setId(userId);

        Item item = new Item();
        item.setId(itemId);
        item.setOwner(owner);

        Booking rejectedBooking = createTestBooking(bookingId, owner, item,
                now.plusDays(1), now.plusDays(2), REJECTED);

        Page<Booking> page = new PageImpl<>(List.of(rejectedBooking));

        // Настройка моков
        when(userRepository.existsById(userId)).thenReturn(true);
        when(bookingRepository.findByItemOwnerIdAndStatus(
                eq(userId), eq(REJECTED), any(Pageable.class)))
                .thenReturn(page);

        // Выполнение
        Collection<BookingDtoResponse> result = bookingService.getOwnerBookings(
                userId, "REJECTED", 0, 10);

        // Проверки
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(bookingRepository).findByItemOwnerIdAndStatus(
                eq(userId), eq(REJECTED), any(Pageable.class));
    }

    @Test
    @DisplayName("Получение ALL бронирований для арендатора")
    void getUserBookings_AllState_ShouldReturnAllBookings() {
        // Подготовка данных
        User booker = new User();
        booker.setId(userId);

        Item item = new Item();
        item.setId(itemId);

        Booking booking1 = createTestBooking(bookingId, booker, item,
                now.minusDays(1), now.plusDays(1), APPROVED);
        Booking booking2 = createTestBooking(bookingId + 1, booker, item,
                now.plusDays(1), now.plusDays(2), WAITING);

        Page<Booking> page = new PageImpl<>(List.of(booking1, booking2));

        // Настройка моков
        when(userRepository.existsById(userId)).thenReturn(true);
        when(bookingRepository.findByBookerId(eq(userId), any(Pageable.class)))
                .thenReturn(page);

        // Выполнение
        Collection<BookingDtoResponse> result = bookingService.getUserBookings(
                userId, "ALL", 0, 10);

        // Проверки
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(bookingRepository).findByBookerId(eq(userId), any(Pageable.class));
    }

    @Test
    @DisplayName("Получение бронирований с дефолтной пагинацией")
    void getOwnerBookings_DefaultPagination() {
        // 1. Подготовка
        Pageable defaultPageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "startDate"));

        User owner = new User();
        owner.setId(userId);

        Item item = new Item();
        item.setId(itemId);
        item.setOwner(owner);

        Booking booking = createTestBooking(bookingId, owner, item,
                now.plusDays(1), now.plusDays(2), APPROVED);

        Page<Booking> expectedPage = new PageImpl<>(List.of(booking), defaultPageable, 1);

        // 2. Настройка моков
        when(userRepository.existsById(userId)).thenReturn(true);
        when(bookingRepository.findByItemOwnerId(eq(userId), eq(defaultPageable)))
                .thenReturn(expectedPage);

        // 3. Выполнение
        Collection<BookingDtoResponse> result = bookingService.getOwnerBookings(
                userId, "ALL", 0, 10);

        // 4. Проверки
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    // Другие тесты должны использовать разные конфигурации моков
    @Test
    @DisplayName("Получение ALL бронирований для владельца с дефолтной пагинацией")
    void getOwnerBookings_AllState_DefaultPagination() {
        // Подготовка
        Pageable defaultPageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "startDate"));

        User owner = new User();
        owner.setId(userId);

        Item item = new Item();
        item.setId(itemId);
        item.setOwner(owner);

        Booking booking = createTestBooking(bookingId, new User(), item,
                now.plusDays(1), now.plusDays(2), APPROVED);

        Page<Booking> page = new PageImpl<>(List.of(booking), defaultPageable, 1);

        // Настройка моков для дефолтной пагинации
        when(userRepository.existsById(userId)).thenReturn(true);
        when(bookingRepository.findByItemOwnerId(eq(userId), eq(defaultPageable)))
                .thenReturn(page);

        // Выполнение
        Collection<BookingDtoResponse> result = bookingService.getOwnerBookings(
                userId, "ALL", 0, 10);

        // Проверки
        assertNotNull(result);
        assertEquals(1, result.size());
    }
}