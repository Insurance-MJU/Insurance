import { create } from 'zustand';
import { persist } from 'zustand/middleware';

interface AuthState {
  isLoggedIn: boolean;
  role: string | null;
  userId: string | null;
  name: string | null;
  isVerified: boolean;
  identityToken: string | null;
  _hydrated: boolean;
}

interface AuthActions {
  login: (role: string, name?: string, userId?: string) => void;
  logout: () => void;
  setVerified: (token: string) => void;
  clearVerified: () => void;
  setHydrated: () => void;
}

export const useAuthStore = create<AuthState & AuthActions>()(
  persist(
    (set) => ({
      isLoggedIn: false,
      role: null,
      userId: null,
      name: null,
      isVerified: false,
      identityToken: null,
      _hydrated: false,

      login: (role, name, userId) => set({ isLoggedIn: true, role, name: name ?? null, userId: userId ?? null }),
      logout: () => set({ isLoggedIn: false, role: null, userId: null, name: null, isVerified: false, identityToken: null }),
      setVerified: (token) => set({ isVerified: true, identityToken: token }),
      clearVerified: () => set({ isVerified: false, identityToken: null }),
      setHydrated: () => set({ _hydrated: true }),
    }),
    {
      name: 'auth-storage',
      onRehydrateStorage: () => (state) => {
        state?.setHydrated();
      },
    }
  )
);
