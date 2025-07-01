package ru.practicum.shareit.util;

public class Constants {
    public static final String X_SHARER_USER_ID = "X-Sharer-User-Id";
    public static final String USER_NOT_FOUND_ERR = "Пользователь с id %d не найден";
    public static final String USER_WITH_SAME_EMAIL_ERR = "Пользователь с email %s уже существует";
    public static final String BOOKING_NOT_FOUND_ERR = "Бронирование с id %d не найдено";
    public static final String ITEM_NOT_FOUND_ERR = "Вещь с id %d не найдена";
    public static final String NOT_OWNER_UPDATE_MESSAGE = "Только владелец может обновить статус бронирования товара";
    public static final String BOOKER_OR_OWNER_ACCESS_REQUIRED = "Только автор бронирования или владелец предмета может просматривать это бронирование";
    public static final String ITEM_NOT_AVAILABLE = "Предмет недоступен для бронирования";
    public static final String USER_HAS_NOT_BROOKED_ITEM = "Пользователь не забронировал этот товар";
}
