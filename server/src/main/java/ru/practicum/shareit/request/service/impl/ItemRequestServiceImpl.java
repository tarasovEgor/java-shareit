package ru.practicum.shareit.request.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import ru.practicum.shareit.exception.ItemRequestDoesNotExistException;
import ru.practicum.shareit.exception.UserDoesNotExistException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Autowired
    public ItemRequestServiceImpl(ItemRequestRepository itemRequestRepository,
                                  ItemRepository itemRepository,
                                  UserRepository userRepository) {
        this.itemRequestRepository = itemRequestRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ItemRequestDto saveItemRequest(ItemRequest itemRequest, long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserDoesNotExistException("User doesn't exist."));
        itemRequest.setRequestor(user);
        return ItemRequestMapper.toItemRequestDto(itemRequestRepository.save(itemRequest), user, itemRepository);
    }

    @Override
    public List<ItemRequestDto> getAllItemRequestsByUser(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserDoesNotExistException("User doesn't exist."));
        return ItemRequestMapper.toItemRequestDto(itemRequestRepository.findAllByRequestor(user), itemRepository);
    }

    @Override
    public List<ItemRequestDto> getAllItemRequests(PageRequest pageRequest, long userId) {
        Page<ItemRequest> page = itemRequestRepository.findAll(pageRequest);
        return ItemRequestMapper.toItemRequestDto(page.getContent(), itemRepository).stream()
                .filter(request -> request.getRequestor().getId() != userId)
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getItemRequestById(long requestId, long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserDoesNotExistException("User doesn't exist."));
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new ItemRequestDoesNotExistException("Item request does not exist."));
        return ItemRequestMapper.toItemRequestDto(
                itemRequest,
                itemRequest.getRequestor(),
                itemRepository
        );
    }


}
