package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User createTestUser() {
        User user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@email.com");
        return user;
    }

    private UserDto createTestUserDto() {
        // Используем маппер для создания DTO
        return UserMapper.toUserDto(createTestUser());
    }

    @Test
    void createUser_shouldCreateAndReturnUserDto() {
        User user = createTestUser();
        UserDto inputDto = createTestUserDto();

        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto result = userService.createUser(inputDto);

        assertNotNull(result);
        assertEquals(inputDto.getName(), result.getName());
        assertEquals(inputDto.getEmail(), result.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void getUser_shouldReturnUserDtoWhenUserExists() {
        User user = createTestUser();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        UserDto result = userService.getUser(1L);

        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void getUser_shouldThrowExceptionWhenUserNotExists() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> userService.getUser(1L));

        assertEquals("User not found with id: 1", exception.getMessage());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void getAllUsers_shouldReturnListOfUserDtos() {
        User user = createTestUser();
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<UserDto> result = userService.getAllUsers();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(user.getId(), result.get(0).getId());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    @Transactional
    void updateUser_shouldUpdateFields() {
        User existingUser = createTestUser();
        UserDto updateDto = UserMapper.toUserDto(existingUser);
        updateDto.setName("Updated Name");

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(existingUser));
        // Не мокаем save, так как он не вызывается явно

        UserDto result = userService.updateUser(1L, updateDto);

        assertNotNull(result);
        assertEquals("Updated Name", result.getName());
        verify(userRepository, times(1)).findById(1L);
        // Убираем проверку save, так как он не вызывается явно
    }

    @Test
    void deleteUser_shouldCallRepositoryDelete() {
        doNothing().when(userRepository).deleteById(anyLong());

        userService.deleteUser(1L);

        verify(userRepository, times(1)).deleteById(1L);
    }
}