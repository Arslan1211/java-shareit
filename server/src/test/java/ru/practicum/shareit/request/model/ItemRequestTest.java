package ru.practicum.shareit.request.model;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ItemRequestTest {

    @Test
    void testNoArgsConstructor() {
        ItemRequest request = new ItemRequest();

        assertNull(request.getId());
        assertNull(request.getDescription());
        assertNull(request.getRequestor());
        assertNull(request.getCreated());
        assertNotNull(request.getItems());
        assertEquals(0, request.getItems().size());
    }

    @Test
    void testGettersAndSetters() {
        ItemRequest request = new ItemRequest();

        Long id = 1L;
        String description = "Need a drill";
        User requestor = new User();
        LocalDateTime created = LocalDateTime.now();
        List<Item> items = new ArrayList<>();

        request.setId(id);
        request.setDescription(description);
        request.setRequestor(requestor);
        request.setCreated(created);
        request.setItems(items);

        assertEquals(id, request.getId());
        assertEquals(description, request.getDescription());
        assertEquals(requestor, request.getRequestor());
        assertEquals(created, request.getCreated());
        assertEquals(items, request.getItems());
    }

    @Test
    void testToString() {
        ItemRequest request = new ItemRequest();
        request.setId(1L);
        request.setDescription("Test description");

        String toString = request.toString();

        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("description=Test description"));
    }

    @Test
    void testItemsInitialization() {
        ItemRequest request = new ItemRequest();

        assertNotNull(request.getItems());
        assertEquals(0, request.getItems().size());

        List<Item> items = new ArrayList<>();
        items.add(new Item());
        request.setItems(items);

        assertEquals(1, request.getItems().size());
    }

    @Test
    void testCreatedTimestamp() {
        ItemRequest request = new ItemRequest();
        assertNull(request.getCreated());

        // В реальном приложении created устанавливается автоматически благодаря @CreationTimestamp
        LocalDateTime now = LocalDateTime.now();
        request.setCreated(now);
        assertEquals(now, request.getCreated());
    }

    @Test
    void testEquals() {
        ItemRequest request1 = new ItemRequest();
        request1.setId(1L);

        ItemRequest request2 = new ItemRequest();
        request2.setId(1L);

        ItemRequest request3 = new ItemRequest();
        request3.setId(2L);

        // Рефлексивность
        assertEquals(request1, request1);

        // Симметричность
        assertEquals(request1, request2);
        assertEquals(request2, request1);

        // Неравенство
        assertNotEquals(request1, request3);
        assertNotEquals(request3, request1);

        // Сравнение с null
        assertNotEquals(null, request1);

        // Сравнение с объектом другого класса
        assertNotEquals(request1, new Object());
    }

    @Test
    void testHashCode() {
        ItemRequest request1 = new ItemRequest();
        request1.setId(1L);

        ItemRequest request2 = new ItemRequest();
        request2.setId(1L);

        // Все объекты ItemRequest имеют одинаковый hashCode
        assertEquals(request1.hashCode(), request2.hashCode());

        // Проверка консистентности
        int initialHashCode = request1.hashCode();
        assertEquals(initialHashCode, request1.hashCode());
        assertEquals(initialHashCode, request1.hashCode());
    }

    @Test
    void testEqualsWithNullId() {
        ItemRequest request1 = new ItemRequest();
        ItemRequest request2 = new ItemRequest();

        assertNotEquals(request1, request2);
    }
}