import { apiGet, apiPost } from './client';
import { RoleType } from './types';

export type StudentProfileResponse = {
  studentNumber: string;
  department: string;
  grade: number;
};

export type AuthUser = {
  id: number;
  email: string;
  name: string;
  role: RoleType;
  studentProfile: StudentProfileResponse | null;
};

export type LoginRequest = {
  email: string;
  password: string;
};

export type LoginResponse = {
  accessToken: string;
  tokenType: string;
  expiresIn: number;
  user: AuthUser;
};

export type MeResponse = AuthUser;

export function login(email: string, password: string): Promise<LoginResponse> {
  const request: LoginRequest = { email, password };
  return apiPost<LoginResponse>('/api/auth/login', request);
}

export function me(): Promise<MeResponse> {
  return apiGet<MeResponse>('/api/auth/me');
}

export function logout(): Promise<void> {
  return apiPost<void>('/api/auth/logout');
}
