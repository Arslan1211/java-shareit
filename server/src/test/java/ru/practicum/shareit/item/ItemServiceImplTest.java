package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private UserService userService;
    @Mock
    private ItemRequestRepository requestRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    private User testUser;
    private Item testItem;
    private ItemDto testItemDto;
    private Comment testComment;
    private Booking testBooking;

    @BeforeEach
    void setUp() {
        // Инициализация тестовых данных
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("testUser");
        testUser.setEmail("test@mail.ru");

        testItem = new Item();
        testItem.setId(1L);
        testItem.setName("Дрель");
        testItem.setDescription("Простая дрель");
        testItem.setAvailable(true);
        testItem.setOwner(testUser);

        testItemDto = new ItemDto();
        testItemDto.setId(1L);
        testItemDto.setName("Дрель");
        testItemDto.setDescription("Простая дрель");
        testItemDto.setAvailable(true);

        testComment = new Comment();
        testComment.setId(1L);
        testComment.setText("Хорошая вещь");
        testComment.setItem(testItem);
        testComment.setAuthor(testUser);
        testComment.setCreated(LocalDateTime.now());

        testBooking = new Booking();
        testBooking.setId(1L);
        testBooking.setStartDate(LocalDateTime.now().plusDays(1));
        testBooking.setEndDate(LocalDateTime.now().plusDays(2));
        testBooking.setItem(testItem);
        testBooking.setBooker(testUser);


    }

    @Test
    @Transactional
    @DisplayName("Создание вещи с запросом - должен сохранить requestId")
    void createItem_withRequest_shouldCreateItemWithRequest() {
        // 1. Подготовка тестовых данных
        Long requestId = 1L;
        Long userId = 1L;

        // Подготовка запроса
        ItemRequest request = new ItemRequest();
        request.setId(requestId);

        // Подготовка DTO с requestId
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Дрель");
        itemDto.setDescription("Простая дрель");
        itemDto.setAvailable(true);
        itemDto.setRequestId(requestId); // Устанавливаем requestId

        // Подготовка пользователя
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("testUser")
                .email("test@mail.ru")
                .build();

        // Подготовка ожидаемого результата
        Item expectedItem = new Item();
        expectedItem.setName("Дрель");
        expectedItem.setDescription("Простая дрель");
        expectedItem.setAvailable(true);
        expectedItem.setRequest(request); // Важно установить связь с запросом

        // 2. Настройка моков
        when(userService.getUser(userId)).thenReturn(userDto);
        when(requestRepository.findById(requestId)).thenReturn(Optional.of(request));
        when(itemRepository.save(any(Item.class))).thenAnswer(invocation -> {
            Item savedItem = invocation.getArgument(0);
            savedItem.setId(1L); // Эмулируем сохранение в БД
            return savedItem;
        });

        // 3. Выполнение
        ItemDto result = itemService.createItem(userId, itemDto);

        // 4. Проверки
        assertNotNull(result);
        assertEquals(requestId, result.getRequestId(), "RequestId должен сохраниться");
        verify(requestRepository).findById(requestId);
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void createItem_shouldSaveItem() {
        // Подготовка UserDto
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("user")
                .email("user@mail.ru")
                .build();

        when(userService.getUser(anyLong())).thenReturn(userDto);

        // Подготовка ItemDto
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Дрель");
        itemDto.setDescription("Простая дрель");
        itemDto.setAvailable(true);

        // Подготовка сохраненного Item
        User owner = new User();
        owner.setId(1L);

        Item savedItem = new Item();
        savedItem.setId(1L);
        savedItem.setName("Дрель");
        savedItem.setDescription("Простая дрель");
        savedItem.setAvailable(true);
        savedItem.setOwner(owner);

        when(itemRepository.save(any())).thenReturn(savedItem);

        // Вызов метода
        ItemDto result = itemService.createItem(1L, itemDto);

        // Проверки
        assertEquals(1L, result.getId());
        verify(userService).getUser(1L);
        verify(itemRepository).save(any());
    }

    @Test
    @DisplayName("Добавление комментария - должен сохранить текст комментария")
    void addComment_shouldAddCommentSuccessfully() {
        // 1. Подготовка тестовых данных
        Long userId = 1L;
        Long itemId = 1L;
        String commentText = "Отлично!";
        LocalDateTime fixedDateTime = LocalDateTime.now();

        // Подготовка пользователя
        User user = new User();
        user.setId(userId);
        user.setName("Тестовый пользователь");
        user.setEmail("test@email.com");

        // Подготовка вещи
        User owner = new User(2L, "Владелец", "owner@email.com");
        Item item = new Item();
        item.setId(itemId);
        item.setName("Дрель");
        item.setDescription("Мощная дрель");
        item.setAvailable(true);
        item.setOwner(owner);

        // Подготовка бронирования
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStartDate(LocalDateTime.now().minusDays(2));
        booking.setEndDate(LocalDateTime.now().minusDays(1));
        booking.setStatus(BookingStatus.APPROVED);

        // Подготовка DTO
        CommentDto inputDto = new CommentDto();
        inputDto.setText(commentText);

        // 2. Настройка моков
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.findByItemIdAndBookerIdAndEndDateIsBefore(
                eq(itemId), eq(userId), any(LocalDateTime.class))
        ).thenReturn(List.of(booking));

        // Моделируем сохранение комментария с установкой даты
        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> {
            Comment commentToSave = invocation.getArgument(0);
            commentToSave.setId(1L);
            if (commentToSave.getCreated() == null) {
                commentToSave.setCreated(fixedDateTime);
            }
            return commentToSave;
        });

        // 3. Выполнение
        CommentDto result = itemService.addComment(userId, itemId, inputDto);

        // 4. Проверки
        assertNotNull(result, "Результат не должен быть null");
        assertEquals(commentText, result.getText(), "Текст комментария должен совпадать");
        assertEquals(user.getName(), result.getAuthorName(), "Имя автора должно совпадать");
        assertNotNull(result.getCreated(), "Дата создания должна быть установлена");

        verify(userRepository).findById(userId);
        verify(itemRepository).findById(itemId);
        verify(bookingRepository).findByItemIdAndBookerIdAndEndDateIsBefore(
                eq(itemId), eq(userId), any(LocalDateTime.class));
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    @DisplayName("Добавление комментария без бронирования - должно выбросить исключение")
    void addComment_withoutBooking_shouldThrowValidationException() {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Отлично!");

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(testItem));
        when(bookingRepository.findByItemIdAndBookerIdAndEndDateIsBefore(
                anyLong(), anyLong(), any(LocalDateTime.class))
        ).thenReturn(Collections.emptyList());

        assertThrows(ValidationException.class, () ->
                itemService.addComment(1L, 1L, commentDto));
    }

    @Test
    @DisplayName("Получение вещи владельцем - полная информация")
    void getItem_byOwner_shouldReturnFullInfo() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(testItem));
        when(bookingRepository.findByItemId(anyLong())).thenReturn(List.of(testBooking));
        when(commentRepository.findByItemId(anyLong())).thenReturn(List.of(testComment));

        ItemDtoResponse result = itemService.getItem(1L, 1L);

        assertNotNull(result);
        assertNotNull(result.getLastBooking());
        assertNotNull(result.getNextBooking());
        assertFalse(result.getComments().isEmpty());
    }

    @Test
    @DisplayName("Получение вещи не владельцем - базовая информация")
    void getItem_byNotOwner_shouldReturnBasicInfo() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(testItem));
        when(bookingRepository.findByItemId(anyLong())).thenReturn(List.of(testBooking));
        when(commentRepository.findByItemId(anyLong())).thenReturn(List.of(testComment));

        ItemDtoResponse result = itemService.getItem(2L, 1L);

        assertNotNull(result);
        assertNull(result.getLastBooking());
        assertNull(result.getNextBooking());
    }

    @Test
    @DisplayName("Получение списка вещей пользователя")
    void getUserItems_shouldReturnUserItems() {
        when(itemRepository.findAllByOwnerId(anyLong())).thenReturn(List.of(testItem));
        when(bookingRepository.findByItemId(anyLong())).thenReturn(List.of(testBooking));
        when(commentRepository.findByItemId(anyLong())).thenReturn(List.of(testComment));

        Collection<ItemDtoResponse> result = itemService.getUserItems(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Поиск вещей по тексту - успешный сценарий")
    void getItemsByPattern_shouldReturnFilteredItems() {
        when(itemRepository.search(anyString())).thenReturn(List.of(testItem));
        when(bookingRepository.findByItemId(anyLong())).thenReturn(List.of(testBooking));
        when(commentRepository.findByItemId(anyLong())).thenReturn(List.of(testComment));

        Collection<ItemDtoResponse> result = itemService.getItemsByPattern("дрель");

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Поиск с пустым текстом - должен вернуть пустой список")
    void getItemsByPattern_emptyText_shouldReturnEmptyList() {
        Collection<ItemDtoResponse> result = itemService.getItemsByPattern("");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Обновление вещи - успешный сценарий")
    void updateItem_shouldUpdateItemSuccessfully() {
        // 1. Подготовка данных
        Long ownerId = 1L;
        Long itemId = 1L;

        // Создаем владельца
        User owner = new User();
        owner.setId(ownerId);
        owner.setName("Владелец");
        owner.setEmail("owner@mail.ru");

        // Создаем существующую вещь через сеттеры
        Item existingItem = new Item();
        existingItem.setId(itemId);
        existingItem.setName("Дрель");
        existingItem.setDescription("Простая дрель");
        existingItem.setAvailable(true);
        existingItem.setOwner(owner);

        ItemDto updateDto = new ItemDto();
        updateDto.setName("Дрель+");

        // 2. Настройка моков
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(existingItem));

        // 3. Выполнение
        ItemDto result = itemService.updateItem(ownerId, itemId, updateDto);

        // 4. Проверки
        assertNotNull(result);
        assertEquals("Дрель+", result.getName());
        assertEquals("Простая дрель", result.getDescription());

        // Проверяем, что поля обновились
        assertEquals("Дрель+", existingItem.getName());
    }

    @Test
    @DisplayName("Обновление несуществующей вещи - должно выбросить исключение")
    void updateItem_notFound_shouldThrowNotFoundException() {
        ItemDto updateDto = new ItemDto();
        updateDto.setName("Дрель+");

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                itemService.updateItem(1L, 1L, updateDto));
    }

    @Test
    @Transactional
    @DisplayName("Удаление вещи - успешный сценарий")
    void deleteItem_shouldDeleteItemSuccessfully() {
        doNothing().when(itemRepository).deleteById(anyLong());

        assertDoesNotThrow(() -> itemService.deleteItem(1L));
        verify(itemRepository).deleteById(1L);
    }
}