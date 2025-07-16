package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ItemRequestMapperTest {

    @Test
    void toItemRequest_ShouldConvertDtoToEntity() {
        ItemRequestDto dto = ItemRequestDto.builder()
                .description("Нужна дрель")
                .build();

        ItemRequest result = ItemRequestMapper.toItemRequest(dto);

        assertNotNull(result);
        assertEquals("Нужна дрель", result.getDescription());
        assertNull(result.getId());
        assertNull(result.getCreated());
    }

    @Test
    void toItemRequestDto_ShouldConvertEntityToDto() {
        LocalDateTime created = LocalDateTime.now();
        ItemRequest entity = new ItemRequest();
        entity.setId(1L);
        entity.setDescription("Нужна дрель");
        entity.setCreated(created);

        ItemRequestDto result = ItemRequestMapper.toItemRequestDto(entity);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Нужна дрель", result.getDescription());
        assertEquals(created, result.getCreated());
    }

    @Test
    void toItemRequest_WithNullDto_ShouldThrowException() {
        assertThrows(NullPointerException.class, () -> {
            ItemRequestMapper.toItemRequest(null);
        });
    }

    @Test
    void toItemRequestDto_WithNullEntity_ShouldThrowException() {
        assertThrows(NullPointerException.class, () -> {
            ItemRequestMapper.toItemRequestDto(null);
        });
    }

    @Test
    void toItemRequest_WithEmptyDescription_ShouldWork() {
        ItemRequestDto dto = ItemRequestDto.builder()
                .description("")
                .build();

        ItemRequest result = ItemRequestMapper.toItemRequest(dto);

        assertNotNull(result);
        assertEquals("", result.getDescription());
    }
}