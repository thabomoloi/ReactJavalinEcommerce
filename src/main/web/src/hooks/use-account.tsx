import { useMutation, useQueryClient } from "@tanstack/react-query";
import { useToast } from "./use-toast";
import { AxiosError } from "axios";
import { handleMutationError } from "./helpers";
import {
  deleteAccount,
  resetPassword,
  updateProfile,
} from "@/lib/data/api/user";
import { UserUpdateSchemaType } from "@/lib/data/schemas/user";

// Custom hook for creating authentication mutations
function useCreateAccMutation<T = unknown, V = void>(
  mutationFn: (variables: V) => Promise<T>,
  successMessage: string,
  errorMessage: string
) {
  const { toast } = useToast();
  const queryClient = useQueryClient();

  return useMutation<T, AxiosError, V>({
    mutationFn,
    onSuccess: (message) => {
      queryClient.invalidateQueries({ queryKey: ["currentUser"] });
      toast({
        variant: "success",
        title: typeof message === "string" ? message : successMessage,
      });
    },
    onError: (error) => handleMutationError(error, errorMessage, toast),
  });
}

export function useAccount() {
  const updateAccMutation = useCreateAccMutation<string, UserUpdateSchemaType>(
    updateProfile,
    "Account has been updated successfully.",
    "Failed to update account."
  );

  const deleteAccMutation = useCreateAccMutation<string, number>(
    deleteAccount,
    "Account has been deleted successfully.",
    "Failed to delete account."
  );

  const resetPasswordMutation = useCreateAccMutation<
    string,
    Parameters<typeof resetPassword>[0]
  >(
    resetPassword,
    "Account has been deleted successfully.",
    "Failed to delete account."
  );

  return {
    updateAccount: updateAccMutation.mutate,
    deleteAccount: deleteAccMutation.mutate,
    resetPassword: resetPasswordMutation.mutate,
    isLoading:
      updateAccMutation.isPending ||
      deleteAccMutation.isPending ||
      resetPasswordMutation.isPending,
  };
}
