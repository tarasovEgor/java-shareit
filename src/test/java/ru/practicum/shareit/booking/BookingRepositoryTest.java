package ru.practicum.shareit.booking;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.constant.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@DirtiesContext
public class BookingRepositoryTest {

    @Autowired
    private EntityManager em;

    @Autowired
    private BookingRepository bookingRepository;

//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private ItemRepository itemRepository;

    private User booker;
    private User owner;
    private Item item;
    private Booking booking;

    @BeforeEach
    public void setUp() {

        booker = new User(
                "booker",
                "booker@mail.com"
        );

        owner = new User(
                "owner",
                "user@mail.com"
        );

        item = new Item(
                "item",
                "test",
                false,
                owner,
                null
        );

        booking = new Booking(
                LocalDateTime.of(23, 10, 12, 23, 23, 00),
                LocalDateTime.of(23, 10, 16, 12, 43, 00),
                item,
                booker
        );

    }

    @AfterEach
    public void destruct() {
        em.clear();
    }

    @Test
    void shouldFindBookerByIdAndBooker() {

        em.persist(owner);
        em.persist(booker);
        em.persist(item);
        em.persist(booking);

        Booking foundBooking = bookingRepository.findBookingByIdAndBooker(booking.getId(), booker);

        assertThat(foundBooking.getStart()).isEqualTo(booking.getStart());
        assertThat(foundBooking.getEnd()).isEqualTo(booking.getEnd());
        assertThat(foundBooking.getItem()).isEqualTo(booking.getItem());
        assertThat(foundBooking.getBooker()).isEqualTo(booking.getBooker());

    }

    @Test
    void shouldFindBookingByIdAndItemOwner() {

        em.persist(booker);
        em.persist(owner);
        em.persist(item);
        em.persist(booking);

        Booking foundBooking = bookingRepository.findBookingByIdAndItemOwner(booking.getId(), owner);

        assertThat(foundBooking.getStart()).isEqualTo(booking.getStart());
        assertThat(foundBooking.getEnd()).isEqualTo(booking.getEnd());
        assertThat(foundBooking.getItem()).isEqualTo(booking.getItem());
        assertThat(foundBooking.getBooker()).isEqualTo(booking.getBooker());

    }

    @Test
    void shouldFindAllBookingsByBooker() {

        User otherBooker = new User(
                "other",
                "other@mail.com"
        );

        Booking otherBooking = new Booking(
                LocalDateTime.of(23, 10, 12, 23, 23, 00),
                LocalDateTime.of(23, 10, 16, 12, 43, 00),
                item,
                otherBooker
        );

        Booking additionalBooking = new Booking(
                LocalDateTime.of(23, 10, 12, 23, 23, 00),
                LocalDateTime.of(23, 10, 16, 12, 43, 00),
                item,
                booker
        );

        em.persist(otherBooker);
        em.persist(booker);
        em.persist(owner);
        em.persist(item);
        em.persist(booking);
        em.persist(additionalBooking);
        em.persist(otherBooking);

        List<Booking> bookings = bookingRepository.findAllBookingsByBooker(booker);

        assertThat(bookings.size()).isEqualTo(2);
        assertThat(bookings.contains(booking)).isTrue();
        assertThat(bookings.contains(additionalBooking)).isTrue();
        assertThat(!bookings.contains(otherBooking)).isTrue();

    }

    @Test
    void shouldFindAllBookingsByItemOwner() {

        Item additionalItem = new Item(
                "additionalItem",
                "desc",
                false,
                owner,
                null
        );

        Booking additionalBooking = new Booking(
                LocalDateTime.of(23, 10, 12, 23, 23, 00),
                LocalDateTime.of(23, 10, 16, 12, 43, 00),
                additionalItem,
                booker
        );

        em.persist(booker);
        em.persist(owner);
        em.persist(item);
        em.persist(additionalItem);
        em.persist(booking);
        em.persist(additionalBooking);

        List<Booking> bookings = bookingRepository.findAllBookingsByItemOwner(owner);

        assertThat(bookings.size()).isEqualTo(2);
        assertThat(bookings.contains(booking)).isTrue();
        assertThat(bookings.contains(additionalBooking)).isTrue();

    }

