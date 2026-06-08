import { API_BASE_URL } from './config';
import { ApiError, ApiResponse } from './types';

import { getToken } from '@/storage/tokenStorage';

type QueryParams = Record<string, unknown>;

let unauthorizedHandler: (() => void) | null = null;

export function setUnauthorizedHandler(handler: (() => void) | null): void {
  unauthorizedHandler = handler;
}

export function apiGet<T>(path: string, query?: QueryParams): Promise<T> {
  return request<T>(path, { method: 'GET', query });
}

export function apiPost<T>(path: string, body?: unknown): Promise<T> {
  return request<T>(path, { method: 'POST', body });
}

export function apiPatch<T>(path: string, body?: unknown): Promise<T> {
  return request<T>(path, { method: 'PATCH', body });
}

export function apiDelete<T>(path: string): Promise<T> {
  return request<T>(path, { method: 'DELETE' });
}

async function request<T>(
  path: string,
  options: {
    method: 'GET' | 'POST' | 'PATCH' | 'DELETE';
    body?: unknown;
    query?: QueryParams;
  },
): Promise<T> {
  const token = await getToken();
  const headers: HeadersInit = {
    Accept: 'application/json',
  };

  if (options.body !== undefined) {
    headers['Content-Type'] = 'application/json';
  }

  if (token) {
    headers.Authorization = `Bearer ${token}`;
  }

  let response: Response;

  try {
    response = await fetch(buildUrl(path, options.query), {
      method: options.method,
      headers,
      body: options.body === undefined ? undefined : JSON.stringify(options.body),
    });
  } catch {
    throw new ApiError(0, 'Network request failed.');
  }

  const apiResponse = await parseApiResponse<T>(response);

  if (!response.ok || !apiResponse.success) {
    if (response.status === 401) {
      unauthorizedHandler?.();
    }

    throw new ApiError(response.status, apiResponse.message || defaultMessage(response.status));
  }

  return apiResponse.data as T;
}

function buildUrl(path: string, query?: QueryParams): string {
  const normalizedBaseUrl = API_BASE_URL.replace(/\/$/, '');
  const normalizedPath = path.startsWith('/') ? path : `/${path}`;
  const url = new URL(`${normalizedBaseUrl}${normalizedPath}`);

  if (query) {
    Object.entries(query).forEach(([key, value]) => {
      if (value === undefined || value === null) {
        return;
      }

      if (Array.isArray(value)) {
        value.forEach((item) => {
          if (item !== undefined && item !== null) {
            url.searchParams.append(key, String(item));
          }
        });
        return;
      }

      url.searchParams.append(key, String(value));
    });
  }

  return url.toString();
}

async function parseApiResponse<T>(response: Response): Promise<ApiResponse<T>> {
  const text = await response.text();

  if (!text) {
    return {
      success: response.ok,
      data: null,
      message: response.ok ? null : defaultMessage(response.status),
    };
  }

  try {
    return JSON.parse(text) as ApiResponse<T>;
  } catch {
    return {
      success: false,
      data: null,
      message: defaultMessage(response.status),
    };
  }
}

function defaultMessage(status: number): string {
  if (status === 0) {
    return 'Network request failed.';
  }

  if (status === 401) {
    return 'Authentication is required.';
  }

  if (status === 403) {
    return 'You do not have permission.';
  }

  if (status >= 500) {
    return 'Server error occurred.';
  }

  return 'Request failed.';
}
