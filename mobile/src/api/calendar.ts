import { apiDelete, apiGet, apiPatch, apiPost } from './client';

export type CalendarEventCreator = {
  id: number;
  name: string;
};

export type CalendarEventListItem = {
  id: number;
  title: string;
  location: string | null;
  startAt: string;
  endAt: string;
  allDay: boolean;
};

export type CalendarEventDetail = {
  id: number;
  title: string;
  description: string | null;
  location: string | null;
  startAt: string;
  endAt: string;
  allDay: boolean;
  createdBy: CalendarEventCreator;
  createdAt: string;
  updatedAt: string;
};

export type CreateCalendarEventRequest = {
  title: string;
  description?: string | null;
  location?: string | null;
  startAt: string;
  endAt: string;
  allDay?: boolean | null;
};

export type UpdateCalendarEventRequest = {
  title?: string | null;
  description?: string | null;
  location?: string | null;
  startAt?: string | null;
  endAt?: string | null;
  allDay?: boolean | null;
};

export function getCalendarEvents(
  from: string,
  to: string,
): Promise<CalendarEventListItem[]> {
  return apiGet<CalendarEventListItem[]>('/api/calendar/events', { from, to });
}

export function getCalendarEvent(eventId: number): Promise<CalendarEventDetail> {
  return apiGet<CalendarEventDetail>(`/api/calendar/events/${eventId}`);
}

export function createCalendarEvent(
  request: CreateCalendarEventRequest,
): Promise<CalendarEventDetail> {
  return apiPost<CalendarEventDetail>('/api/calendar/events', request);
}

export function updateCalendarEvent(
  eventId: number,
  request: UpdateCalendarEventRequest,
): Promise<CalendarEventDetail> {
  return apiPatch<CalendarEventDetail>(`/api/calendar/events/${eventId}`, request);
}

export function deleteCalendarEvent(eventId: number): Promise<void> {
  return apiDelete<void>(`/api/calendar/events/${eventId}`);
}
