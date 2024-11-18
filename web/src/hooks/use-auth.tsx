import {
  getCurrentUser,
  refreshJWT,
  signIn,
  signOut,
  signUp,
} from "@/lib/data/api/user";
import { useQuery, useQueryClient, QueryKey } from "@tanstack/react-query";
import { AxiosError } from "axios";
import { useCreateMutation } from "./helpers/use-create-mutation";
import { User } from "@/lib/data/models/types";

function useAuthMutations() {
  const queryClient = useQueryClient();

  const invalidateCurrentUser = () => {
    queryClient.invalidateQueries({ queryKey: ["currentUser"] });
  };

  const signOutMutation = useCreateMutation(
    signOut,
    "Successfully signed out.",
    "Failed to sign out.",
    invalidateCurrentUser
  );

  const signInMutation = useCreateMutation(
    signIn,
    "Successfully signed in.",
    "Failed to sign in.",
    invalidateCurrentUser
  );

  const signUpMutation = useCreateMutation(
    signUp,
    "Successfully signed up.",
    "Failed to sign up. Please try again.",
    invalidateCurrentUser
  );

  return {
    signOutMutation,
    signInMutation,
    signUpMutation,
  };
}

function useAuthQuery() {
  // TOBE REMOVED
  const delay = (ms: number) =>
    new Promise((resolve) => setTimeout(resolve, ms));

  const { data: currentUser, isLoading } = useQuery<User | null>({
    queryKey: ["currentUser"] as QueryKey,
    queryFn: async () => {
      try {
        // TOBE REMOVED
        await delay(2000);
        return await getCurrentUser();
      } catch (error) {
        if (error instanceof AxiosError && error.response?.status === 401) {
          try {
            await refreshJWT();
            return await getCurrentUser();
          } catch {
            return null;
          }
        }
        throw error;
      }
    },
    retry: 1,
    staleTime: 5 * 60 * 1000,
  });

  return {
    currentUser,
    isAuthenticated: !!currentUser,
    isLoading,
  };
}

export function useAuth() {
  const authQuery = useAuthQuery();
  const authMutations = useAuthMutations();

  return {
    ...authQuery,
    signIn: authMutations.signInMutation.mutate,
    signUp: authMutations.signUpMutation.mutate,
    signOut: authMutations.signOutMutation.mutate,
    isLoading: authQuery.isLoading,
    isPending: Object.values(authMutations).some(
      (mutation) => mutation.isPending
    ),
  };
}
