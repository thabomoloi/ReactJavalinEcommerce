import { getErrorMessage } from "@/lib/data/api/error-message";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { describe, it, vi, expect, afterEach } from "vitest";
import { handleMutationError, useCreateMutation } from "./use-create-mutation";
import { cleanup, renderHook, waitFor } from "@testing-library/react";

vi.mock("@/lib/data/api/error-message", () => ({
  getErrorMessage: vi.fn(),
}));

const toastMock = vi.fn();

vi.mock(import("@/hooks/use-toast"), async (importOriginal) => {
  const actual = await importOriginal();
  return {
    ...actual,
    useToast: () => ({
      toast: toastMock,
      dismiss: vi.fn(),
      toasts: [],
    }),
  };
});

const queryClient = new QueryClient();

describe("Mutation creation helpers", () => {
  afterEach(() => {
    vi.clearAllMocks();
    cleanup();
  });

  describe("useCreateMutation", () => {
    it("should call the mutation function on success and show a success toast", async () => {
      const successMessage = "Mutation success message.";
      const mutationFn = vi.fn(async () => successMessage);
      const callbackFn = vi.fn();

      const { result } = renderHook(
        () =>
          useCreateMutation(
            mutationFn,
            "Default success message.",
            "Default error message.",
            callbackFn
          ),
        {
          wrapper: ({ children }) => (
            <QueryClientProvider client={queryClient}>
              {children}
            </QueryClientProvider>
          ),
        }
      );

      result.current.mutate();

      await waitFor(() => expect(callbackFn).toHaveBeenCalled());
      expect(toastMock).toHaveBeenCalledWith({
        variant: "success",
        title: successMessage,
      });
      expect(mutationFn).toHaveBeenCalled();
    });

    it("should call handleMutationError on mutation failure", async () => {
      const mutationFn = vi.fn(async () => {
        throw new Error("Test error");
      });
      const successMessage = "Operation successful!";
      const errorMessage = "Operation failed!";

      vi.mocked(getErrorMessage).mockReturnValue(errorMessage);

      const { result } = renderHook(
        () => useCreateMutation(mutationFn, successMessage, errorMessage),
        {
          wrapper: ({ children }) => (
            <QueryClientProvider client={queryClient}>
              {children}
            </QueryClientProvider>
          ),
        }
      );

      result.current.mutate(undefined);

      await waitFor(() =>
        expect(toastMock).toHaveBeenCalledWith({
          variant: "destructive",
          title: errorMessage,
        })
      );
      expect(getErrorMessage).toHaveBeenCalled();
    });
  });

  describe("handleMutationError", () => {
    it("should call toast with a destructive variant and error message", () => {
      const error = new Error("Test error");
      const defaultMessage = "Default error message";

      vi.mocked(getErrorMessage).mockReturnValue("Test error message");

      handleMutationError(error, defaultMessage, toastMock);

      expect(getErrorMessage).toHaveBeenCalledWith(error, defaultMessage);
      expect(toastMock).toHaveBeenCalledWith({
        variant: "destructive",
        title: "Test error message",
      });
    });
  });
});