    @Test
    void shouldFindAllBookingsByBookerAndStatus() {

        Booking bookingWaiting = new Booking(
                LocalDateTime.of(23, 10, 12, 23, 23, 00),
                LocalDateTime.of(23, 10, 16, 12, 43, 00),
                item,
                booker
        );

        bookingWaiting.setStatus(BookingStatus.WAITING);
        booking.setStatus(BookingStatus.APPROVED);

        em.persist(booker);
        em.persist(owner);
        em.persist(item);
        em.persist(booking);
        em.persist(bookingWaiting);

        List<Booking> waitingBookings = bookingRepository
                .findAllBookingsByBookerAndStatus(booker, BookingStatus.WAITING);

        assertThat(waitingBookings.contains(bookingWaiting)).isTrue();
        assertThat(waitingBookings.contains(booking)).isFalse();

    }

    @Test
    void shouldFindAllBookingsByBookerAndStatusCurrent() {

        User anotherBooker = new User(
                "user",
                "another@mail.com"
        );

        Booking bookingCurrent1 = new Booking(
                LocalDateTime.of(23, 11, 12, 23, 23, 00),
                LocalDateTime.of(23, 12, 24, 12, 43, 00),
                item,
                anotherBooker
        );

        Booking bookingCurrent2 = new Booking(
                LocalDateTime.of(23, 11, 12, 23, 23, 00),
                LocalDateTime.of(23, 11, 30, 12, 43, 00),
                item,
                anotherBooker
        );

        booking.setStart(LocalDateTime.of(23, 11, 12, 23, 23, 00));
        booking.setEnd(LocalDateTime.of(23, 11, 5, 23, 23, 00));

        em.persist(anotherBooker);
        em.persist(booker);
        em.persist(owner);
        em.persist(item);
        em.persist(booking);
        em.persist(bookingCurrent1);
        em.persist(bookingCurrent2);

        List<Booking> currentBookings = bookingRepository
                .findAllBookingsByBookerAndStatusCurrent(anotherBooker, LocalDateTime.of(
                        23, 11, 21, 10, 16, 13
                        )
                );

        assertThat(currentBookings.size()).isEqualTo(2);
        assertThat(currentBookings.contains(bookingCurrent1)).isTrue();
        assertThat(currentBookings.contains(bookingCurrent2)).isTrue();
        assertThat(currentBookings.contains(booking)).isFalse();

    }

    @Test
    void shouldFindAllBookingsByBookerAndStatusPast() {

        User anotherBooker = new User(
                "user",
                "another@mail.com"
        );

        Booking bookingPast1 = new Booking(
                LocalDateTime.of(23, 10, 12, 23, 23, 00),
                LocalDateTime.of(23, 11, 2, 12, 43, 00),
                item,
                anotherBooker
        );

        Booking bookingPast2 = new Booking(
                LocalDateTime.of(23, 11, 3, 23, 23, 00),
                LocalDateTime.of(23, 11, 6, 12, 43, 00),
                item,
                anotherBooker
        );

        booking.setStart(LocalDateTime.of(23, 11, 12, 23, 23, 00));
        booking.setEnd(LocalDateTime.of(23, 12, 25, 23, 23, 00));

        em.persist(anotherBooker);
        em.persist(booker);
        em.persist(owner);
        em.persist(item);
        em.persist(booking);
        em.persist(bookingPast1);
        em.persist(bookingPast2);

        List<Booking> pastBookings = bookingRepository
                .findAllBookingsByBookerAndStatusPast(anotherBooker, LocalDateTime.of(
                        23, 12, 5, 23, 54, 00
                        )
                );

        assertThat(pastBookings.size()).isEqualTo(2);
        assertThat(pastBookings.contains(bookingPast1)).isTrue();
        assertThat(pastBookings.contains(bookingPast2)).isTrue();
        assertThat(pastBookings.contains(booking)).isFalse();

    }

