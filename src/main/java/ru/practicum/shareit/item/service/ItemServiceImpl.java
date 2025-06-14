package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dao.ItemStorage;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemMapper itemMapper;
    private final UserMapper userMapper;
    private final ItemStorage itemStorage;

    private static final String ITEM_NOT_FOUND_ERR = "Вещь с id %d не найдена";


    @Override
    public ItemDto createItem(ItemDto itemDto, UserDto userDto) {
        Item item = itemMapper.toItem(itemDto);
        item.setOwner(userMapper.toUser(userDto));

        return itemMapper.toItemDto(itemStorage.createItem(item));
    }

    @Override
    public ItemDto updateItemById(ItemDto itemDto, Long userId, Long itemId) {
        if (isUserOwnerOfItem(itemId, userId)) {
            return itemMapper.toItemDto(itemStorage.updateItemById(itemMapper.toItem(itemDto), itemId));
        } else {
            throw new ValidationException(String.format("Вещь с id %d не принадлежит пользователю с id %d",
                    itemId, userId));
        }
    }

    @Override
    public ItemDto findItemById(Long itemId) {
        Item item = itemStorage.findItemById(itemId);
        if (item != null) {
            return itemMapper.toItemDto(itemStorage.findItemById(itemId));
        } else {
            throw new ValidationException(String.format(ITEM_NOT_FOUND_ERR, itemId));
        }
    }

    @Override
    public Collection<ItemDto> findAllByUserId(Long userId) {
        return itemStorage.findAllByUserId(userId).stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ItemDto> searchItemsByText(String text) {
        if (text == null || text.isEmpty()) {
            return new ArrayList<>();
        }
        return itemStorage.searchItemsByText(text).stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    private Boolean isUserOwnerOfItem(Long itemId, Long userId) {
        Item item = itemStorage.findItemById(itemId);
        if (item != null) {
            return userId.equals(item.getOwner().getId());
        } else {
            throw new ValidationException(String.format(ITEM_NOT_FOUND_ERR, itemId));
        }
    }
}
