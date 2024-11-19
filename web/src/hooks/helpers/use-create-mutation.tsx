import { AxiosError } from "axios";
import { useToast } from "../use-toast";
import { useMutation } from "@tanstack/react-query";
import { getErrorMessage } from "@/lib/data/api/error-message";

// Reusable helper to handle errors and display toast messages
export function handleMutationError(
  error: Error,
  defaultMessage: string,
  toast: ReturnType<typeof useToast>["toast"]
): void {
  const message = getErrorMessage(error, defaultMessage);
  toast({
    variant: "destructive",
    title: message,
  });
}

/**
 * Custom hook for creating a mutations
 */
// eslint-disable-next-line @typescript-eslint/no-explicit-any
export function useCreateMutation<T extends (...args: any) => any>(
  mutationFn: T,
  defaultSuccessMessage: string,
  defaultErrorMessage: string,
  callbackFn: () => void = () => {}
) {
  const { toast } = useToast();

  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  type ParamType<V extends (...args: any[]) => any> =
    Parameters<V>[0] extends undefined ? void : Parameters<V>[0];

  return useMutation<Awaited<ReturnType<T>>, AxiosError, ParamType<T>>({
    mutationFn,
    onSuccess: (message) => {
      callbackFn();
      toast({
        variant: "success",
        title:
          typeof message === "string" && message.trim().length !== 0
            ? message
            : defaultSuccessMessage,
      });
    },
    onError: (error) => handleMutationError(error, defaultErrorMessage, toast),
  });
}
