package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.service.ItemService;

import java.util.ArrayList;
import java.util.List;

import static ru.practicum.shareit.util.Constants.X_SHARER_USER_ID;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto createItem(
            @RequestHeader(X_SHARER_USER_ID) Long userId,
            @Validated @RequestBody ItemDto itemDto
    ) {
        log.info("POST /items - создание вещи пользователем с ID={}, данные: {}", userId, itemDto);
        return itemService.createItem(userId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(
            @RequestHeader(X_SHARER_USER_ID) Long userId,
            @PathVariable Long itemId,
            @RequestBody CommentDto commentDto
    ) {
        log.info("POST /items/{}/comment - добавление комментария пользователем с ID={}, данные: {}",
                itemId, userId, commentDto);
        return itemService.addComment(userId, itemId, commentDto);
    }

    @GetMapping("/{itemId}")
    public ItemDtoResponse getItem(
            @RequestHeader(X_SHARER_USER_ID) Long userId,
            @PathVariable Long itemId
    ) {
        log.info("GET /items/{} - запрос вещи пользователем с ID={}", itemId, userId);
        return itemService.getItem(userId, itemId);
    }

    @GetMapping
    public List<ItemDtoResponse> getUserItems(@RequestHeader(X_SHARER_USER_ID) Long userId) {
        log.info("GET /items - запрос всех вещей пользователя с ID={}", userId);
        return new ArrayList<>(itemService.getUserItems(userId));
    }

    @GetMapping("/search")
    public List<ItemDtoResponse> getItemsByPattern(@RequestParam("text") String pattern) {
        log.info("GET /items/search?text={} - поиск вещей по тексту", pattern);
        return new ArrayList<>(itemService.getItemsByPattern(pattern));
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(
            @RequestHeader(X_SHARER_USER_ID) Long userId,
            @PathVariable Long itemId,
            @RequestBody ItemDto itemDto
    ) {
        log.info("PATCH /items/{} - обновление вещи пользователем с ID={}, данные: {}",
                itemId, userId, itemDto);
        return itemService.updateItem(userId, itemId, itemDto);
    }

    @DeleteMapping("/{id}")
    public void deleteItem(@PathVariable Long id) {
        log.info("DELETE /items/{} - удаление вещи", id);
        itemService.deleteItem(id);
    }
}
