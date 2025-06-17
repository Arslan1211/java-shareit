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

import java.util.Collection;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;

    private static final String ITEM_NOT_FOUND_ERR = "Вещь с id %d не найдена";


    @Override
    public ItemDto createItem(ItemDto itemDto, UserDto userDto) {
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(UserMapper.toUser(userDto));

        return ItemMapper.toItemDto(itemStorage.createItem(item));
    }

    @Override
    public ItemDto updateItemById(ItemDto itemDto, Long userId, Long itemId) {
        if (Boolean.TRUE.equals(isUserOwnerOfItem(itemId, userId))) {
            return ItemMapper.toItemDto(itemStorage.updateItemById(ItemMapper.toItem(itemDto), itemId));
        } else {
            throw new ValidationException(String.format("Вещь с id %d не принадлежит пользователю с id %d",
                    itemId, userId));
        }
    }

    @Override
    public ItemDto findItemById(Long itemId) {
        Item item = itemStorage.findItemById(itemId);
        if (item != null) {
            return ItemMapper.toItemDto(itemStorage.findItemById(itemId));
        } else {
            throw new ValidationException(String.format(ITEM_NOT_FOUND_ERR, itemId));
        }
    }

    @Override
    public Collection<ItemDto> findAllByUserId(Long userId) {
        return itemStorage.findAllByUserId(userId).stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }

    @Override
    public Collection<ItemDto> searchItemsByText(String text) {
        if (text == null || text.isEmpty()) {
            return Collections.emptyList();
        }
        return itemStorage.searchItemsByText(text).stream()
                .map(ItemMapper::toItemDto)
                .toList();
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