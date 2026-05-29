import { create } from 'zustand';
import { persist } from 'zustand/middleware';

interface AuthState {
  isLoggedIn: boolean;
  role: string | null;
  isVerified: boolean;
  identityToken: string | null;
  _hydrated: boolean;
}

interface AuthActions {
  login: (role: string) => void;
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
      isVerified: false,
      identityToken: null,
      _hydrated: false,

      login: (role) => set({ isLoggedIn: true, role }),
      logout: () => set({ isLoggedIn: false, role: null, isVerified: false, identityToken: null }),
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
