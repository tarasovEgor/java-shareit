package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import ru.practicum.shareit.booking.constant.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Modifying
    @Query("update Booking b " +
            " set b.status = ?1" +
            " where b.id = ?2 and b.booker = ?3")
    void updateBookingStatus(BookingStatus  status, long bookingId, User booker);

    @Query("select b from Booking b" +
            " where b.id = ?1 and b.booker = ?2")
    Booking findBookingByIdAndBooker(long bookingId, User booker);

    @Query("select b from Booking as b" +
            " join b.item as i" +
            " where b.id = ?1 and i.owner = ?2")
    Booking findBookingByIdAndItemOwner(long bookingId, User owner);

    @Query("select b from Booking as b" +
            " where b.booker = ?1" +
            " order by b.start DESC")
    List<Booking> findAllBookingsByBooker(User booker);

    @Query("select b from Booking as b" +
            " join b.item as i" +
            " where i.owner = ?1" +
            " order by b.start DESC")
    List<Booking> findAllBookingsByItemOwner(User owner);

    @Query("select b from Booking as b" +
            " where b.booker = ?1 and" +
            " b.status = ?2" +
            " order by b.start DESC")
    List<Booking> findAllBookingsByBookerAndStatus(User booker, BookingStatus status);

    @Query("select b from Booking as b" +
            " where b.booker = ?1 and" +
            " b.start <= ?2 and b.end >= ?2" +
            " order by b.start DESC")
    List<Booking> findAllBookingsByBookerAndStatusCurrent(User booker, LocalDateTime now);

    @Query("select b from Booking as b" +
            " where b.booker = ?1 and" +
            " b.start < ?2 and b.end <= ?2" +
            " order by b.start DESC")
    List<Booking> findAllBookingsByBookerAndStatusPast(User booker, LocalDateTime now);

    @Query("select b from Booking as b" +
            " join b.item as i" +
            " where i.owner = ?1 and" +
            " b.start <= ?2 and b.end >= ?2" +
            " order by b.start DESC")
    List<Booking> findAllBookingsByItemOwnerAndStatusCurrent(User owner, LocalDateTime now);

    @Query("select b from Booking as b" +
            " join b.item as i" +
            " where i.owner = ?1 and" +
            " b.start < ?2 and b.end <= ?2" +
            " order by b.start DESC")
    List<Booking> findAllBookingsByItemOwnerAndStatusPast(User owner, LocalDateTime now);

    @Query("select b from Booking as b" +
            " join b.item as i" +
            " where i.owner = ?1 and" +
            " b.status = ?2" +
            " order by b.start DESC")
    List<Booking> findAllBookingsByItemOwnerAndStatus(User owner, BookingStatus status);

    //GET ALL ITEMS
    Booking findFirstByItemOwnerAndStartIsBeforeOrderByStartDesc(User owner, LocalDateTime now);

    Booking findFirstByItemOwnerAndStartIsAfterOrderByStartAsc(User owner, LocalDateTime now);

    //GET ITEM BY ID
    Booking findFirstByItemOwnerAndStartIsAfterAndStatusOrderByStartAsc(User owner, LocalDateTime now, BookingStatus status);

    Booking findFirstByItemOwnerAndStartIsBeforeAndStatusOrderByStartDesc(User owner, LocalDateTime now, BookingStatus status);

    //SAVE COMMENT
    @Query("select b from Booking as b" +
            " join b.item as i" +
            " where i.id = ?1" +
            " and b.booker = ?2" +
            " and b.end <= ?3" +
            " order by b.start DESC")
    List<Booking> findBookingsByItemAndOwnerOrderByStartDesc(long itemId, User owner, LocalDateTime now);

    //PAGINATION METHODS
    //ITEM BOOKER
    Page<Booking> findAllByBookerAndStatusOrderByStartDesc(User booker, BookingStatus status, Pageable pageable);

    //b.start <= ?2 and b.end >= ?2
    Page<Booking> findAllByBookerAndStartLessThanEqualAndEndGreaterThanEqualOrderByStartDesc(User booker, LocalDateTime now1, LocalDateTime now2, Pageable pageable);

    //b.start < ?2 and b.end <= ?2
    Page<Booking> findAllByBookerAndStartLessThanAndEndLessThanEqualOrderByStartDesc(User booker, LocalDateTime now1, LocalDateTime now2, Pageable pageable);

    Page<Booking> findAllByBookerOrderByStartDesc(User booker, Pageable pageable);

    //ITEM OWNER
    Page<Booking> findAllByItemOwnerAndStatusOrderByStartDesc(User owner, BookingStatus status, Pageable pageable);

    Page<Booking> findAllByItemOwnerAndStartLessThanEqualAndEndGreaterThanEqualOrderByStartDesc(User owner, LocalDateTime now1, LocalDateTime now2, Pageable pageable);

    Page<Booking> findAllByItemOwnerAndStartLessThanAndEndLessThanEqualOrderByStartDesc(User owner, LocalDateTime now1, LocalDateTime now2, Pageable pageable);

    Page<Booking> findAllByItemOwnerOrderByStartDesc(User owner, Pageable pageable);
}
