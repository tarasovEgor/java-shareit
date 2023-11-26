package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@DirtiesContext
public class ItemRequestRepositoryTest {

    @Autowired
    private EntityManager em;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    private ItemRequest itemRequest;
    private User requestor;
    private User itemOwner;
    private Item item;


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
                "item request",
                requestor
        );
    }

    @Test
    void shouldFindAllByRequestor() {
        // Given
        em.persist(itemOwner);
        em.persist(requestor);
        em.persist(item);
        em.persist(itemRequest);

        // When
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequestor(requestor);

        // Then
        assertNotNull(itemRequests);
        assertThat(itemRequests.size()).isEqualTo(1);
        assertThat(itemRequests.get(0)).isEqualTo(itemRequest);
        assertThat(itemRequests.get(0).getDescription()).isEqualTo(itemRequest.getDescription());
        assertThat(itemRequests.get(0).getRequestor()).isEqualTo(requestor);

    }

}
