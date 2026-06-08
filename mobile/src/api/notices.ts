import { apiDelete, apiGet, apiPatch, apiPost } from './client';
import { PageResponse } from './types';

export type NoticeAuthor = {
  id: number;
  name: string;
};

export type NoticeListItem = {
  id: number;
  title: string;
  authorName: string;
  pinned: boolean;
  viewCount: number;
  createdAt: string;
};

export type NoticeDetail = {
  id: number;
  title: string;
  content: string;
  author: NoticeAuthor;
  pinned: boolean;
  viewCount: number;
  createdAt: string;
  updatedAt: string;
};

export type NoticePage = PageResponse<NoticeListItem>;

export type CreateNoticeRequest = {
  title: string;
  content: string;
  pinned?: boolean | null;
};

export type UpdateNoticeRequest = {
  title?: string | null;
  content?: string | null;
  pinned?: boolean | null;
};

export function getNotices(page = 0, size = 10): Promise<NoticePage> {
  return apiGet<NoticePage>('/api/notices', { page, size });
}

export function getNotice(noticeId: number): Promise<NoticeDetail> {
  return apiGet<NoticeDetail>(`/api/notices/${noticeId}`);
}

export function createNotice(request: CreateNoticeRequest): Promise<NoticeDetail> {
  return apiPost<NoticeDetail>('/api/notices', request);
}

export function updateNotice(
  noticeId: number,
  request: UpdateNoticeRequest,
): Promise<NoticeDetail> {
  return apiPatch<NoticeDetail>(`/api/notices/${noticeId}`, request);
}

export function deleteNotice(noticeId: number): Promise<void> {
  return apiDelete<void>(`/api/notices/${noticeId}`);
}