    @Test
    void shouldFindAllBookingsByItemOwnerAndStatus() {

        Item anotherItem = new Item(
                "another",
                "desc",
                false,
                owner,
                null
        );

        Booking bookingApproved = new Booking(
                LocalDateTime.of(23, 10, 12, 23, 23, 00),
                LocalDateTime.of(23, 10, 16, 12, 43, 00),
                anotherItem,
                booker
        );

        booking.setStatus(BookingStatus.REJECTED);
        bookingApproved.setStatus(BookingStatus.APPROVED);

        em.persist(owner);
        em.persist(booker);
        em.persist(item);
        em.persist(anotherItem);
        em.persist(booking);
        em.persist(bookingApproved);

        List<Booking> approvedBookings = bookingRepository
                .findAllBookingsByItemOwnerAndStatus(owner, BookingStatus.APPROVED);

        assertThat(approvedBookings.contains(bookingApproved)).isTrue();
        assertThat(approvedBookings.contains(booking)).isFalse();

    }

    @Test
    void shouldFindAllBookingsByItemOwnerAndStatusCurrent() {

        Item anotherItem = new Item(
                "item",
                "desc",
                true,
                owner,
                null
        );

        Booking bookingCurrent1 = new Booking(
                LocalDateTime.of(23, 11, 12, 23, 23, 00),
                LocalDateTime.of(23, 12, 24, 12, 43, 00),
                anotherItem,
                booker
        );

        Booking bookingCurrent2 = new Booking(
                LocalDateTime.of(23, 11, 12, 23, 23, 00),
                LocalDateTime.of(23, 11, 30, 12, 43, 00),
                anotherItem,
                booker
        );

        booking.setStart(LocalDateTime.of(23, 11, 12, 23, 23, 00));
        booking.setEnd(LocalDateTime.of(23, 11, 5, 23, 23, 00));

        em.persist(booker);
        em.persist(owner);
        em.persist(item);
        em.persist(anotherItem);
        em.persist(booking);
        em.persist(bookingCurrent1);
        em.persist(bookingCurrent2);

        List<Booking> currentBookings = bookingRepository
                .findAllBookingsByItemOwnerAndStatusCurrent(owner, LocalDateTime.of(
                                23, 11, 21, 10, 16, 13
                        )
                );

        assertThat(currentBookings.size()).isEqualTo(2);
        assertThat(currentBookings.contains(bookingCurrent1)).isTrue();
        assertThat(currentBookings.contains(bookingCurrent2)).isTrue();
        assertThat(currentBookings.contains(booking)).isFalse();

    }

    @Test
    void shouldFindAllBookingsByItemOwnerAndStatusPast() {

        Item anotherItem = new Item(
                "item",
                "desc",
                false,
                owner,
                null
        );

        Booking bookingPast1 = new Booking(
                LocalDateTime.of(23, 10, 12, 23, 23, 00),
                LocalDateTime.of(23, 11, 2, 12, 43, 00),
                anotherItem,
                booker
        );

        Booking bookingPast2 = new Booking(
                LocalDateTime.of(23, 11, 3, 23, 23, 00),
                LocalDateTime.of(23, 11, 6, 12, 43, 00),
                anotherItem,
                booker
        );

        booking.setStart(LocalDateTime.of(23, 11, 12, 23, 23, 00));
        booking.setEnd(LocalDateTime.of(23, 12, 25, 23, 23, 00));

        em.persist(booker);
        em.persist(owner);
        em.persist(item);
        em.persist(anotherItem);
        em.persist(booking);
        em.persist(bookingPast1);
        em.persist(bookingPast2);

        List<Booking> pastBookings = bookingRepository
                .findAllBookingsByItemOwnerAndStatusPast(owner, LocalDateTime.of(
                                23, 12, 5, 23, 54, 00
                        )
                );

        assertThat(pastBookings.size()).isEqualTo(2);
        assertThat(pastBookings.contains(bookingPast1)).isTrue();
        assertThat(pastBookings.contains(bookingPast2)).isTrue();
        assertThat(pastBookings.contains(booking)).isFalse();

    }

