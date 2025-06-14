package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.User;

import java.util.Collection;

public interface UserStorage {
    Collection<User> findAllUsers();

    User findUserById(Long userId);

    User createUser(User user);

    User updateUserById(User user);

    void deleteUser(Long userId);
}
