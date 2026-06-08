package com.LGB.global.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    INVALID_INPUT(HttpStatus.BAD_REQUEST, "Invalid input value."),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "Invalid email or password."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "Authentication is required."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "You do not have permission."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "Requested resource was not found."),
    NOTICE_NOT_FOUND(HttpStatus.NOT_FOUND, "Notice was not found."),
    INVALID_NOTICE_REQUEST(HttpStatus.BAD_REQUEST, "Invalid notice request."),
    CALENDAR_EVENT_NOT_FOUND(HttpStatus.NOT_FOUND, "Calendar event was not found."),
    INVALID_CALENDAR_EVENT_REQUEST(HttpStatus.BAD_REQUEST, "Invalid calendar event request."),
    INVALID_CALENDAR_EVENT_PERIOD(HttpStatus.BAD_REQUEST, "Invalid calendar event period."),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "Resource was not found."),
    INVALID_RESOURCE_REQUEST(HttpStatus.BAD_REQUEST, "Invalid resource request."),
    RESERVATION_NOT_FOUND(HttpStatus.NOT_FOUND, "Reservation was not found."),
    INVALID_RESERVATION_REQUEST(HttpStatus.BAD_REQUEST, "Invalid reservation request."),
    INVALID_RESERVATION_PERIOD(HttpStatus.BAD_REQUEST, "Invalid reservation period."),
    RESERVATION_TIME_CONFLICT(HttpStatus.CONFLICT, "Reservation time conflicts with an approved reservation."),
    RESERVATION_ALREADY_DECIDED(HttpStatus.CONFLICT, "Reservation was already decided."),
    RESERVATION_ACCESS_DENIED(HttpStatus.FORBIDDEN, "You cannot access this reservation."),
    RESOURCE_INACTIVE(HttpStatus.BAD_REQUEST, "Resource is inactive."),
    POLL_NOT_FOUND(HttpStatus.NOT_FOUND, "Poll was not found."),
    INVALID_POLL_REQUEST(HttpStatus.BAD_REQUEST, "Invalid poll request."),
    INVALID_POLL_PERIOD(HttpStatus.BAD_REQUEST, "Invalid poll period."),
    POLL_ALREADY_CLOSED(HttpStatus.CONFLICT, "Poll was already closed."),
    POLL_OPTION_NOT_FOUND(HttpStatus.NOT_FOUND, "Poll option was not found."),
    POLL_NOT_OPEN(HttpStatus.CONFLICT, "Poll is not open."),
    POLL_NOT_STARTED(HttpStatus.BAD_REQUEST, "Poll has not started."),
    POLL_ENDED(HttpStatus.BAD_REQUEST, "Poll has ended."),
    POLL_ALREADY_VOTED(HttpStatus.CONFLICT, "You have already voted in this poll."),
    POLL_OPTION_MISMATCH(HttpStatus.BAD_REQUEST, "Poll option does not belong to this poll."),
    POLL_RESULT_ACCESS_DENIED(HttpStatus.FORBIDDEN, "You cannot access this poll result."),
    DUPLICATE_RESOURCE(HttpStatus.CONFLICT, "Resource already exists."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error.");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
