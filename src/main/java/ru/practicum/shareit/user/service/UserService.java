package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface UserService {
    Collection<UserDto> findAllUsers();

    UserDto findUserById(Long userId);

    UserDto createUser(UserDto user);

    UserDto updateUserById(UserDto userDto, Long userId);

    void deleteUser(Long userId);

}
