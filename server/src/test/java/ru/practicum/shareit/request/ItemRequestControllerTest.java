package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemRequestService itemRequestService;

    private final ItemDto itemDto = ItemDto.builder()
            .id(1L)
            .name("Дрель")
            .description("Простая дрель")
            .available(true)
            .requestId(1L)
            .build();

    private final ItemRequestDto requestDto = ItemRequestDto.builder()
            .id(1L)
            .userId(1L)
            .description("Нужна дрель")
            .createdDate(LocalDateTime.now())
            .created(LocalDateTime.now())
            .items(List.of(itemDto))
            .build();

    @Test
    void createRequest_shouldReturnCreatedRequest() throws Exception {
        when(itemRequestService.createRequest(anyLong(), any()))
                .thenReturn(requestDto);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestDto.getId()))
                .andExpect(jsonPath("$.description").value(requestDto.getDescription()))
                .andExpect(jsonPath("$.userId").value(requestDto.getUserId()))
                .andExpect(jsonPath("$.items[0].name").value(itemDto.getName()));
    }

    @Test
    void getUserRequests_shouldReturnListOfRequestsWithItems() throws Exception {
        when(itemRequestService.getUserRequests(anyLong()))
                .thenReturn(List.of(requestDto));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(requestDto.getId()))
                .andExpect(jsonPath("$[0].items[0].id").value(itemDto.getId()))
                .andExpect(jsonPath("$[0].items[0].name").value(itemDto.getName()));
    }

    @Test
    void getAllRequests_shouldReturnPaginatedRequestsWithItems() throws Exception {
        when(itemRequestService.getAllRequests(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(requestDto));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(requestDto.getId()))
                .andExpect(jsonPath("$[0].items").isArray())
                .andExpect(jsonPath("$[0].items[0].requestId").value(itemDto.getRequestId()));
    }

    @Test
    void getRequestById_shouldReturnRequestWithItems() throws Exception {
        when(itemRequestService.getRequestById(anyLong(), anyLong()))
                .thenReturn(requestDto);

        mockMvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestDto.getId()))
                .andExpect(jsonPath("$.items[0].id").value(itemDto.getId()))
                .andExpect(jsonPath("$.items[0].available").value(itemDto.getAvailable()));
    }

 /*   @Test
    void createRequest_withEmptyDescription_shouldReturnBadRequest() throws Exception {
        ItemRequestDto emptyRequest = ItemRequestDto.builder()
                .description("")
                .build();

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(emptyRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(containsString("Описание не может быть пустым")));
    }*/

/*    @Test
    void createRequest_withoutDescription_shouldReturnBadRequest() throws Exception {
        ItemRequestDto noDescriptionRequest = ItemRequestDto.builder().build();

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(noDescriptionRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }*/

    @Test
    void createRequest_withoutUserIdHeader_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}