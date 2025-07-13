package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    private User createTestUser() {
        User user = new User();
        user.setId(1L);
        user.setName("User");
        user.setEmail("user@email.com");
        return user;
    }

    private ItemRequestDto createTestRequestDto() {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(1L);
        dto.setDescription("Need a drill");
        dto.setCreated(LocalDateTime.now());
        return dto;
    }

    private ItemRequest createTestRequest(User user) {
        ItemRequest request = new ItemRequest();
        request.setId(1L);
        request.setDescription("Need a drill");
        request.setRequestor(user);
        request.setCreated(LocalDateTime.now());
        return request;
    }

    private Item createTestItem(User user, ItemRequest request) {
        Item item = new Item();
        item.setId(1L);
        item.setName("Drill");
        item.setDescription("Powerful drill");
        item.setAvailable(true);
        item.setOwner(user);
        item.setRequest(request);
        return item;
    }

    @Test
    void createRequest_shouldCreateNewRequest() {
        User user = createTestUser();
        ItemRequestDto requestDto = createTestRequestDto();
        ItemRequest request = createTestRequest(user);

        // Мокируем оба вызова к userRepository
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.save(any())).thenReturn(request);

        ItemRequestDto result = itemRequestService.createRequest(1L, requestDto);

        assertNotNull(result);
        assertEquals(requestDto.getDescription(), result.getDescription());
        verify(itemRequestRepository, times(1)).save(any());
        // Проверяем, что оба метода userRepository были вызваны
        verify(userRepository, times(1)).existsById(anyLong());
        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    void createRequest_withNonExistingUser_shouldThrowException() {
        // Мокируем existsById, который вызывается первым в checkUserExists
        when(userRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(NotFoundException.class, () -> itemRequestService.createRequest(1L, createTestRequestDto()));
        verify(itemRequestRepository, never()).save(any());
        // Проверяем, что findById не вызывался вообще
        verify(userRepository, never()).findById(anyLong());
    }

    @Test
    void getUserRequests_shouldReturnUserRequests() {
        User user = createTestUser();
        ItemRequest request = createTestRequest(user);

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRequestRepository.findByRequestorId(anyLong(), any(Sort.class)))
                .thenReturn(Collections.singletonList(request));

        List<ItemRequestDto> result = itemRequestService.getUserRequests(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(request.getId(), result.get(0).getId());
        verify(itemRequestRepository, times(1)).findByRequestorId(anyLong(), any(Sort.class));
    }

    @Test
    void getUserRequests_withNonExistingUser_shouldThrowException() {
        when(userRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(NotFoundException.class, () -> itemRequestService.getUserRequests(1L));
        verify(itemRequestRepository, never()).findByRequestorId(anyLong(), any(Sort.class));
    }

    @Test
    void getAllRequests_shouldReturnPaginatedRequests() {
        User user = createTestUser();
        ItemRequest request = createTestRequest(user);

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRequestRepository.findAll(any(PageRequest.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(request)));

        List<ItemRequestDto> result = itemRequestService.getAllRequests(1L, 0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(request.getId(), result.get(0).getId());
        verify(itemRequestRepository, times(1)).findAll(any(PageRequest.class));
    }

    @Test
    void getAllRequests_withNonExistingUser_shouldThrowException() {
        when(userRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(NotFoundException.class, () -> itemRequestService.getAllRequests(1L, 0, 10));
        verify(itemRequestRepository, never()).findAll(any(PageRequest.class));
    }

    @Test
    void getRequestById_shouldReturnRequestWithItems() {
        User user = createTestUser();
        ItemRequest request = createTestRequest(user);
        Item item = createTestItem(user, request);

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(request));
        when(itemRepository.findByRequestId(anyLong())).thenReturn(Collections.singletonList(item));

        ItemRequestDto result = itemRequestService.getRequestById(1L, 1L);

        assertNotNull(result);
        assertEquals(request.getId(), result.getId());
        assertEquals(1, result.getItems().size());
        assertEquals(item.getId(), result.getItems().get(0).getId());
        verify(itemRequestRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).findByRequestId(anyLong());
    }

    @Test
    void getRequestById_withNonExistingUser_shouldThrowException() {
        when(userRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(NotFoundException.class, () -> itemRequestService.getRequestById(1L, 1L));
        verify(itemRequestRepository, never()).findById(anyLong());
    }

    @Test
    void getRequestById_withNonExistingRequest_shouldThrowException() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.getRequestById(1L, 1L));
        verify(itemRepository, never()).findByRequestId(anyLong());
    }
}