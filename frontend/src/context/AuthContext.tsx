import React, { createContext, useContext, useState, useEffect, useCallback } from 'react';
import { login as apiLogin, logout as apiLogout, register as apiRegister } from '../api/auth';
import type { RegisterUserRequest, UserLoginRequest } from '../types';

interface AuthUser {
  username: string;
  fullName?: string;
}

interface AuthContextValue {
  user: AuthUser | null;
  isLoading: boolean;
  login: (data: UserLoginRequest) => Promise<void>;
  register: (data: RegisterUserRequest) => Promise<void>;
  logout: () => Promise<void>;
}

const AuthContext = createContext<AuthContextValue | null>(null);

const STORAGE_KEY = 'ourchemo_user';

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [user, setUser] = useState<AuthUser | null>(() => {
    try {
      const stored = localStorage.getItem(STORAGE_KEY);
      return stored ? (JSON.parse(stored) as AuthUser) : null;
    } catch {
      return null;
    }
  });
  const [isLoading, setIsLoading] = useState(false);

  useEffect(() => {
    if (user) {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(user));
    } else {
      localStorage.removeItem(STORAGE_KEY);
    }
  }, [user]);

  const login = useCallback(async (data: UserLoginRequest) => {
    setIsLoading(true);
    try {
      const res = await apiLogin(data);
      setUser({ username: res.username });
    } finally {
      setIsLoading(false);
    }
  }, []);

  const register = useCallback(async (data: RegisterUserRequest) => {
    setIsLoading(true);
    try {
      const res = await apiRegister(data);
      // Auto-login after register
      await apiLogin({ username: res.username, password: data.password });
      setUser({ username: res.username, fullName: res.fullName });
    } finally {
      setIsLoading(false);
    }
  }, []);

  const logout = useCallback(async () => {
    if (!user) return;
    setIsLoading(true);
    try {
      await apiLogout({ username: user.username });
    } finally {
      setUser(null);
      setIsLoading(false);
    }
  }, [user]);

  return (
    <AuthContext.Provider value={{ user, isLoading, login, register, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth must be used within AuthProvider');
  return ctx;
}
