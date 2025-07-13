package ru.practicum.shareit.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.groups.Default;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {
    @JsonInclude
    private Long id;

    @NotBlank(message = "Имя не должно быть пустым", groups = Default.class)
    private String name;

    @NotBlank(message = "Описание не должно быть пустым", groups = Default.class)
    private String description;

    @NotNull(message = "Значение 'Available' не должно быть null", groups = Default.class)
    private Boolean available;

    private Long requestId;
}
