package ru.practicum.shareit.request.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1"
})
class ItemRequestRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private ItemRequestRepository requestRepository;

    @Test
    void shouldSaveRequest() {
        // Создаем и сохраняем пользователя
        User user = new User();
        user.setName("Test User");
        user.setEmail("test@example.com");
        em.persist(user);

        // Создаем запрос
        ItemRequest request = new ItemRequest();
        request.setDescription("Need item");
        request.setRequestor(user);
        request.setCreated(LocalDateTime.now());

        // Сохраняем
        ItemRequest saved = requestRepository.save(request);

        // Проверяем
        assertNotNull(saved.getId());
        assertEquals("Need item", saved.getDescription());
        assertEquals(user.getId(), saved.getRequestor().getId());
    }
}