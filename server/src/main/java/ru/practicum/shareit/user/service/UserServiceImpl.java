package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static ru.practicum.shareit.user.mapper.UserMapper.toUser;
import static ru.practicum.shareit.user.mapper.UserMapper.toUserDto;

@Service
@Slf4j
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    @Transactional
    public UserDto createUser(UserDto userDto) {
        log.info("Creating user: {}", userDto);
        User user = toUser(userDto);
        User savedUser = userRepository.save(user);
        log.info("Created user: {}", savedUser);
        return toUserDto(savedUser);
    }

    @Override
    public UserDto getUser(Long id) {
        log.info("Getting user with id: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));
        log.info("User found: {}", user);
        return toUserDto(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        log.info("Getting all users");
        List<User> users = new ArrayList<>(userRepository.findAll());
        log.info("Found {} users", users.size());
        return users.stream()
                .map(UserMapper::toUserDto)
                .toList();
    }

    @Override
    @Transactional
    public UserDto updateUser(Long id, UserDto userDto) {
        log.info("Updating user with id: {}", id);
        User updatingUser = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));

        if (Objects.nonNull(userDto.getEmail())) updatingUser.setEmail(userDto.getEmail());
        if (Objects.nonNull(userDto.getName())) updatingUser.setName(userDto.getName());

        log.info("User updated: {}", updatingUser);
        return toUserDto(updatingUser);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        log.info("Deleting user with id: {}", id);
        userRepository.deleteById(id);
        log.info("User deleted: {}", id);
    }
}