package ru.practicum.shareit.item;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@DataJpaTest
@DirtiesContext
public class ItemRepositoryTest {

    @Autowired
    private EntityManager em;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    private Item item;

    private User owner;

    @BeforeEach
    public void setUp() {
        owner = new User(
            "owner",
            "user@mail.com"
        );

        em.persist(owner);

        item = new Item(
                "item",
                "test",
                false,
                owner,
                null
        );
    }

    @AfterEach
    public void destruct() {
        itemRepository.deleteAll();
        em.clear();
    }

    @Test
    void shouldSaveItem() {
        // Given
        em.persist(owner);

        // When
        Item newItem = itemRepository.save(item);
        List<Item> items = itemRepository.findAll();

        // Then
        assertThat(newItem).hasFieldOrPropertyWithValue("name", "item");
        assertThat(newItem).hasFieldOrPropertyWithValue("description", "test");
        assertThat(newItem).hasFieldOrPropertyWithValue("available", false);
        assertThat(newItem).hasFieldOrPropertyWithValue("owner", owner);
        assertThat(newItem).hasFieldOrPropertyWithValue("requestId", null);
        assertThat(items.size()).isEqualTo(1);
    }

    @Test
    void shouldGetItemById() {
        // Given
        Item item1 = new Item("item1", "test", false, owner, null);
        em.persist(item1);

        Item item2 = new Item("item2", "test", true, owner, null);
        em.persist(item2);

        // When
        Item foundItem = itemRepository.findById(item2.getId()).get();

        // Then
        assertThat(foundItem).isEqualTo(item2);
    }

    @Test
    void shouldFindAllItems() {
        // Given
        Item item1 = new Item("item1", "test", false, owner, null);
        em.persist(item1);

        Item item2 = new Item("item2", "test", false, owner, null);
        em.persist(item2);

        Item item3 = new Item("item3", "test", false, owner, null);
        em.persist(item3);

        // When
        List<Item> items = itemRepository.findAll();

        // Then
        assertEquals(items.size(), 3);
        assertThat(items.get(0)).isEqualTo(item1);
        assertThat(items.get(1)).isEqualTo(item2);
        assertThat(items.get(2)).isEqualTo(item3);
    }

    @Test
    void shouldUpdateItemNameEmailAvailable() {
        // Given
        Item updatedItem = new Item(
                "updated",
                "updated",
                true,
                owner,
                null
        );

        em.persist(owner);
        em.persist(item);

        // When
        item.setName("updated");
        item.setDescription("updated");
        item.setAvailable(true);

        em.persist(item);

        Optional<Item> newItem = itemRepository.findById(item.getId());

        // Then
        assertThat(newItem.get().getName()).isEqualTo(updatedItem.getName());
        assertThat(newItem.get().getDescription()).isEqualTo(updatedItem.getDescription());
        assertThat(newItem.get().getAvailable()).isEqualTo(updatedItem.getAvailable());
    }

    @Test
    void shouldDeleteItemById() {
        // Given
        Item item1 = new Item("item1", "test", false, owner, null);
        em.persist(item1);

        Item item2 = new Item("item2", "test", true, owner, null);
        em.persist(item2);

        Item item3 = new Item("item3", "test", true, owner, null);
        em.persist(item3);

        // When
        itemRepository.deleteById(item2.getId());

        Iterable<Item> items = itemRepository.findAll();

        // Then
        assertThat(items).asList().hasSize(2).contains(item1, item3);
    }

    @Test
    void shouldDeleteAllItems() {
        // Given
        Item item1 = new Item("item1", "test", false, owner, null);
        em.persist(item1);

        Item item2 = new Item("item2", "test", true, owner, null);
        em.persist(item2);

        // When
        itemRepository.deleteAll();

        // Then
        assertThat(itemRepository.findAll()).asList().isEmpty();
    }

    @Test
    void shouldSearchItem() {
        // Given
        Item searchedItem1 = new Item(
                "search",
                "searching",
                true,
                owner,
                null
        );

        Item searchedItem2 = new Item(
                "sears",
                "garden sears",
                false,
                owner,
                null
        );

        Item searchedItem3 = new Item(
                "glass",
                "a kitchen glass",
                false,
                owner,
                null
        );

        em.persist(searchedItem1);
        em.persist(searchedItem2);
        em.persist(searchedItem3);

        // When
        TypedQuery<Item> query = em.createQuery(
                "select i from Item i" +
                        " where upper(i.name) like upper(concat('%', :text, '%'))" +
                        " or upper(i.description) like upper(concat('%', :text, '%'))" +
                        " and i.available = true",
                Item.class
        );

        List<Item> foundItems = query.setParameter("text", "sear").getResultList();

        // Then
        assertEquals(foundItems.size(), 2);
        assertThat(foundItems.get(0).getName().contains("sear"));
        assertThat(foundItems.get(1).getName().contains("sear"));
        assertThat(foundItems.get(0).getDescription().contains("sear"));
        assertThat(foundItems.get(1).getDescription().contains("sear"));
        assertThat(foundItems.contains(searchedItem1));
        assertThat(foundItems.contains(searchedItem2));
        assertThat(!foundItems.contains(searchedItem3));
    }

    @Test
    void shouldFindAllItemsByOwner() {
        // Given
        User owner1 = new User("owner1", "owner1@mail.com");
        User owner2 = new User("owner2", "owner2@mail.com");

        em.persist(owner1);
        em.persist(owner2);

        Item item1 = new Item(
                "item1",
                "test",
                true,
                owner1,
                null
        );

        Item item2 = new Item(
                "item1",
                "test",
                true,
                owner1,
                null
        );

        Item item3 = new Item(
                "item1",
                "test",
                true,
                owner2,
                null
        );

        em.persist(item1);
        em.persist(item2);
        em.persist(item3);

        // When
        List<Item> owner1Items = itemRepository.findAllByOwnerOrderByIdAsc(owner1);

        // Then
        assertEquals(owner1Items.size(), 2);
        assertThat(owner1Items.contains(item1));
        assertThat(owner1Items.contains(item2));
        assertThat(!owner1Items.contains(item3));
    }


}
