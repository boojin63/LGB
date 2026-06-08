package com.LGB.domain.calendar.repository;

import com.LGB.domain.calendar.entity.CalendarEvent;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CalendarEventRepository extends JpaRepository<CalendarEvent, Long> {

    @Query("""
            select event
            from CalendarEvent event
            where event.startAt < :to
              and event.endAt > :from
            order by event.startAt asc, event.id asc
            """)
    List<CalendarEvent> findOverlappingEvents(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );
}
