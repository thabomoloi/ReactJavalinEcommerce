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
            : successMessage,
      });
    },
    onError: (error) => handleMutationError(error, errorMessage, toast),
  });
}
