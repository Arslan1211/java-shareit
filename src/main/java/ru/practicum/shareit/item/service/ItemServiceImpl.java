package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
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
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.*;

import static ru.practicum.shareit.item.mapper.CommentMapper.toComment;
import static ru.practicum.shareit.item.mapper.CommentMapper.toCommentDto;
import static ru.practicum.shareit.item.mapper.ItemMapper.*;
import static ru.practicum.shareit.user.mapper.UserMapper.toUser;
import static ru.practicum.shareit.util.Constants.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final UserService userService;


    @Override
    @Transactional
    public ItemDto createItem(Long userId, ItemDto itemDto) {
        User user = toUser(userService.findUserById(userId));
        log.info("Создание предмета: {}", itemDto);
        Item item = toItem(itemDto);
        item.setOwner(user);
        Item savedItem = itemRepository.save(item);
        log.info("Предмет создан: {}", item);
        return toItemDto(savedItem);
    }

    @Override
    @Transactional
    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format(USER_NOT_FOUND_ERR, userId)));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format(ITEM_NOT_FOUND_ERR, itemId)));

        Collection<Booking> bookings = bookingRepository.findByItemIdAndBookerIdAndEndDateIsBefore(
                itemId, userId, LocalDateTime.now()
        );

        if (bookings.isEmpty()) throw new ValidationException(USER_HAS_NOT_BROOKED_ITEM);

        Comment comment = toComment(commentDto, item, user);
        Comment savedComment = commentRepository.save(comment);

        return toCommentDto(savedComment);
    }

    @Override
    public ItemDtoResponse getItem(Long userId, Long itemId) {
        log.info("Получение элемента с идентификатором: {}", itemId);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format(ITEM_NOT_FOUND_ERR, itemId)));

        if (!item.getOwner().getId().equals(userId)) {
            Collection<Booking> bookings = bookingRepository.findByItemId(item.getId());
            Collection<Comment> comments = commentRepository.findByItemId(item.getId());
            return toItemDtoResponse(item, bookings, comments);
        }

        log.info("Найден предмет: {}", item);
        return toItemDtoResponseWithBookingsAndComments(item);
    }

    @Override
    public Collection<ItemDtoResponse> getUserItems(Long userId) {
        log.info("Получение элементов для пользователя с идентификатором: {}", userId);
        List<Item> items = new ArrayList<>(itemRepository.findAllByOwnerId(userId));
        List<ItemDtoResponse> itemDtos = items.stream().map(this::toItemDtoResponseWithBookingsAndComments).toList();

        log.info("Найден {} предметы для пользователя с идентификатором: {}", items.size(), userId);
        return itemDtos;
    }

    @Override
    public Collection<ItemDtoResponse> getItemsByPattern(String pattern) {
        if (pattern.trim().isEmpty()) return Collections.emptyList();

        log.info("Поиск элементов по шаблону: {}", pattern);

        List<Item> items = itemRepository.search(pattern).stream()
                .filter(Item::getAvailable)
                .toList();

        List<ItemDtoResponse> itemDtos = items.stream().map(this::toItemDtoResponseWithBookingsAndComments).toList();

        log.info("Найдены {} предметы", items.size());
        return itemDtos;
    }

    @Override
    @Transactional
    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException(String.format(USER_NOT_FOUND_ERR, userId)));

        log.info("Обновление предмета с идентификатором: {} для пользователя с идентификатором: {}", itemId, userId);
        Item updatingItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format(ITEM_NOT_FOUND_ERR, itemId)));

        if (Objects.nonNull(itemDto.getName())) updatingItem.setName(itemDto.getName());
        if (Objects.nonNull(itemDto.getDescription())) updatingItem.setDescription(itemDto.getDescription());
        if (Objects.nonNull(itemDto.getAvailable())) updatingItem.setAvailable(itemDto.getAvailable());

        log.info("Обновленный предмет: {}", updatingItem);
        return toItemDto(updatingItem);
    }

    @Override


    public void deleteItem(Long itemId) {
        log.info("Удаление элемента с идентификатором: {}", itemId);
        itemRepository.deleteById(itemId);
        log.info("Объект удален: {}", itemId);
    }

    private ItemDtoResponse toItemDtoResponseWithBookingsAndComments(Item item) {
        Collection<Booking> bookings = bookingRepository.findByItemId(item.getId());
        Collection<Comment> comments = commentRepository.findByItemId(item.getId());

        LocalDateTime lastBookingDate = bookings.stream()
                .map(Booking::getEndDate)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .orElse(null);

        LocalDateTime nextBookingDate = bookings.stream()
                .map(Booking::getStartDate)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(null);

        ItemDtoResponse itemDtoResponse = toItemDtoResponse(item, bookings, comments);
        itemDtoResponse.setLastBooking(lastBookingDate);
        itemDtoResponse.setNextBooking(nextBookingDate);
        return itemDtoResponse;
    }
}