package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.service.ItemService;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
@AutoConfigureMockMvc
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    private final ObjectMapper mapper = new ObjectMapper();
    private static final String X_SHARER_USER_ID = "X-Sharer-User-Id";

    @Test
    @DisplayName("Создание вещи - успешный сценарий")
    void createItem_shouldReturnCreatedItem() throws Exception {
        ItemDto itemDto = new ItemDto(null, "Дрель", "Простая дрель", true, null);
        ItemDto expected = new ItemDto(1L, "Дрель", "Простая дрель", true, null);

        when(itemService.createItem(anyLong(), any(ItemDto.class))).thenReturn(expected);

        mockMvc.perform(post("/items")
                        .header(X_SHARER_USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Дрель"));
    }

    @Test
    @DisplayName("Добавление комментария - успешный сценарий")
    void addComment_shouldReturnComment() throws Exception {
        CommentDto commentDto = new CommentDto(null, "Отличная дрель!", null, null);
        CommentDto expected = new CommentDto(1L, "Отличная дрель!", "user", LocalDateTime.now());

        when(itemService.addComment(anyLong(), anyLong(), any(CommentDto.class))).thenReturn(expected);

        mockMvc.perform(post("/items/1/comment")
                        .header(X_SHARER_USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.text").value("Отличная дрель!"));
    }

    @Test
    @DisplayName("Получение вещи по ID - успешный сценарий")
    void getItem_shouldReturnItem() throws Exception {
        ItemDtoResponse expected = ItemDtoResponse.builder()
                .id(1L)
                .name("Дрель")
                .description("Простая дрель")
                .available(true)
                .build();

        when(itemService.getItem(anyLong(), anyLong())).thenReturn(expected);

        mockMvc.perform(get("/items/1")
                        .header(X_SHARER_USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Дрель"));
    }

    @Test
    @DisplayName("Получение всех вещей пользователя - успешный сценарий")
    void getUserItems_shouldReturnItemsList() throws Exception {
        List<ItemDtoResponse> expected = List.of(
                ItemDtoResponse.builder().id(1L).name("Дрель").build(),
                ItemDtoResponse.builder().id(2L).name("Молоток").build()
        );

        when(itemService.getUserItems(anyLong())).thenReturn(expected);

        mockMvc.perform(get("/items")
                        .header(X_SHARER_USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L));
    }

    @Test
    @DisplayName("Поиск вещей по тексту - успешный сценарий")
    void searchItems_shouldReturnFilteredList() throws Exception {
        List<ItemDtoResponse> expected = List.of(
                ItemDtoResponse.builder().id(1L).name("Дрель").build()
        );

        when(itemService.getItemsByPattern(anyString())).thenReturn(expected);

        mockMvc.perform(get("/items/search")
                        .param("text", "дрель"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    @DisplayName("Обновление вещи - успешный сценарий")
    void updateItem_shouldReturnUpdatedItem() throws Exception {
        ItemDto itemDto = new ItemDto(null, "Дрель+", null, null, null);
        ItemDto expected = new ItemDto(1L, "Дрель+", "Простая дрель", true, null);

        when(itemService.updateItem(anyLong(), anyLong(), any(ItemDto.class))).thenReturn(expected);

        mockMvc.perform(patch("/items/1")
                        .header(X_SHARER_USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Дрель+"));
    }

    @Test
    @DisplayName("Удаление вещи - успешный сценарий")
    void deleteItem_shouldReturnNoContent() throws Exception {
        doNothing().when(itemService).deleteItem(anyLong());

        mockMvc.perform(delete("/items/1"))
                .andExpect(status().isOk());

        verify(itemService).deleteItem(1L);
    }

    @Test
    @DisplayName("Создание вещи без заголовка - ошибка валидации")
    void createItem_withoutUserId_shouldReturnBadRequest() throws Exception {
        ItemDto itemDto = new ItemDto(null, "Дрель", "Простая дрель", true, null);

        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest());
    }

/*    @Test
    @DisplayName("Создание вещи с невалидными данными - должен вернуть 400 с ошибками валидации")
    void createItem_withInvalidData_shouldReturnBadRequest() throws Exception {
        // Невалидные данные: пустое имя, пустое описание, null available
        String invalidItemJson = "{\"name\":\"\",\"description\":\"\",\"available\":null}";

        mockMvc.perform(post("/items")
                        .header(X_SHARER_USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidItemJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.name").value("Имя не должно быть пустым"))
                .andExpect(jsonPath("$.validationErrors.description").value("Описание не должно быть пустым"))
                .andExpect(jsonPath("$.validationErrors.available").value("Значение 'Available' не должно быть null"));
    }*/
}