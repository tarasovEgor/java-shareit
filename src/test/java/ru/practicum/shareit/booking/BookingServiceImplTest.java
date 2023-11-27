package ru.practicum.shareit.booking;

import org.checkerframework.checker.nullness.Opt;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.constant.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.impl.BookingServiceImpl;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class BookingServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private User owner;
    private User booker;
    private Item item;
    private Booking booking;

    @BeforeEach
    public void setUp() {

        owner = new User(
                "owner",
                "owner@mail.com"
        );

        booker = new User(
                "booker",
                "booker@mail.com"
        );

        item = new Item(
                "item",
                "desc",
                true,
                owner,
                null
        );

        booking = new Booking(
                LocalDateTime.of(2023, 11, 30, 12, 45, 00),
                LocalDateTime.of(2023, 12, 15, 17, 45, 00),
                item,
                booker
        );

        item.setId(1L);
        owner.setId(1L);
        booker.setId(2L);

    }

    @Test
    void shouldSaveBooking() {
        // Given
        BookingDto bookingDto = BookingMapper.toBookingDto(booking);

        // When
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));

        when(bookingRepository.save(booking)).thenReturn(booking);

        BookingDto savedBooking = bookingService.saveBooking(bookingDto, bookingDto.getBooker().getId());

        // Then
        assertNotNull(savedBooking);
        assertThat(savedBooking.getStart()).isEqualTo(booking.getStart());
        assertThat(savedBooking.getEnd()).isEqualTo(booking.getEnd());
        assertThat(savedBooking.getItem()).isEqualTo(item);
        assertThat(savedBooking.getStatus()).isEqualTo(booking.getStatus());
        assertThat(savedBooking.getBooker()).isEqualTo(booker);

    }

    @Test
    void shouldNotSaveBookingWhenItemDoesNotExist() {
        // Given
        BookingDto bookingDto = BookingMapper.toBookingDto(booking);

        // Then
        assertThrows(ItemDoesNotExistException.class,
                () -> bookingService.saveBooking(bookingDto, 112L));
    }

    @Test
    void shouldNotSaveBookingWhenItemOwnerIdEqualsToItemBookerId() {
        // Given
        Booking booking = new Booking(
                LocalDateTime.of(2023, 11, 12, 12, 0),
                LocalDateTime.of(2023, 11, 15, 12, 0),
                item,
                owner
        );

        BookingDto bookingDto = BookingMapper.toBookingDto(booking);

        given(itemRepository
                .findById(anyLong()))
                .willReturn(Optional.of(item));

        // Then
        assertThrows(InvalidItemOwnerException.class,
                () -> bookingService.saveBooking(bookingDto, 1));
    }

    @Test
    void shouldNotSaveBookingWhenItemIsUnavailable() {
        // Given
        item.setAvailable(false);

        BookingDto bookingDto = BookingMapper.toBookingDto(booking);

        given(itemRepository
                .findById(anyLong()))
                .willReturn(Optional.of(item));

        // Then
        assertThrows(ItemIsUnavailableException.class,
                () -> bookingService.saveBooking(bookingDto, 2L));
    }

    @Test
    void shouldNotSaveBookingWhenBookingStartIsNull() {
        // Given
        Booking booking = new Booking(
                null,
                LocalDateTime.of(2023, 11, 23, 12, 12),
                item,
                booker
        );

        BookingDto bookingDto = BookingMapper.toBookingDto(booking);

        given(itemRepository
                .findById(anyLong()))
                .willReturn(Optional.of(item));

        // Then
        assertThrows(InvalidBookingDateException.class,
                () -> bookingService.saveBooking(bookingDto, 2L));
    }

    @Test
    void shouldNotSaveBookingWhenBookingEndIsNull() {
        // Given
        Booking booking = new Booking(
                LocalDateTime.of(2023, 11, 12, 12, 00),
                null,
                item,
                booker
        );

        BookingDto bookingDto = BookingMapper.toBookingDto(booking);

        given(itemRepository
                .findById(anyLong()))
                .willReturn(Optional.of(item));

        // Then
        assertThrows(InvalidBookingDateException.class,
                () -> bookingService.saveBooking(bookingDto, 2));
    }

    @Test
    void shouldNotSaveBookingWhenEndIsBeforeStart() {
        // Given
        Booking booking = new Booking(
                LocalDateTime.of(2023, 11, 12, 12, 10),
                LocalDateTime.of(2023, 10, 12, 12, 10),
                item,
                booker
        );

        BookingDto bookingDto = BookingMapper.toBookingDto(booking);

        given(itemRepository
                .findById(anyLong()))
                .willReturn(Optional.of(item));

        // Then
        assertThrows(InvalidBookingDateException.class,
                () -> bookingService.saveBooking(bookingDto, 2L));
    }

    @Test
    void shouldNotSaveBookingWhenStartEqualsEnd() {
        // Given
        Booking booking = new Booking(
                LocalDateTime.of(2023, 10, 12, 12, 10),
                LocalDateTime.of(2023, 10, 12, 12, 10),
                item,
                booker
        );

        BookingDto bookingDto = BookingMapper.toBookingDto(booking);

        given(itemRepository
                .findById(anyLong()))
                .willReturn(Optional.of(item));

        // Then
        assertThrows(InvalidBookingDateException.class,
                () -> bookingService.saveBooking(bookingDto, 2L));
    }

    @Test
    void shouldNotSaveBookingWhenStartIsBeforeNow() {
        // Given
        Booking booking = new Booking(
                LocalDateTime.of(2023, 10, 12, 12, 10),
                LocalDateTime.of(2023, 11, 12, 12, 10),
                item,
                booker
        );

        BookingDto bookingDto = BookingMapper.toBookingDto(booking);

        given(itemRepository
                .findById(anyLong()))
                .willReturn(Optional.of(item));

        // Then
        assertThrows(InvalidBookingDateException.class,
                () -> bookingService.saveBooking(bookingDto, 2L));
    }

    @Test
    void shouldGetBookingByIdAndBooker() {
        // Given
        given(userRepository.findById(anyLong())).willReturn(Optional.of(booker));
        given(bookingRepository.findById(anyLong())).willReturn(Optional.of(booking));

        // When
        when(bookingRepository.findBookingByIdAndBooker(1L, booker)).thenReturn(booking);

        BookingDto bookingDto = bookingService.getBookingById(1L, 1L);

        // Then
        assertNotNull(bookingDto);
        assertThat(bookingDto.getStart()).isEqualTo(booking.getStart());
        assertThat(bookingDto.getEnd()).isEqualTo(booking.getEnd());
        assertThat(bookingDto.getItem()).isEqualTo(item);
        assertThat(bookingDto.getStatus()).isEqualTo(booking.getStatus());
        assertThat(bookingDto.getBooker()).isEqualTo(booker);

    }

    @Test
    void shouldGetBookingByIdAndItemOwner() {
        // Given
        given(userRepository.findById(anyLong())).willReturn(Optional.of(owner));
        given(bookingRepository.findById(anyLong())).willReturn(Optional.of(booking));

        // When

        when(bookingRepository.findBookingByIdAndItemOwner(1L, owner)).thenReturn(booking);

        BookingDto bookingDto = bookingService.getBookingById(1L, 1L);

        // Then
        assertNotNull(bookingDto);
        assertThat(bookingDto.getStart()).isEqualTo(booking.getStart());
        assertThat(bookingDto.getEnd()).isEqualTo(booking.getEnd());
        assertThat(bookingDto.getItem()).isEqualTo(item);
        assertThat(bookingDto.getStatus()).isEqualTo(booking.getStatus());
        assertThat(bookingDto.getBooker()).isEqualTo(booker);

    }

    @Test
    void shouldUpdateBookingStatusToApprovedWhenTrue() {
        // Given
        given(userRepository.findById(anyLong())).willReturn(Optional.of(owner));
        given(bookingRepository.findById(anyLong())).willReturn(Optional.of(booking));

        // When
        BookingDto bookingDto = bookingService.updateBookingStatus(1L, true, 1L);

        // Then
        assertNotNull(bookingDto);
        assertThat(bookingDto.getStart()).isEqualTo(booking.getStart());
        assertThat(bookingDto.getEnd()).isEqualTo(booking.getEnd());
        assertThat(bookingDto.getItem()).isEqualTo(item);
        assertThat(bookingDto.getStatus()).isEqualTo(BookingStatus.APPROVED);
        assertThat(bookingDto.getBooker()).isEqualTo(booker);

    }

    @Test
    void shouldUpdateBookingStatusToRejectedWhenFalse() {
        // Given
        given(userRepository.findById(anyLong())).willReturn(Optional.of(owner));
        given(bookingRepository.findById(anyLong())).willReturn(Optional.of(booking));

        // When
        BookingDto bookingDto = bookingService.updateBookingStatus(1L, false, 1L);

        // Then
        assertNotNull(bookingDto);
        assertThat(bookingDto.getStart()).isEqualTo(booking.getStart());
        assertThat(bookingDto.getEnd()).isEqualTo(booking.getEnd());
        assertThat(bookingDto.getItem()).isEqualTo(item);
        assertThat(bookingDto.getStatus()).isEqualTo(BookingStatus.REJECTED);
        assertThat(bookingDto.getBooker()).isEqualTo(booker);

    }

    @Test
    void shouldNotUpdateBookingStatusWhenStatusIsAlreadyApproved() {
        // Given
        booking.setId(1L);
        booking.setStatus(BookingStatus.APPROVED);

        given(userRepository
                .findById(anyLong()))
                .willReturn(Optional.of(owner));

        given(bookingRepository
                .findById(anyLong()))
                .willReturn(Optional.of(booking));

        // Then
        assertThrows(BookingStatusIsAlreadyApprovedException.class,
                () -> bookingService.updateBookingStatus(1L, true, 1L));
    }

    @Test
    void shouldNotReturnBookingByNonExistentId() {
        // Given
        given(userRepository
                .findById(anyLong()))
                .willReturn(Optional.of(booker));

        // Then
        assertThrows(BookingNotFoundException.class,
                () -> bookingService.getBookingById(999L, 2L));
    }

    @Test
    void shouldGetAllBookingsByBookerStatusWaiting() {
        // Given
        Page<Booking> page = new PageImpl<>(List.of(booking));
        BookingDto bookingDto = BookingMapper.toBookingDto(booking);

        given(userRepository.findById(anyLong())).willReturn(Optional.of(booker));
        given(bookingRepository.findAllByBookerOrderByStartDesc(booker,
                PageRequest.of(1, 1))).willReturn(page);

        // When
        when(bookingRepository
                .findAllByBookerAndStatusOrderByStartDesc(
                        booker, BookingStatus.WAITING, PageRequest.of(1, 1))).thenReturn(page);

        List<BookingDto> bookingDtoList = bookingService
                .getAllBookingsByBooker("WAITING", 1L, 1, 1);

        // Then
        assertNotNull(bookingDtoList);
        assertThat(bookingDtoList.size()).isEqualTo(1);
        assertThat(bookingDtoList.contains(bookingDto)).isTrue();
        assertThat(bookingDtoList.get(0).getStatus()).isEqualTo(BookingStatus.WAITING);

    }

    @Test
    void shouldGetAllBookingsByBookerStatusRejected() {
        // Given
        booking.setStatus(BookingStatus.REJECTED);

        Page<Booking> page = new PageImpl<>(List.of(booking));
        BookingDto bookingDto = BookingMapper.toBookingDto(booking);

        given(userRepository.findById(anyLong())).willReturn(Optional.of(booker));
        given(bookingRepository.findAllByBookerOrderByStartDesc(booker,
                PageRequest.of(1, 1))).willReturn(page);

        // When
        when(bookingRepository
                .findAllByBookerAndStatusOrderByStartDesc(
                        booker, BookingStatus.REJECTED, PageRequest.of(1, 1))).thenReturn(page);

        List<BookingDto> bookingDtoList = bookingService
                .getAllBookingsByBooker("REJECTED", 1L, 1, 1);

        // Then
        assertNotNull(bookingDtoList);
        assertThat(bookingDtoList.size()).isEqualTo(1);
        assertThat(bookingDtoList.contains(bookingDto)).isTrue();
        assertThat(bookingDtoList.get(0).getStatus()).isEqualTo(BookingStatus.REJECTED);

    }


    @Test
    void shouldGetAllBookingsByBookerStatusNull() {
        // Given
        Page<Booking> page = new PageImpl<>(List.of(booking));
        BookingDto bookingDto = BookingMapper.toBookingDto(booking);

        given(userRepository.findById(anyLong())).willReturn(Optional.of(booker));
        given(bookingRepository.findAllByBookerOrderByStartDesc(booker,
                PageRequest.of(1, 1))).willReturn(page);

        // When
        when(bookingRepository
                .findAllByBookerOrderByStartDesc(
                       booker,
                       PageRequest.of(1, 1)
                )
        ).thenReturn(page);

        List<BookingDto> bookingDtoList = bookingService
                .getAllBookingsByBooker(null, 1L, 1, 1);

        // Then
        assertNotNull(bookingDtoList);
        assertThat(bookingDtoList.size()).isEqualTo(1);
        assertThat(bookingDtoList.contains(bookingDto)).isTrue();
        assertThat(bookingDtoList.get(0).getStatus()).isEqualTo(BookingStatus.WAITING);

    }

    @Test
    void shouldNotGetAllBookingsByUnsupportedBookingStatus() {
        // Then
        assertThrows(UnsupportedBookingStatusException.class,
                () -> bookingService.getAllBookingsByBooker("UNSUPPORTED", 2L, 0, 5));

    }

    @Test
    void shouldGetAllBookingsByItemOwnerStatusWaiting() {
        // Given
        Page<Booking> page = new PageImpl<>(List.of(booking));
        BookingDto bookingDto = BookingMapper.toBookingDto(booking);

        given(userRepository.findById(anyLong())).willReturn(Optional.of(owner));
        given(bookingRepository.findAllByItemOwnerOrderByStartDesc(owner,
                PageRequest.of(1, 1))).willReturn(page);

        // When
        when(bookingRepository
                .findAllByItemOwnerAndStatusOrderByStartDesc(
                        owner, BookingStatus.WAITING, PageRequest.of(1, 1))).thenReturn(page);

        List<BookingDto> bookingDtoList = bookingService
                .getAllBookingsByItemOwner("WAITING", 1L, 1, 1);

        // Then
        assertNotNull(bookingDtoList);
        assertThat(bookingDtoList.size()).isEqualTo(1);
        assertThat(bookingDtoList.contains(bookingDto)).isTrue();
        assertThat(bookingDtoList.get(0).getStatus()).isEqualTo(BookingStatus.WAITING);
        assertThat(bookingDtoList.get(0).getItem().getOwner()).isEqualTo(owner);

    }

    @Test
    void shouldGetAllBookingsByItemOwnerStatusRejected() {
        // Given
        booking.setStatus(BookingStatus.REJECTED);

        Page<Booking> page = new PageImpl<>(List.of(booking));
        BookingDto bookingDto = BookingMapper.toBookingDto(booking);

        given(userRepository.findById(anyLong())).willReturn(Optional.of(owner));
        given(bookingRepository.findAllByItemOwnerOrderByStartDesc(owner,
                PageRequest.of(1, 1))).willReturn(page);

        // When
        when(bookingRepository
                .findAllByItemOwnerAndStatusOrderByStartDesc(
                        owner, BookingStatus.REJECTED, PageRequest.of(1, 1))).thenReturn(page);

        List<BookingDto> bookingDtoList = bookingService
                .getAllBookingsByItemOwner("REJECTED", 1L, 1, 1);

        // Then
        assertNotNull(bookingDtoList);
        assertThat(bookingDtoList.size()).isEqualTo(1);
        assertThat(bookingDtoList.contains(bookingDto)).isTrue();
        assertThat(bookingDtoList.get(0).getStatus()).isEqualTo(BookingStatus.REJECTED);
        assertThat(bookingDtoList.get(0).getItem().getOwner()).isEqualTo(owner);

    }

    @Test
    void shouldGetAllBookingsByItemOwnerStatusNull() {
        // Given
        Page<Booking> page = new PageImpl<>(List.of(booking));
        BookingDto bookingDto = BookingMapper.toBookingDto(booking);

        given(userRepository.findById(anyLong())).willReturn(Optional.of(owner));
        given(bookingRepository.findAllByItemOwnerOrderByStartDesc(owner,
                PageRequest.of(1, 1))).willReturn(page);

        // When
        when(bookingRepository
                .findAllByItemOwnerOrderByStartDesc(
                        owner,
                        PageRequest.of(1, 1)
                )
        ).thenReturn(page);

        List<BookingDto> bookingDtoList = bookingService
                .getAllBookingsByItemOwner(null, 1L, 1, 1);

        // Then
        assertNotNull(bookingDtoList);
        assertThat(bookingDtoList.size()).isEqualTo(1);
        assertThat(bookingDtoList.contains(bookingDto)).isTrue();
        assertThat(bookingDtoList.get(0).getStatus()).isEqualTo(BookingStatus.WAITING);
        assertThat(bookingDtoList.get(0).getItem().getOwner()).isEqualTo(owner);

    }
}
