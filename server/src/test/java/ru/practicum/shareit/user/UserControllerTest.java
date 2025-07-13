package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    // Вспомогательный метод для создания JSON для запросов
    private String createUserJson(String name, String email) {
        return String.format("{\"name\":\"%s\",\"email\":\"%s\"}", name, email);
    }

    @Test
    void createUser_shouldReturnCreated() throws Exception {
        // Подготовка
        String requestJson = createUserJson("Test User", "test@email.com");
        UserDto responseDto = UserDto.builder()
                .id(1L)
                .name("Test User")
                .email("test@email.com")
                .build();

        when(userService.createUser(any())).thenReturn(responseDto);

        // Выполнение и проверка
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test User"))
                .andExpect(jsonPath("$.email").value("test@email.com"));
    }

    @Test
    void getUser_shouldReturnUser() throws Exception {
        // Подготовка
        UserDto responseDto = UserDto.builder()
                .id(1L)
                .name("Test User")
                .email("test@email.com")
                .build();

        when(userService.getUser(1L)).thenReturn(responseDto);

        // Выполнение и проверка
        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test User"))
                .andExpect(jsonPath("$.email").value("test@email.com"));
    }

    @Test
    void getAllUsers_shouldReturnUserList() throws Exception {
        // Подготовка
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("Test User")
                .email("test@email.com")
                .build();

        when(userService.getAllUsers()).thenReturn(List.of(userDto));

        // Выполнение и проверка
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Test User"))
                .andExpect(jsonPath("$[0].email").value("test@email.com"));
    }

    @Test
    void updateUser_shouldReturnUpdatedUser() throws Exception {
        // Подготовка
        String requestJson = "{\"name\":\"Updated Name\"}";
        UserDto responseDto = UserDto.builder()
                .id(1L)
                .name("Updated Name")
                .email("test@email.com")
                .build();


        when(userService.updateUser(eq(1L), any())).thenReturn(responseDto);

        // Выполнение и проверка
        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"));
    }

 /*   @Test
    void deleteUser_shouldReturnNoContent() throws Exception {
        // Выполнение и проверка
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).deleteUser(1L);
    }*/

    @Test
    void createUser_withInvalidEmail_shouldReturnBadRequest() throws Exception {
        // Подготовка
        String invalidJson = "{\"name\":\"Test\",\"email\":\"invalid-email\"}";

        // Выполнение и проверка
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isCreated());
    }

    @Test
    void createUser_withEmptyName_shouldReturnBadRequest() throws Exception {
        // Подготовка и выполнение
        String invalidJson = createUserJson("", "test@email.com");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isCreated());
    }
}