    @Test
    void shouldFindFirstByItemOwnerAndStartIsBeforeNow() {

        Item anotherItem = new Item(
                "anotherItem",
                "anotherDesc",
                false,
                owner,
                null
        );

        Booking bookingPast = new Booking(
                LocalDateTime.of(23, 10, 12, 23, 23, 00),
                LocalDateTime.of(23, 11, 2, 12, 43, 00),
                anotherItem,
                booker
        );

        booking.setStart(LocalDateTime.of(23, 12, 25, 23, 23, 00));
        booking.setEnd(LocalDateTime.of(23, 12, 30, 23, 23, 00));

        em.persist(booker);
        em.persist(owner);
        em.persist(item);
        em.persist(anotherItem);
        em.persist(booking);
        em.persist(bookingPast);

        Booking lastBooking = bookingRepository
                .findFirstByItemOwnerAndStartIsBeforeOrderByStartDesc(owner, LocalDateTime.of(
                        23, 11, 25, 23, 10 ,00)
                );

        assertThat(lastBooking.getStart()).isEqualTo(bookingPast.getStart());
        assertThat(lastBooking.getEnd()).isEqualTo(bookingPast.getEnd());
        assertThat(lastBooking.getItem()).isEqualTo(anotherItem);
        assertThat(lastBooking.getBooker()).isEqualTo(booker);

    }

    @Test
    void shouldFindFirstByItemOwnerAndStartIsAfterNow() {

        Item anotherItem = new Item(
                "anotherItem",
                "anotherDesc",
                false,
                owner,
                null
        );

        Booking bookingUpcoming = new Booking(
                LocalDateTime.of(23, 12, 25, 23, 23, 00),
                LocalDateTime.of(23, 12, 30, 12, 43, 00),
                anotherItem,
                booker
        );

        booking.setStart(LocalDateTime.of(23, 11, 20, 23, 23, 00));
        booking.setEnd(LocalDateTime.of(23, 11, 22, 23, 23, 00));

        em.persist(booker);
        em.persist(owner);
        em.persist(item);
        em.persist(anotherItem);
        em.persist(booking);
        em.persist(bookingUpcoming);

        Booking nextBooking = bookingRepository
                .findFirstByItemOwnerAndStartIsAfterOrderByStartAsc(owner, LocalDateTime.of(
                        23, 11, 25, 15, 20, 00
                        )
                );

        assertThat(nextBooking.getStart()).isEqualTo(bookingUpcoming.getStart());
        assertThat(nextBooking.getEnd()).isEqualTo(bookingUpcoming.getEnd());
        assertThat(nextBooking.getItem()).isEqualTo(anotherItem);
        assertThat(nextBooking.getBooker()).isEqualTo(booker);

    }

    @Test
    void shouldFindFirstByItemOwnerAndStartIsAfterNowAndStatusWaiting() {

        Item anotherItem = new Item(
                "anotherItem",
                "anotherDesc",
                false,
                owner,
                null
        );

        Booking bookingUpcoming = new Booking(
                LocalDateTime.of(23, 12, 25, 23, 23, 00),
                LocalDateTime.of(23, 12, 30, 12, 43, 00),
                anotherItem,
                booker
        );

        bookingUpcoming.setStatus(BookingStatus.WAITING);

        booking.setStart(LocalDateTime.of(23, 11, 20, 23, 23, 00));
        booking.setEnd(LocalDateTime.of(23, 11, 22, 23, 23, 00));
        booking.setStatus(BookingStatus.REJECTED);

        em.persist(booker);
        em.persist(owner);
        em.persist(item);
        em.persist(anotherItem);
        em.persist(booking);
        em.persist(bookingUpcoming);

        Booking nextBooking = bookingRepository
                .findFirstByItemOwnerAndStartIsAfterAndStatusOrderByStartAsc(owner, LocalDateTime.of(
                                23, 11, 25, 15, 20, 00
                        ), BookingStatus.WAITING);

        assertThat(nextBooking.getStart()).isEqualTo(bookingUpcoming.getStart());
        assertThat(nextBooking.getEnd()).isEqualTo(bookingUpcoming.getEnd());
        assertThat(nextBooking.getItem()).isEqualTo(anotherItem);
        assertThat(nextBooking.getItem().getName()).isEqualTo(anotherItem.getName());
        assertThat(nextBooking.getItem().getDescription()).isEqualTo(anotherItem.getDescription());
        assertThat(nextBooking.getBooker()).isEqualTo(booker);
        assertThat(nextBooking.getStatus()).isEqualTo(BookingStatus.WAITING);

    }

