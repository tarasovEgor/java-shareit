package ru.practicum.shareit.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.item.exception.InvalidAvailableFieldException;
import ru.practicum.shareit.item.exception.InvalidCommentException;
import ru.practicum.shareit.item.exception.InvalidItemDescriptionException;
import ru.practicum.shareit.item.exception.InvalidItemNameException;
import ru.practicum.shareit.user.exception.InvalidEmailException;
import ru.practicum.shareit.user.exception.UserDoesNotExistException;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidEmailException(final InvalidEmailException e) {
        return new ErrorResponse(
                e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserDoesNotExistException(final UserDoesNotExistException e) {
        return new ErrorResponse(
                e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidItemNameException(final InvalidItemNameException e) {
        return new ErrorResponse(
                e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidItemDescriptionException(final InvalidItemDescriptionException e) {
        return new ErrorResponse(
                e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidAvailableFieldException(final InvalidAvailableFieldException e) {
        return new ErrorResponse(
                e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidCommentException(final InvalidCommentException e) {
        return new ErrorResponse(
                e.getMessage()
        );
    }

}
