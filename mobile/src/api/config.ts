declare const process:
  | {
      env?: {
        EXPO_PUBLIC_API_BASE_URL?: string;
      };
    }
  | undefined;

const DEFAULT_API_BASE_URL = 'http://localhost:18081';

export const API_BASE_URL =
  (typeof process !== 'undefined' && process.env?.EXPO_PUBLIC_API_BASE_URL) ||
  DEFAULT_API_BASE_URL;
