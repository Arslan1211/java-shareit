package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @GetMapping
    public Collection<UserDto> findAllUsers() {
        return userService.findAllUsers();
    }

    @GetMapping("/{userId}")
    public UserDto findUserById(@PathVariable("userId") Long userId) {
        return userService.findUserById(userId);
    }

    @PostMapping
    public UserDto createUser(@RequestBody @Valid UserDto user) {
        return userService.createUser(user);
    }

    @PatchMapping("/{userId}")
    UserDto updateUserById(@RequestBody UserDto userDto, @PathVariable("userId") Long userId) {
        return userService.updateUserById(userDto, userId);
    }

    @DeleteMapping("/{userId}")
    void deleteUser(@PathVariable("userId") Long userId) {
        userService.deleteUser(userId);
    }
}
