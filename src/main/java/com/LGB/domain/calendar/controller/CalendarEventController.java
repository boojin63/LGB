package com.LGB.domain.calendar.controller;

import com.LGB.domain.calendar.dto.CalendarEventDetailResponse;
import com.LGB.domain.calendar.dto.CalendarEventListResponse;
import com.LGB.domain.calendar.dto.CreateCalendarEventRequest;
import com.LGB.domain.calendar.dto.UpdateCalendarEventRequest;
import com.LGB.domain.calendar.service.CalendarEventService;
import com.LGB.global.response.ApiResponse;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/calendar/events")
@RequiredArgsConstructor
public class CalendarEventController {

    private final CalendarEventService calendarEventService;

    @PostMapping
    public ResponseEntity<ApiResponse<CalendarEventDetailResponse>> create(
            @AuthenticationPrincipal Long creatorId,
            @Valid @RequestBody CreateCalendarEventRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(calendarEventService.create(creatorId, request)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CalendarEventListResponse>>> getEvents(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to
    ) {
        return ResponseEntity.ok(ApiResponse.success(calendarEventService.getEvents(from, to)));
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<ApiResponse<CalendarEventDetailResponse>> getEvent(
            @PathVariable Long eventId
    ) {
        return ResponseEntity.ok(ApiResponse.success(calendarEventService.getEvent(eventId)));
    }

    @PatchMapping("/{eventId}")
    public ResponseEntity<ApiResponse<CalendarEventDetailResponse>> update(
            @PathVariable Long eventId,
            @Valid @RequestBody UpdateCalendarEventRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(calendarEventService.update(eventId, request)));
    }

    @DeleteMapping("/{eventId}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long eventId) {
        calendarEventService.delete(eventId);
        return ResponseEntity.ok(ApiResponse.emptySuccess());
    }
}
