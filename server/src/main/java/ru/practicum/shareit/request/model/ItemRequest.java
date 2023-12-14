package ru.practicum.shareit.request.model;

import lombok.Data;

import ru.practicum.shareit.user.model.User;

import javax.persistence.*;

import java.time.LocalDateTime;


@Data
@Entity
@Table(
        name = "requests",
        schema = "public"
)
public class ItemRequest {

    @Id
//    @SequenceGenerator(
//            name = "request_sequence",
//            sequenceName = "request_sequence",
//            allocationSize = 1
//    )
//    @GeneratedValue(
//            strategy = SEQUENCE,
//            generator = "request_sequence"
//    )
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @Column(
            name = "created"
    )
    private LocalDateTime created = LocalDateTime.now();

    public ItemRequest() {

    }

    public ItemRequest(String description, User requestor) {
        this.description = description;
        this.requestor = requestor;
    }
}
