/* eslint-disable @typescript-eslint/no-explicit-any */
import { describe, it, expect, vi, beforeEach, afterEach } from "vitest";
import { useCreateMutation } from "./helpers/use-create-mutation";
import { act, renderHook } from "@testing-library/react";
import { useAccount } from "./use-account";
import { updateProfile } from "@/lib/data/api/user";

const mockInvalidateQueries = vi.fn();

vi.mock("@tanstack/react-query", () => ({
  useQueryClient: () => ({ invalidateQueries: mockInvalidateQueries }),
}));

vi.mock("./helpers/use-create-mutation", () => ({
  useCreateMutation: vi.fn(),
}));

vi.mock("@/lib/data/api/user", () => ({
  confirmAccount: vi.fn(),
  deleteAccount: vi.fn(),
  resetPassword: vi.fn(),
  sendConfirmationLink: vi.fn(),
  sendResetPasswordLink: vi.fn(),
  updateProfile: vi.fn(),
}));

describe("useAccount", () => {
  const mockMutate = vi.fn();
  const mockMutationState = {
    mutate: mockMutate,
    isPending: false,
  };

  beforeEach(() => {
    vi.clearAllMocks();

    vi.mocked(useCreateMutation).mockImplementation(
      (mutationFn, _successMessage, _errorMessage, callbackFn) =>
        ({
          ...mockMutationState,
          mutate: (params: unknown) => {
            mutationFn(params);
            if (callbackFn) callbackFn();
          },
        } as any)
    );
  });

  afterEach(() => {
    vi.clearAllMocks();
  });

  it("should initialize all mutations", () => {
    const { result } = renderHook(() => useAccount());

    expect(result.current.updateAccount).toBeInstanceOf(Function);
    expect(result.current.deleteAccount).toBeInstanceOf(Function);
    expect(result.current.resetPassword).toBeInstanceOf(Function);
    expect(result.current.sendConfirmationLink).toBeInstanceOf(Function);
    expect(result.current.confirmAccount).toBeInstanceOf(Function);
    expect(result.current.sendResetPasswordLink).toBeInstanceOf(Function);
  });

  it("should call `invalidateQueries` on deleteAccount success", () => {
    const { result } = renderHook(() => useAccount());

    act(() => {
      result.current.deleteAccount(1);
    });

    expect(mockInvalidateQueries).toHaveBeenCalledWith({
      queryKey: ["currentUser"],
    });
  });

  it("should trigger the correct mutation functions", () => {
    const { result } = renderHook(() => useAccount());

    act(() => {
      result.current.updateAccount({
        id: 1,
        name: "John Doe",
        email: "john.doe@test.com",
      });
    });

    expect(updateProfile).toHaveBeenCalled();
  });

  it("should combine isPending states correctly", () => {
    const pendingState = { ...mockMutationState, isPending: true };

    vi.mocked(useCreateMutation).mockImplementationOnce(
      () => pendingState as any
    );

    const { result } = renderHook(() => useAccount());

    expect(result.current.isPending).toBe(true);
  });
});
