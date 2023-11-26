package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.impl.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ItemRequestServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    private ItemRequest itemRequest;
    private ItemRequestDto itemRequestDto;
    private Item item;
    private User itemOwner;
    private User requestor;

    @BeforeEach
    void setUp() {

        itemOwner = new User(
                "itemOwner",
                "itemOwner@mail.com"
        );

        requestor = new User(
                "requestor",
                "requestor@mail.com"
        );

        item = new Item(
                "item",
                "desc",
                true,
                itemOwner,
                null
        );

        itemRequest = new ItemRequest(
                "item description",
                requestor
        );

        itemRequestDto = new ItemRequestDto(
                1L,
                "item description",
                requestor,
                LocalDateTime.of(2023, 11, 23, 12, 23, 0),
                List.of(item)
        );

        itemRequest.setId(1L);
        requestor.setId(2L);
    }

    @Test
    void shouldSaveItemRequest() {
        // Given
        given(userRepository
                .findById(anyLong()))
                .willReturn(Optional.of(requestor));

        // When
        when(itemRepository
                .findAllByRequestId(anyLong()))
                .thenReturn(List.of(item));

        when(itemRequestRepository
                .save(itemRequest))
                .thenReturn(itemRequest);

        ItemRequestDto savedItemRequest = itemRequestService.saveItemRequest(itemRequest, 1L);

        // Then
        assertNotNull(savedItemRequest);
        assertThat(savedItemRequest.getId()).isEqualTo(itemRequestDto.getId());
        assertThat(savedItemRequest.getDescription()).isEqualTo(itemRequestDto.getDescription());
        assertThat(savedItemRequest.getItems()).isEqualTo(itemRequestDto.getItems());
        assertThat(savedItemRequest.getRequestor()).isEqualTo(itemRequestDto.getRequestor());

    }

    @Test
    void shouldGetAllItemRequests() {
        // Given
        Page<ItemRequest> page = new PageImpl<>(List.of(itemRequest));

        given(itemRequestRepository
                .findAll(PageRequest.of(0, 1)))
                .willReturn(page);

        // When
        when(itemRepository
                .findAllByRequestId(anyLong()))
                .thenReturn(List.of(item));

        List<ItemRequestDto> itemRequests = itemRequestService
                .getAllItemRequests(PageRequest.of(0, 1), 1L);

        // Then
        assertNotNull(itemRequests);
        assertThat(itemRequests.get(0).getId()).isEqualTo(itemRequestDto.getId());
        assertThat(itemRequests.get(0).getDescription()).isEqualTo(itemRequestDto.getDescription());
        assertThat(itemRequests.get(0).getItems().get(0)).isEqualTo(item);
        assertThat(itemRequests.get(0).getRequestor()).isEqualTo(itemRequestDto.getRequestor());

    }

    @Test
    void shouldGetItemRequestById() {
        // Given
        given(userRepository
                .findById(anyLong()))
                .willReturn(Optional.of(requestor));
        // When
        when(itemRequestRepository
                .findById(anyLong()))
                .thenReturn(Optional.of(itemRequest));

        when(itemRepository
                .findAllByRequestId(anyLong()))
                .thenReturn(List.of(item));

        ItemRequestDto returnedItemRequest = itemRequestService.getItemRequestById(1L, 1L);

        // Then
        assertNotNull(returnedItemRequest);
        assertThat(returnedItemRequest.getId()).isEqualTo(itemRequestDto.getId());
        assertThat(returnedItemRequest.getItems().get(0)).isEqualTo(item);
        assertThat(returnedItemRequest.getDescription()).isEqualTo(itemRequestDto.getDescription());
        assertThat(returnedItemRequest.getRequestor()).isEqualTo(itemRequestDto.getRequestor());

    }

    @Test
    void shouldGetAllItemRequestsByUser() {
        // Given
        given(userRepository
                .findById(anyLong()))
                .willReturn(Optional.of(requestor));

        // When
        when(itemRepository
                .findAllByRequestId(anyLong()))
                .thenReturn(List.of(item));

        when(itemRequestRepository
                .findAllByRequestor(requestor))
                .thenReturn(List.of(itemRequest));

        List<ItemRequestDto> itemRequests = itemRequestService.getAllItemRequestsByUser(1L);

        // Then
        assertNotNull(itemRequests);
        assertThat(itemRequests.get(0).getId()).isEqualTo(itemRequestDto.getId());
        assertThat(itemRequests.get(0).getDescription()).isEqualTo(itemRequestDto.getDescription());
        assertThat(itemRequests.get(0).getItems().get(0)).isEqualTo(item);
        assertThat(itemRequests.get(0).getRequestor()).isEqualTo(itemRequestDto.getRequestor());

    }

}
