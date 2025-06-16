package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemStorage {
    Item createItem(Item item);

    Item updateItemById(Item item, Long itemId);

    Item findItemById(Long itemId);

    Collection<Item> findAllByUserId(Long userId);

    Collection<Item> searchItemsByText(String text);
}
