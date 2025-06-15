package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private final UserService userService;

    private static final String X_SHARER_USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public ItemDto createItem(@RequestHeader(X_SHARER_USER_ID) Long userId, @RequestBody @Valid ItemDto itemDto) {
        UserDto userDto = userService.findUserById(userId);
        return itemService.createItem(itemDto, userDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItemById(@RequestHeader(X_SHARER_USER_ID) Long userId, @RequestBody ItemDto itemDto,
                                  @PathVariable("itemId") Long itemId) {
        userService.findUserById(userId);
        return itemService.updateItemById(itemDto, userId, itemId);
    }

    @GetMapping("/{itemId}")
    public ItemDto findItemById(@RequestHeader(X_SHARER_USER_ID) Long userId, @PathVariable("itemId") Long itemId) {
        userService.findUserById(userId);
        return itemService.findItemById(itemId);
    }

    @GetMapping
    public Collection<ItemDto> findAllByUserId(@RequestHeader(X_SHARER_USER_ID) Long userId) {
        userService.findUserById(userId);
        return itemService.findAllByUserId(userId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> searchItemsByText(@RequestHeader(X_SHARER_USER_ID) Long userId,
                                                 @RequestParam(name = "text") String text) {
        userService.findUserById(userId);
        return itemService.searchItemsByText(text);
    }
}
