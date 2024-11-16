import {
  getCurrentUser,
  refreshJWT,
  signIn,
  signOut,
  signUp,
} from "@/lib/data/api/user";
import { User } from "@/lib/data/models/types";
import {
  useMutation,
  useQuery,
  useQueryClient,
  QueryKey,
} from "@tanstack/react-query";
import { AxiosError } from "axios";
import { useToast } from "./use-toast";
import { SignInSchemaType, SignUpSchemaType } from "@/lib/data/schemas/user";
import { handleMutationError } from "./helpers";

// Custom hook for creating authentication mutations
function useCreateAuthMutation<T = unknown, V = void>(
  mutationFn: (variables: V) => Promise<T>,
  successMessage: string,
  errorMessage: string
) {
  const { toast } = useToast();
  const queryClient = useQueryClient();

  return useMutation<T, AxiosError, V>({
    mutationFn,
    onSuccess: (message) => {
      console.log(message);
      queryClient.invalidateQueries({ queryKey: ["currentUser"] });
      toast({
        variant: "success",
        title:
          typeof message === "string" && message.trim().length != 0
            ? message
            : successMessage,
      });
    },
    onError: (error) => handleMutationError(error, errorMessage, toast),
  });
}

function useAuthMutations() {
  const signOutMutation = useCreateAuthMutation<string, void>(
    signOut,
    "Successfully signed out.",
    "Failed to sign out."
  );

  const signInMutation = useCreateAuthMutation<string, SignInSchemaType>(
    signIn,
    "Successfully signed in.",
    "Failed to sign in."
  );

  const signUpMutation = useCreateAuthMutation<string, SignUpSchemaType>(
    signUp,
    "Successfully signed up.",
    "Failed to sign up. Please try again."
  );

  return {
    signOutMutation,
    signInMutation,
    signUpMutation,
  };
}

function useAuthQuery() {
  const { data: currentUser, isLoading } = useQuery<User | null>({
    queryKey: ["currentUser"] as QueryKey,
    queryFn: async () => {
      try {
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
    isLoading:
      authQuery.isLoading ||
      Object.values(authMutations).some((mutation) => mutation.isPending),
  };
}
