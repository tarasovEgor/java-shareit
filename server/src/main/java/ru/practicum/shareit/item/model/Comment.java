package ru.practicum.shareit.item.model;

import lombok.Data;

import javax.persistence.*;

import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static javax.persistence.GenerationType.SEQUENCE;

@Data
@Entity
@Table(
        name = "comments",
        schema = "public"
)
public class Comment {

    @Id
    @SequenceGenerator(
            name = "comment_sequence",
            sequenceName = "comment_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = SEQUENCE,
            generator = "comment_sequence"
    )
    @Column(
            name = "id",
            updatable = false
    )
    private Long id;

    @Column(
            name = "text",
            nullable = false
    )
    private String text;

    @ManyToOne
    @JoinColumn(
            name = "item_id"
    )
    private Item item;

    @ManyToOne
    @JoinColumn(
            name = "author_id"
    )
    private User author;

    @Column(
            name = "created"
    )
    private LocalDateTime created;

    public Comment() {

    }

    public Comment(Long id, String text, Item item, User author, LocalDateTime created) {
        this.id = id;
        this.text = text;
        this.item = item;
        this.author = author;
        this.created = created;
    }
}
