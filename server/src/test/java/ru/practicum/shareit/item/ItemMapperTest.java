package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ItemMapperTest {

    @Test
    void toItem() {
        ItemDto dto = ItemDto.builder()
                .name("Item")
                .description("Description")
                .available(true)
                .build();

        Item item = ItemMapper.toItem(dto);

        assertEquals(dto.getName(), item.getName());
        assertEquals(dto.getDescription(), item.getDescription());
        assertEquals(dto.getAvailable(), item.getAvailable());
    }

    @Test
    void toItemDto() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Item");
        item.setDescription("Description");
        item.setAvailable(true);

        ItemDto dto = ItemMapper.toItemDto(item);

        assertEquals(item.getId(), dto.getId());
        assertEquals(item.getName(), dto.getName());
        assertEquals(item.getDescription(), dto.getDescription());
        assertEquals(item.getAvailable(), dto.getAvailable());
    }

    @Test
    void toItemDtoResponse() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Item");

        ItemDtoResponse response = ItemMapper.toItemDtoResponse(item, Collections.emptyList(), Collections.emptyList());

        assertEquals(item.getId(), response.getId());
        assertEquals(item.getName(), response.getName());
        assertTrue(response.getBookings().isEmpty());
        assertTrue(response.getComments().isEmpty());
    }
}