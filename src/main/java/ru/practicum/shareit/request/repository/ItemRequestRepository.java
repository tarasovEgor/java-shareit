package ru.practicum.shareit.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.validation.ItemRequestValidation;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    ItemRequest findFirstById(long id);

    List<ItemRequest> findAllByRequestor(User requestor);

}
