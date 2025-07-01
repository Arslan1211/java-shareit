package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface UserService {
    Collection<UserDto> findAllUsers();

    UserDto findUserById(Long id);

    UserDto createUser(UserDto userDto);

    UserDto updateUserById(Long id, UserDto userDto);

    void deleteUser(Long id);

}
