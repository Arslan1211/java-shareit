package ru.practicum.shareit.request;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@Data
@Builder
public class ItemRequest {
    Long id;
    String description;
    User requestor;
    LocalDateTime created;
}
