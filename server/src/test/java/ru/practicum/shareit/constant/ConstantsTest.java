package ru.practicum.shareit.constant;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.practicum.shareit.constant.Constants.*;

class ConstantsTest {
    @Test
    void constants_shouldHaveCorrectValues() {
        // Проверка заголовка
        assertEquals("X-Sharer-User-Id", X_SHARER_USER_ID);

        // Проверка сообщений об ошибках
        assertEquals("Пользователь с id %d не найден", USER_NOT_FOUND_ERR);
        assertEquals("Пользователь с email %s уже существует", USER_WITH_SAME_EMAIL_ERR);
        assertEquals("Бронирование с id %d не найдено", BOOKING_NOT_FOUND_ERR);
        assertEquals("Вещь с id %d не найдена", ITEM_NOT_FOUND_ERR);
        assertEquals("Товар недоступен для бронирования", ITEM_NOT_AVAILABLE);
        assertEquals("Только владелец может обновить статус бронирования товара", ONLY_OWNER_CAN_UPDATE_BOOKING_STATUS);
        assertEquals("Просмотр бронирования доступен только автору брони или владельцу вещи", BOOKING_VIEW_PERMISSION_RESTRICTED);
        assertEquals("Неизвестное состояние %s. Поддерживаемые значения: %s ", UNKNOWN_STATE);
    }

    @Test
    void errorMessages_shouldBeFormattable() {
        // Проверка форматирования сообщений с параметрами
        assertEquals("Пользователь с id 123 не найден", String.format(USER_NOT_FOUND_ERR, 123));
        assertEquals("Пользователь с email test@example.com уже существует",
                String.format(USER_WITH_SAME_EMAIL_ERR, "test@example.com"));
        assertEquals("Бронирование с id 456 не найдено", String.format(BOOKING_NOT_FOUND_ERR, 456));
        assertEquals("Вещь с id 789 не найдена", String.format(ITEM_NOT_FOUND_ERR, 789));
        assertEquals("Неизвестное состояние STATE. Поддерживаемые значения: VALUES ",
                String.format(UNKNOWN_STATE, "STATE", "VALUES"));
    }

    @Test
    void constants_shouldBePublicStaticFinal() throws NoSuchFieldException, IllegalAccessException {
        // Проверка модификаторов всех полей
        for (var field : Constants.class.getDeclaredFields()) {
            assertTrue(java.lang.reflect.Modifier.isPublic(field.getModifiers()));
            assertTrue(java.lang.reflect.Modifier.isStatic(field.getModifiers()));
            assertTrue(java.lang.reflect.Modifier.isFinal(field.getModifiers()));
        }
    }

    @Test
    void constants_shouldBeStrings() {
        // Проверка, что все поля являются строками
        for (var field : Constants.class.getDeclaredFields()) {
            assertEquals(String.class, field.getType());
        }
    }
}