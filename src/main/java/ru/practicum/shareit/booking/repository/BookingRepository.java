package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.constant.BookingState;
import ru.practicum.shareit.booking.constant.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
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

    Booking findFirstByItemOwnerAndStartIsBeforeOrderByStartDesc(User owner, LocalDateTime now);

    Booking findFirstByItemOwnerAndStartIsAfterOrderByStartAsc(User owner, LocalDateTime now);


    ///////////////////////////

    Booking findFirstByItemNameAndItemOwnerAndStartIsBeforeOrderByStartDesc(String name, User owner, LocalDateTime now);



    @Query("select b from Booking as b" +
            " join b.item as i" +
            " where i.name = ?1" +
            " and i.owner = ?2" +
            " and b.start < ?3" +
            " order by b.start DESC")
    Booking findBookingByItemNameAndOwner(String name, User owner, LocalDateTime now);
    //Booking findFirstByItemOwnerItemNameAndStartIsBeforeOrderByStartDesc(User owner, String name, LocalDateTime now);

    //Booking findFirstByItemOwnerAndEndIsBeforeOrEndIsEqualOrderByStartDesc(User owner, LocalDateTime now);

    @Query("select b from Booking as b" +
            " join b.item as i" +
            " where i.id = ?1" +
            " and b.booker = ?2" +
            " and b.end <= ?3" +
            " order by b.start DESC")
    List<Booking> findBookingsByItemAndOwnerOrderByStartDesc(long itemId, User owner, LocalDateTime now);

    /////
    //TRIAL
    //////|
    //    |
    //    |
    //    v

//    @Query("select b from Booking as b" +
//            " join b.item as i" +
//            " where i.id = ?1" +
//            " and b.start < ?2" +
//            " order by b.start DESC")
//    Booking findFirstByItemIdOwnerAndStartIsBeforeOrderByStartDesc(long itemId, LocalDateTime now);
//
//    @Query("select b from Booking as b" +
//            " join b.item as i" +
//            " where i.id = ?1" +
//            " and b.start > ?2" +
//            " order by b.start ASC")
//    Booking findFirstByItemIdOwnerAndStartIsAfterOrderByStartAsc(long itemId, LocalDateTime now);

//    @Query("select b from Booking as b" +
//            " join b.item as i" +
//            " where i.id = ?1 and b.start > now()")
//    Booking findAllBookingsByItemIdAndStartDateInFuture(long itemId);
//
//    @Query("select b from Booking as b" +
//            " join b.item as i" +
//            " where i.id = ?1 and b.start < now()")
//    Booking findAllBookingsByItemAndStartDateInPast(long itemId);
}
