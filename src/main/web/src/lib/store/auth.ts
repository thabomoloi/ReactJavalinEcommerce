import { create } from "zustand";
import { User } from "@/lib/data/models/types";
import { getCurrentUser, refreshJWT } from "@/lib/data/api/user";
import { AxiosError } from "axios";

export interface AuthState {
  isAuthenticated: boolean;
  currentUser: User | null;
  isLoading: boolean;
  verifyAuthentication: () => Promise<void>;
}

export const useAuth = create<AuthState>((set) => {
  const fetchCurrentUser = async () => {
    const user: User | null = await getCurrentUser();
    set({ currentUser: user, isAuthenticated: !!user });
  };

  return {
    isAuthenticated: false,
    currentUser: null,
    isLoading: true,

    verifyAuthentication: async () => {
      set({ isLoading: true });
      try {
        await fetchCurrentUser();
      } catch (error) {
        if (error instanceof AxiosError) {
          if (error.response?.status == 401) {
            try {
              await refreshJWT();
              await fetchCurrentUser();
              // eslint-disable-next-line @typescript-eslint/no-unused-vars
            } catch (refreshError) {
              set({ currentUser: null, isAuthenticated: false });
            }
          }
        } else {
          set({ currentUser: null, isAuthenticated: false });
        }
      } finally {
        set({ isLoading: false });
      }
    },
  };
});
