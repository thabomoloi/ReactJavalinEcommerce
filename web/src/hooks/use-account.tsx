import { useQueryClient } from "@tanstack/react-query";
import {
  confirmAccount,
  deleteAccount,
  resetPassword,
  sendConfirmationLink,
  sendResetPasswordLink,
  updateProfile,
} from "@/lib/data/api/user";
import { useCreateMutation } from "./helpers/use-create-mutation";

export function useAccount() {
  const queryClient = useQueryClient();

  const invalidateCurrentUser = () => {
    queryClient.invalidateQueries({ queryKey: ["currentUser"] });
  };

  const updateAccMutation = useCreateMutation(
    updateProfile,
    "Account has been updated successfully.",
    "Failed to update account."
  );

  const deleteAccMutation = useCreateMutation(
    deleteAccount,
    "Account has been deleted successfully.",
    "Failed to delete account.",
    invalidateCurrentUser
  );

  const resetPasswordMutation = useCreateMutation(
    resetPassword,
    "Account has been deleted successfully.",
    "Failed to delete account."
  );

  const sendConfirmationLinkMutation = useCreateMutation(
    sendConfirmationLink,
    "The confirmation link has been sent to your email.",
    "Failed to send confirmation link."
  );

  const confirmAccountMutation = useCreateMutation(
    confirmAccount,
    "Your account has been verified.",
    "Failed to verify account.",
    invalidateCurrentUser
  );

  const sendPasswordResetLinkMutation = useCreateMutation(
    sendResetPasswordLink,
    "The password reset link has been sent to your email.",
    "Failed to send password reset link."
  );

  return {
    updateAccount: updateAccMutation.mutate,
    deleteAccount: deleteAccMutation.mutate,
    resetPassword: resetPasswordMutation.mutate,
    sendConfirmationLink: sendConfirmationLinkMutation.mutate,
    confirmAccount: confirmAccountMutation.mutate,
    sendResetPasswordLink: sendPasswordResetLinkMutation.mutate,
    isPending:
      updateAccMutation.isPending ||
      deleteAccMutation.isPending ||
      resetPasswordMutation.isPending ||
      sendConfirmationLinkMutation.isPending ||
      (confirmAccountMutation.isPending && !confirmAccountMutation.isPaused) ||
      sendPasswordResetLinkMutation.isPending,
  };
}
