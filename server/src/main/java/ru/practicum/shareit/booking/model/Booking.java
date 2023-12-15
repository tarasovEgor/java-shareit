package ru.practicum.shareit.booking.model;

import lombok.Data;

import ru.practicum.shareit.booking.constant.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;

import java.time.LocalDateTime;


@Data
@Entity
@Table(
        name = "bookings",
        schema = "public"
)
public class Booking {

    @Id
//    @SequenceGenerator(
//            name = "booking_sequence",
//            sequenceName = "booking_sequence",
//            allocationSize = 1
//    )
//    @GeneratedValue(
//            strategy = SEQUENCE,
//            generator = "booking_sequence"
//    )
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(
            name = "id",
            updatable = false
    )
    private Long id;

    @Column(
            name = "start_date",
            nullable = false
    )
    private LocalDateTime start;

    @Column(
            name = "end_date",
            nullable = false
    )
    private LocalDateTime end;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne
    @JoinColumn(name = "booker_id")
    private User booker;

    @Column(
            name = "status"
    )
    @Enumerated(EnumType.STRING)
    private BookingStatus status = BookingStatus.WAITING;

    public Booking() {

    }

    public Booking(LocalDateTime start,
                   LocalDateTime end,
                   Item item,
                   User booker) {
        this.start = start;
        this.end = end;
        this.item = item;
        this.booker = booker;
    }
}
