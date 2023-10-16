package ru.practicum.shareit.request.model;

import lombok.Data;

import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDate;

import static javax.persistence.GenerationType.SEQUENCE;

@Data
@Entity
@Table(
        name = "requests",
        schema = "public"
)
public class ItemRequest {

    @Id
    @SequenceGenerator(
            name = "request_sequence",
            sequenceName = "request_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = SEQUENCE,
            generator = "request_sequence"
    )
    @Column(
            name = "id",
            updatable = false
    )
    private Long id;

    @Column(
            name = "description",
            nullable = false
    )
    private String description;

    @ManyToOne
    @JoinColumn(
            name = "requestor_id"
    )
    private User requestor;
    private LocalDate created;

    public ItemRequest() {

    }

    public ItemRequest(String description, User requestor, LocalDate created) {
        this.description = description;
        this.requestor = requestor;
        this.created = created;
    }
}
