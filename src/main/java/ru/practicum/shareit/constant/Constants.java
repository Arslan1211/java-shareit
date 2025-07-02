package ru.practicum.shareit.constant;

public class Constants {
    public static final String X_SHARER_USER_ID = "X-Sharer-User-Id";
    public static final String USER_NOT_FOUND_ERR = "Пользователь с id %d не найден";
    public static final String USER_WITH_SAME_EMAIL_ERR = "Пользователь с email %s уже существует";
    public static final String BOOKING_NOT_FOUND_ERR = "Бронирование с id %d не найдено";
    public static final String ITEM_NOT_FOUND_ERR = "Вещь с id %d не найдена";
    public static final String ITEM_NOT_AVAILABLE = "Товар недоступен для бронирования";
    public static final String ONLY_OWNER_CAN_UPDATE_BOOKING_STATUS  = "Только владелец может обновить статус бронирования товара";
    public static final String BOOKING_VIEW_PERMISSION_RESTRICTED = "Просмотр бронирования доступен только автору брони или владельцу вещи";
}
