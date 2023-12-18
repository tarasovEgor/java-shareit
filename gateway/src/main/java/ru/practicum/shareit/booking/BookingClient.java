package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.exception.InvalidBookingDateException;
import ru.practicum.shareit.booking.validation.BookingValidation;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;

@Service
public class BookingClient extends BaseClient {

    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> getAllBookingsByItemOwner(int from, int size, String status, long ownerId) {
        BookingValidation.isBookingStateValid(status);
        Map<String, Object> parameters = Map.of(
                "state", status,
                "from", from,
                "size", size
        );
        return get("/owner?state={state}&from={from}&size={size}", ownerId, parameters);
    }

    public ResponseEntity<Object> getAllBookingsByBooker(int from, int size, String status, long bookerId) {
        BookingValidation.isBookingStateValid(status);
        Map<String, Object> parameters = Map.of(
                "state", status,
                "from", from,
                "size", size
        );
        return get("/?state={state}&from={from}&size={size}", bookerId, parameters);
    }

    public ResponseEntity<Object> saveBooking(long bookerId, BookingDto bookingDto) {
        if (bookingDto.getStart() == null || bookingDto.getEnd() == null) {
            throw new InvalidBookingDateException("Invalid booking date.");
        }
        BookingValidation.isBookingDateValid(bookingDto.getStart(), bookingDto.getEnd());
        return post("", bookerId, bookingDto);
    }

    public ResponseEntity<Object> getBookingById(long userId, Long bookingId) {
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> updateBookingStatus(long bookingId, Boolean approved, long bookerId) {
        return patch("/" + bookingId + "?approved=" + approved, bookerId);
    }

}
