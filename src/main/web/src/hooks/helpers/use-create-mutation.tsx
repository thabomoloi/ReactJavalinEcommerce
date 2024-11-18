import { AxiosError } from "axios";
import { useToast } from "../use-toast";
import { useMutation } from "@tanstack/react-query";
import { handleMutationError } from ".";

/**
 * Custom hook for creating a mutations
 */
// eslint-disable-next-line @typescript-eslint/no-explicit-any
export function useCreateMutation<T extends (...args: any) => any>(
  mutationFn: T,
  successMessage: string,
  errorMessage: string,
  callbackFn: () => void = () => {}
) {
  const { toast } = useToast();

  return useMutation<Awaited<ReturnType<T>>, AxiosError, Parameters<T>[0]>({
    mutationFn,
    onSuccess: (message) => {
      callbackFn();
      toast({
        variant: "success",
        title:
          typeof message === "string" && message.trim().length !== 0
            ? message
            : successMessage,
      });
    },
    onError: (error) => handleMutationError(error, errorMessage, toast),
  });
}