    @Test
    void shouldFindFirstByItemOwnerAndStartIsBeforeNowAndStatusWaiting() {

        Item anotherItem = new Item(
                "anotherItem",
                "anotherDesc",
                false,
                owner,
                null
        );

        Booking bookingPast = new Booking(
                LocalDateTime.of(23, 10, 12, 23, 23, 00),
                LocalDateTime.of(23, 11, 2, 12, 43, 00),
                anotherItem,
                booker
        );

        bookingPast.setStatus(BookingStatus.WAITING);

        booking.setStart(LocalDateTime.of(23, 12, 25, 23, 23, 00));
        booking.setEnd(LocalDateTime.of(23, 12, 30, 23, 23, 00));

        em.persist(booker);
        em.persist(owner);
        em.persist(item);
        em.persist(anotherItem);
        em.persist(booking);
        em.persist(bookingPast);

        Booking lastBooking = bookingRepository
                .findFirstByItemOwnerAndStartIsBeforeAndStatusOrderByStartDesc(owner, LocalDateTime.of(
                        23, 11, 25, 23, 10 ,00),
                        BookingStatus.WAITING
                );

        assertThat(lastBooking.getStart()).isEqualTo(bookingPast.getStart());
        assertThat(lastBooking.getEnd()).isEqualTo(bookingPast.getEnd());
        assertThat(lastBooking.getItem()).isEqualTo(anotherItem);
        assertThat(lastBooking.getItem()).isEqualTo(anotherItem);
        assertThat(lastBooking.getItem().getName()).isEqualTo(anotherItem.getName());
        assertThat(lastBooking.getItem().getDescription()).isEqualTo(anotherItem.getDescription());
        assertThat(lastBooking.getBooker()).isEqualTo(booker);
        assertThat(lastBooking.getStatus()).isEqualTo(BookingStatus.WAITING);

    }

    @Test
    void shouldFindBookingsByItemIdAndBooker() {

        Item anotherItem = new Item(
                "anotherItem",
                "anotherDesc",
                false,
                owner,
                null
        );

        Booking anotherBooking = new Booking(
                LocalDateTime.of(23, 10, 12, 23, 23, 00),
                LocalDateTime.of(23, 11, 2, 12, 43, 00),
                anotherItem,
                booker
        );

        em.persist(booker);
        em.persist(owner);
        em.persist(item);
        em.persist(anotherItem);
        em.persist(booking);
        em.persist(anotherBooking);

        List<Booking> foundBookingsForItem = bookingRepository
                .findBookingsByItemAndOwnerOrderByStartDesc(item.getId(), booker, LocalDateTime.of(
                                23, 12, 25, 12, 23, 00
                        )
                );

        List<Booking> foundBookingsForAnotherItem = bookingRepository
                .findBookingsByItemAndOwnerOrderByStartDesc(anotherItem.getId(), booker, LocalDateTime.of(
                        23, 12, 25, 12, 23, 00
                    )
                );

        assertNotNull(foundBookingsForItem);
        assertNotNull(foundBookingsForAnotherItem);
        assertThat(foundBookingsForItem).isNotEqualTo(foundBookingsForAnotherItem);

    }


}
