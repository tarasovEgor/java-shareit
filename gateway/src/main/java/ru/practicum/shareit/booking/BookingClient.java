package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;

@Service
public class BookingClient extends BaseClient {

    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("http://localhost:9090") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

//    public ResponseEntity<Object> getBookings(long userId, BookingStatus status, Integer from, Integer size) {
//        Map<String, Object> parameters = Map.of(
//                "state", status.name(),
//                "from", from,
//                "size", size
//        );
//        return get("?state={state}&from={from}&size={size}", userId, parameters);
//    }

    public ResponseEntity<Object> getAllBookingsByItemOwner(int from, int size, String state, long ownerId) {
        Map<String, Object> parameters = Map.of(
                "state", state,
                "from", from,
                "size", size
        );
        return get("/owner?state={state}&from={from}&size={size}", ownerId, parameters);
    }

    public ResponseEntity<Object> getAllBookingsByBooker(int from, int size, String state, long bookerId) {
        Map<String, Object> parameters = Map.of(
                "state", state,
                "from", from,
                "size", size
        );
        return get("/?state={state}&from={from}&size={size}", bookerId, parameters);
    }


    public ResponseEntity<Object> saveBooking(long bookerId, BookingDto bookingDto) {
        return post("", bookerId, bookingDto);
    }

    public ResponseEntity<Object> getBookingById(long userId, Long bookingId) {
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> updateBookingStatus(long bookingId, Boolean approved, long bookerId) {
//        Map<String, Object> parameters = Map.of(
//                "approved", approved
//        );
        return patch("/" + bookingId + "?approved=" + approved, bookerId);
    }
    /*@PatchMapping("/{bookingId}")
    public BookingDto updateBookingStatus(@PathVariable long bookingId,
                                       @RequestParam Boolean approved,
                                       @RequestHeader("X-Sharer-User-Id") long bookerId) {
        return bookingService.updateBookingStatus(bookingId, approved, bookerId);
    }*/

}
