import { create } from "zustand";
import { User } from "./data/models/types";
import { getCurrentUser, refreshJWT } from "./data/api/auth";
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
            return;
          } catch (refreshError) {
            console.error("Token refresh failed", refreshError);
            set({ currentUser: null, isAuthenticated: false });
          }
        }
      }
      console.error("Unexpected error", error);
      throw error;
    }
  },
}));
