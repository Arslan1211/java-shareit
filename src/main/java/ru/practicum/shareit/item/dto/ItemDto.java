package ru.practicum.shareit.item.dto;

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

    @NotBlank(message = "Имя не может быть пустым", groups = Default.class)
    private String name;

    @NotBlank(message = "Описание не может быть пустым", groups = Default.class)
    private String description;

    @NotNull(message = "Поле available не может быть null", groups = Default.class)
    private Boolean available;

    private Long requestId;
}
