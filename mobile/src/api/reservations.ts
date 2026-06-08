import { apiGet, apiPost } from './client';
import { PageResponse } from './types';

import { ResourceType } from './resources';

export type ReservationStatus = 'PENDING' | 'APPROVED' | 'REJECTED';

export type ReservationResource = {
  id: number;
  name: string;
  type: ResourceType;
  location: string | null;
};

export type ReservationRequester = {
  id: number;
  name: string;
  email: string;
};

export type ReservationDecider = {
  id: number;
  name: string;
};

export type ReservationItem = {
  id: number;
  resource: ReservationResource;
  requester: ReservationRequester;
  startAt: string;
  endAt: string;
  status: ReservationStatus;
  purpose: string;
  rejectReason: string | null;
  decidedAt: string | null;
  decidedBy: ReservationDecider | null;
  createdAt: string;
  updatedAt: string;
};

export type ReservationPage = PageResponse<ReservationItem>;

export type CreateReservationRequest = {
  resourceId: number;
  startAt: string;
  endAt: string;
  purpose: string;
};

export function getMyReservations(page = 0, size = 10): Promise<ReservationPage> {
  return apiGet<ReservationPage>('/api/reservations/my', { page, size });
}

export function getReservation(reservationId: number): Promise<ReservationItem> {
  return apiGet<ReservationItem>(`/api/reservations/${reservationId}`);
}

export function createReservation(
  request: CreateReservationRequest,
): Promise<ReservationItem> {
  return apiPost<ReservationItem>('/api/reservations', request);
}
