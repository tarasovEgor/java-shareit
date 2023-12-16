package ru.practicum.shareit.error;

import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import ru.practicum.shareit.exception.*;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleInternalServerError(final Throwable e) {
        return new ErrorResponse(
                e.getMessage()
        );
    }

//    @ExceptionHandler
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public ErrorResponse handleInvalidEmailException(final InvalidEmailException e) {
//        return new ErrorResponse(
//                e.getMessage()
//        );
//    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserDoesNotExistException(final UserDoesNotExistException e) {
        return new ErrorResponse(
                e.getMessage()
        );
    }

//    @ExceptionHandler
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public ErrorResponse handleInvalidItemNameException(final InvalidItemNameException e) {
//        return new ErrorResponse(
//                e.getMessage()
//        );
//    }

//    @ExceptionHandler
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public ErrorResponse handleInvalidItemDescriptionException(final InvalidItemDescriptionException e) {
//        return new ErrorResponse(
//                e.getMessage()
//        );
//    }

//    @ExceptionHandler
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public ErrorResponse handleInvalidAvailableFieldException(final InvalidAvailableFieldException e) {
//        return new ErrorResponse(
//                e.getMessage()
//        );
//    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleItemDoesNotExistException(final ItemDoesNotExistException e) {
        return new ErrorResponse(
                e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleItemIsUnavailableException(final ItemIsUnavailableException e) {
        return new ErrorResponse(
                e.getMessage()
        );
    }

//    @ExceptionHandler
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public ErrorResponse handleInvalidBookingDateException(final InvalidBookingDateException e) {
//        return new ErrorResponse(
//                e.getMessage()
//        );
//    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleBookingNotFoundException(final BookingNotFoundException e) {
        return new ErrorResponse(
                e.getMessage()
        );
    }

//    @ExceptionHandler
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public ErrorResponse handleUnsupportedBookingException(final UnsupportedBookingStatusException e) {
//        return new ErrorResponse(
//                e.getMessage()
//        );
//    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBookingStatusIsAlreadyApprovedException(final BookingStatusIsAlreadyApprovedException e) {
        return new ErrorResponse(
                e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleInvalidItemOwnerException(final InvalidItemOwnerException e) {
        return new ErrorResponse(
                e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleNoBookingForCommentException(final NoBookingForCommentException e) {
        return new ErrorResponse(
                e.getMessage()
        );
    }

//    @ExceptionHandler
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public ErrorResponse handleInvalidCommentException(final InvalidCommentException e) {
//        return new ErrorResponse(
//                e.getMessage()
//        );
//    }

//    @ExceptionHandler
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public ErrorResponse handleInvalidItemRequestDescriptionException(final InvalidItemRequestDescriptionException e) {
//        return new ErrorResponse(
//                e.getMessage()
//        );
//    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleItemRequestDoesNotExistException(final ItemRequestDoesNotExistException e) {
        return new ErrorResponse(
                e.getMessage()
        );
    }
}
