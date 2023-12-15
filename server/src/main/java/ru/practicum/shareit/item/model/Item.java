package ru.practicum.shareit.item.model;

import lombok.Data;

import ru.practicum.shareit.user.model.User;

import javax.persistence.*;

import static javax.persistence.GenerationType.SEQUENCE;


@Data
@Entity
@Table(
        name = "items",
        schema = "public"
)
public class Item {

    @Id
    @SequenceGenerator(
            name = "item_sequence",
            sequenceName = "item_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = SEQUENCE,
            generator = "item_sequence"
    )
  //  @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(
            name = "id",
            updatable = false
    )
    private Long id;

    @Column(
            name = "name",
            nullable = false
    )
    private String name;

    @Column(
            name = "description",
            nullable = false
    )
    private String description;

    @Column(
            name = "is_available"
    )
    private Boolean available;

    @ManyToOne
    @JoinColumn(
            name = "owner_id"
    )
    private User owner;

    private Long requestId;

    public Item() {

    }

    public Item(String name,
                String description,
                Boolean available,
                User owner,
                Long requestId) {
        this.name = name;
        this.description = description;
        this.available = available;
        this.owner = owner;
        this.requestId = requestId;
    }

}
