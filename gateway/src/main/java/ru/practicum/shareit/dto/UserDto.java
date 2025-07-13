package ru.practicum.shareit.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.groups.Default;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.validation.UpdateValidation;

@Data
@Builder
public class UserDto {

    @JsonInclude
    private Long id;

    @NotBlank(message = "Имя не должно быть пустым", groups = Default.class)
    private String name;

    @Email(message = "Email должен иметь формат адреса электронной почты",
            groups = {Default.class, UpdateValidation.class})
    @NotBlank(message = "Email не должен быть пустым")
    private String email;
}