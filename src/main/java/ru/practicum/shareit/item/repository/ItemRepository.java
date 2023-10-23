package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    @Modifying
    @Query("update Item i" +
            " set i.name = ?1," +
            " i.description = ?2," +
            " i.available = ?3" +
            " where i.id = ?4 and i.owner = ?5")
    void updateItem(String name, String description, Boolean available, long itemId, User owner);

    @Modifying
    @Query("update Item i" +
            " set i.name = ?1" +
            " where i.id = ?2 and i.owner = ?3")
    void updateItemName(String name, long itemId, User owner);

    @Modifying
    @Query("update Item i" +
            " set i.description = ?1" +
            " where i.id = ?2 and i.owner = ?3")
    void updateItemDescription(String description, long itemId, User owner);

    @Modifying
    @Query("update Item i" +
            " set i.available = ?1" +
            " where i.id = ?2 and i.owner = ?3")
    void updateItemAvailable(Boolean available, long itemId, User owner);

    @Query("select i from Item i" +
            " where upper(i.name) like upper(concat('%', ?1, '%'))" +
            " or upper(i.description) like upper(concat('%', ?1, '%'))" +
            " and i.available = true")
    List<Item> search(String text);

    List<Item> findAllByOwnerOrderByIdAsc(User owner);


}
