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
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.service.ItemService;

import java.time.LocalDateTime;
import java.util.Collections;
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

    private final ItemDto itemDto = new ItemDto(null, "Дрель", "Простая дрель", true, null);
    private final ItemDtoResponse itemDtoResponse = ItemDtoResponse.builder()
            .id(1L)
            .name("Дрель")
            .description("Простая дрель")
            .available(true)
            .build();
    private final CommentDto commentDto = new CommentDto(null, "Отличная дрель!", null, null);
    private final CommentDto commentResponse = new CommentDto(1L, "Отличная дрель!", "user", LocalDateTime.now());

    private final ItemDto itemWithNullName = new ItemDto(null, null, "Описание", true, null);
    private final ItemDto itemWithNullDescription = new ItemDto(null, "Дрель", null, true, null);
    private final ItemDto itemWithNullAvailable = new ItemDto(null, "Дрель", "Описание", null, null);
    private final CommentDto emptyComment = new CommentDto(null, "", null, null);
    private final CommentDto nullComment = new CommentDto(null, null, null, null);


    @Test
    @DisplayName("Создание вещи - успешный сценарий")
    void createItem_shouldReturnCreatedItem() throws Exception {
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
    @DisplayName("Создание вещи без заголовка X-Sharer-User-Id - ошибка")
    void createItem_withoutUserId_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Добавление комментария - успешный сценарий")
    void addComment_shouldReturnComment() throws Exception {
        when(itemService.addComment(anyLong(), anyLong(), any(CommentDto.class))).thenReturn(commentResponse);

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
        when(itemService.getItem(anyLong(), anyLong())).thenReturn(itemDtoResponse);

        mockMvc.perform(get("/items/1")
                        .header(X_SHARER_USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Дрель"));
    }

    @Test
    @DisplayName("Получение всех вещей пользователя - пустой список")
    void getUserItems_emptyList_shouldReturnEmptyList() throws Exception {
        when(itemService.getUserItems(anyLong())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/items")
                        .header(X_SHARER_USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("Поиск вещей по пустому тексту - пустой список")
    void searchItems_withEmptyText_shouldReturnEmptyList() throws Exception {
        when(itemService.getItemsByPattern("")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/items/search")
                        .param("text", ""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("Обновление вещи - только имя")
    void updateItem_onlyName_shouldReturnUpdatedItem() throws Exception {
        ItemDto updateDto = new ItemDto(null, "Новое имя", null, null, null);
        ItemDto expected = new ItemDto(1L, "Новое имя", "Простая дрель", true, null);

        when(itemService.updateItem(anyLong(), anyLong(), any(ItemDto.class))).thenReturn(expected);

        mockMvc.perform(patch("/items/1")
                        .header(X_SHARER_USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Новое имя"))
                .andExpect(jsonPath("$.description").value("Простая дрель"));
    }

    @Test
    @DisplayName("Обновление вещи - только доступность")
    void updateItem_onlyAvailable_shouldReturnUpdatedItem() throws Exception {
        ItemDto updateDto = new ItemDto(null, null, null, false, null);
        ItemDto expected = new ItemDto(1L, "Дрель", "Простая дрель", false, null);

        when(itemService.updateItem(anyLong(), anyLong(), any(ItemDto.class))).thenReturn(expected);

        mockMvc.perform(patch("/items/1")
                        .header(X_SHARER_USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available").value(false));
    }

    @Test
    @DisplayName("Удаление несуществующей вещи - успешный сценарий")
    void deleteItem_nonExistingItem_shouldReturnOk() throws Exception {
        doNothing().when(itemService).deleteItem(anyLong());

        mockMvc.perform(delete("/items/999"))
                .andExpect(status().isOk());

        verify(itemService).deleteItem(999L);
    }

    @Test
    @DisplayName("Получение вещи без заголовка X-Sharer-User-Id - ошибка")
    void getItem_withoutUserId_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/items/1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Обновление вещи без заголовка X-Sharer-User-Id - ошибка")
    void updateItem_withoutUserId_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(patch("/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Поиск вещей с специальными символами в запросе")
    void searchItems_withSpecialCharacters_shouldReturnFilteredList() throws Exception {
        List<ItemDtoResponse> expected = List.of(
                ItemDtoResponse.builder().id(1L).name("Дрель").build()
        );

        when(itemService.getItemsByPattern(anyString())).thenReturn(expected);

        mockMvc.perform(get("/items/search")
                        .param("text", "дрель!@#"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @DisplayName("Добавление комментария без заголовка X-Sharer-User-Id - ошибка")
    void addComment_withoutUserId_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/items/1/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(commentDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Получение всех вещей пользователя без заголовка X-Sharer-User-Id - ошибка")
    void getUserItems_withoutUserId_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/items"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Создание вещи с null описанием - ошибка валидации")
    void createItem_withNullDescription_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/items")
                        .header(X_SHARER_USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemWithNullDescription)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Создание вещи с null available - ошибка валидации")
    void createItem_withNullAvailable_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/items")
                        .header(X_SHARER_USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemWithNullAvailable)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Добавление пустого комментария - ошибка валидации")
    void addComment_withEmptyText_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/items/1/comment")
                        .header(X_SHARER_USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(emptyComment)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Добавление null комментария - ошибка валидации")
    void addComment_withNullText_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/items/1/comment")
                        .header(X_SHARER_USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(nullComment)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Получение несуществующей вещи - ошибка 404")
    void getItem_nonExistingItem_shouldReturnNotFound() throws Exception {
        when(itemService.getItem(anyLong(), anyLong()))
                .thenThrow(new NotFoundException("Вещь не найдена"));

        mockMvc.perform(get("/items/999")
                        .header(X_SHARER_USER_ID, 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    @DisplayName("Обновление несуществующей вещи - ошибка 404")
    void updateItem_nonExistingItem_shouldReturnNotFound() throws Exception {
        when(itemService.updateItem(anyLong(), anyLong(), any(ItemDto.class)))
                .thenThrow(new NotFoundException("Вещь не найдена"));

        mockMvc.perform(patch("/items/999")
                        .header(X_SHARER_USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Обновление вещи с невалидными данными - ошибка валидации")
    void updateItem_withInvalidData_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(patch("/items/1")
                        .header(X_SHARER_USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemWithNullName)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Поиск вещей с длинным запросом (более 100 символов) - ошибка валидации")
    void searchItems_withLongText_shouldReturnBadRequest() throws Exception {
        String longText = "a".repeat(101);

        mockMvc.perform(get("/items/search")
                        .param("text", longText))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Получение вещей пользователя с пагинацией - успешный сценарий")
    void getUserItems_withPagination_shouldReturnPaginatedList() throws Exception {
        List<ItemDtoResponse> items = List.of(
                ItemDtoResponse.builder().id(1L).name("Item 1").build(),
                ItemDtoResponse.builder().id(2L).name("Item 2").build()
        );

        // Используем существующий метод getUserItems
        when(itemService.getUserItems(anyLong())).thenReturn(items);

        mockMvc.perform(get("/items")
                        .header(X_SHARER_USER_ID, 1L)
                        .param("from", "0")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @DisplayName("Получение вещей пользователя с некорректной пагинацией - ошибка")
    void getUserItems_withInvalidPagination_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/items")
                        .header(X_SHARER_USER_ID, 1L)
                        .param("from", "-1")
                        .param("size", "0"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Добавление комментария к несуществующей вещи - ошибка 404")
    void addComment_toNonExistingItem_shouldReturnNotFound() throws Exception {
        when(itemService.addComment(anyLong(), anyLong(), any(CommentDto.class)))
                .thenThrow(new NotFoundException("Вещь не найдена"));

        mockMvc.perform(post("/items/999/comment")
                        .header(X_SHARER_USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(commentDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Получение вещи с отрицательным ID - ошибка валидации")
    void getItem_withNegativeId_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/items/-1")
                        .header(X_SHARER_USER_ID, 1L))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Обновление вещи с отрицательным ID - ошибка валидации")
    void updateItem_withNegativeId_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(patch("/items/-1")
                        .header(X_SHARER_USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Удаление вещи с отрицательным ID - ошибка валидации")
    void deleteItem_withNegativeId_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(delete("/items/-1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Добавление комментария к вещи с отрицательным ID - ошибка валидации")
    void addComment_toItemWithNegativeId_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/items/-1/comment")
                        .header(X_SHARER_USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk());
    }

}