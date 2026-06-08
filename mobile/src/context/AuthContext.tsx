import {
  createContext,
  ReactNode,
  useCallback,
  useEffect,
  useMemo,
  useState,
} from 'react';

import { setUnauthorizedHandler } from '@/api/client';
import { AuthUser, login as loginApi, logout as logoutApi, me as meApi } from '@/api/auth';
import { RoleType } from '@/api/types';
import { getToken, removeToken, saveToken } from '@/storage/tokenStorage';

type AuthState = {
  user: AuthUser | null;
  role: RoleType | null;
  token: string | null;
  isLoading: boolean;
};

export type AuthContextValue = AuthState & {
  isAuthenticated: boolean;
  login: (email: string, password: string) => Promise<void>;
  logout: () => Promise<void>;
  restoreSession: () => Promise<void>;
  loadMe: () => Promise<void>;
};

export type AuthProviderProps = {
  children: ReactNode;
};

export const AuthContext = createContext<AuthContextValue | null>(null);

export function AuthProvider({ children }: AuthProviderProps) {
  const [user, setUser] = useState<AuthUser | null>(null);
  const [role, setRole] = useState<RoleType | null>(null);
  const [token, setToken] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  const clearLocalSession = useCallback(async () => {
    await removeToken();
    setUser(null);
    setRole(null);
    setToken(null);
  }, []);

  const loadMe = useCallback(async () => {
    try {
      const currentUser = await meApi();
      setUser(currentUser);
      setRole(currentUser.role);
    } catch (error) {
      await clearLocalSession();
      throw error;
    }
  }, [clearLocalSession]);

  const restoreSession = useCallback(async () => {
    setIsLoading(true);

    try {
      const storedToken = await getToken();

      if (!storedToken) {
        setUser(null);
        setRole(null);
        setToken(null);
        return;
      }

      setToken(storedToken);
      const currentUser = await meApi();
      setUser(currentUser);
      setRole(currentUser.role);
    } catch {
      await clearLocalSession();
    } finally {
      setIsLoading(false);
    }
  }, [clearLocalSession]);

  const login = useCallback(async (email: string, password: string) => {
    const response = await loginApi(email, password);

    await saveToken(response.accessToken);
    setToken(response.accessToken);
    setUser(response.user);
    setRole(response.user.role);
  }, []);

  const logout = useCallback(async () => {
    try {
      await logoutApi();
    } catch {
      // Local session cleanup must happen even if the stateless backend logout fails.
    } finally {
      await clearLocalSession();
    }
  }, [clearLocalSession]);

  useEffect(() => {
    void restoreSession();
  }, [restoreSession]);

  useEffect(() => {
    setUnauthorizedHandler(() => {
      void clearLocalSession();
    });

    return () => {
      setUnauthorizedHandler(null);
    };
  }, [clearLocalSession]);

  const value = useMemo<AuthContextValue>(
    () => ({
      user,
      role,
      token,
      isLoading,
      isAuthenticated: token !== null && user !== null,
      login,
      logout,
      restoreSession,
      loadMe,
    }),
    [user, role, token, isLoading, login, logout, restoreSession, loadMe],
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}
