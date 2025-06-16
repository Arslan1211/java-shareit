package ru.practicum.shareit.user;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class User {
    Long id;
    String name;
    String email;
}
