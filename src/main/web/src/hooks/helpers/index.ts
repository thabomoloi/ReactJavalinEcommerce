import { getErrorMessage } from "@/lib/data/api/error-message";
import { useToast } from "../use-toast";

// Reusable helper to handle errors and display toast messages
export function handleMutationError(
  error: Error,
  defaultMessage: string,
  toast: ReturnType<typeof useToast>["toast"]
): void {
  const message = getErrorMessage(error, defaultMessage);
  console.error(error);
  toast({
    variant: "destructive",
    title: message,
  });
}
