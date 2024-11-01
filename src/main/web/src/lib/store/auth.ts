import { create } from "zustand";
import { User } from "@/lib/data/models/types";
import { getCurrentUser, refreshJWT } from "@/lib/data/api/auth";
import { AxiosError } from "axios";

interface AuthState {
  isAuthenticated: boolean;
  currentUser: User | null;
  fetchCurrentUser: () => Promise<void>;
  verifyAuthentication: () => Promise<void>;
}

export const useAuth = create<AuthState>((set) => ({
  isAuthenticated: false,
  currentUser: null,

  fetchCurrentUser: async () => {
    const user: User | null = await getCurrentUser();
    set({ currentUser: user, isAuthenticated: !!user });
  },

  verifyAuthentication: async () => {
    try {
      await useAuth.getState().fetchCurrentUser();
    } catch (error) {
      if (error instanceof AxiosError) {
        if (error.response?.status == 401) {
          try {
            await refreshJWT();
            await useAuth.getState().fetchCurrentUser();
            // eslint-disable-next-line @typescript-eslint/no-unused-vars
          } catch (refreshError) {
            set({ currentUser: null, isAuthenticated: false });
          }
        }
      } else {
        set({ currentUser: null, isAuthenticated: false });
      }
    }
  },
}));
