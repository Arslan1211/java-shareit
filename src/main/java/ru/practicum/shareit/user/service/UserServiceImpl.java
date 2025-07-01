package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;

import static ru.practicum.shareit.util.Constants.USER_NOT_FOUND_ERR;
import static ru.practicum.shareit.util.Constants.USER_WITH_SAME_EMAIL_ERR;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public Collection<UserDto> findAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .toList();
    }

    @Override
    public UserDto findUserById(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format(USER_NOT_FOUND_ERR, userId)));

        return UserMapper.toUserDto(user);
    }

    @Override
    @Transactional
    public UserDto createUser(UserDto userDto) {
        if (isEmailUnique(userDto)) {
            return UserMapper.toUserDto(userRepository.save(UserMapper.toUser(userDto)));
        } else {
            throw new ValidationException(String.format(USER_WITH_SAME_EMAIL_ERR, userDto.getEmail()));
        }
    }

    @Override
    @Transactional
    public UserDto updateUserById(Long userId, UserDto userDto) {
        userDto.setId(userId);
        if (userDto.getEmail() != null && !isEmailUnique(userDto)) {
            throw new ValidationException(String.format(USER_WITH_SAME_EMAIL_ERR, userDto.getEmail()));
        }
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format(USER_NOT_FOUND_ERR, userId)));

        User newUser = UserMapper.toUser(userDto);
        newUser.setEmail(newUser.getEmail() != null ? newUser.getEmail() : user.getEmail());
        newUser.setName(newUser.getName() != null ? newUser.getName() : user.getName());
        return UserMapper.toUserDto(userRepository.save(newUser));
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        if (findUserById(userId) != null) {
            userRepository.deleteById(userId);
        } else {
            throw new NotFoundException(String.format(USER_NOT_FOUND_ERR, userId));
        }
    }

    private boolean isEmailUnique(UserDto user) {
        return findAllUsers().stream()
                .noneMatch(u -> u.getEmail().equals(user.getEmail()) && !u.getId().equals(user.getId()));

    }
}