/* eslint-disable @typescript-eslint/no-explicit-any */
import { getCurrentUser, refreshJWT } from "@/lib/data/api/user";
import { Role, User } from "@/lib/data/models/types";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { describe, it, expect, vi, beforeEach } from "vitest";
import { useAuth } from "./use-auth";
import { act, renderHook, waitFor } from "@testing-library/react";
import { useCreateMutation } from "./helpers/use-create-mutation";
import { AxiosError, AxiosResponse } from "axios";

vi.mock("@/lib/data/api/user");

vi.mock("./helpers/use-create-mutation");

const mockQueryClient = new QueryClient();

function wrapper({ children }: { children: React.ReactNode }) {
  return (
    <QueryClientProvider client={mockQueryClient}>
      {children}
    </QueryClientProvider>
  );
}

describe("useAuth Hook", () => {
  const mockMutate = vi.fn();
  const mockMutationState = {
    mutate: mockMutate,
    isPending: false,
  };

  beforeEach(() => {
    vi.clearAllMocks();
    mockQueryClient.clear();

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

  it("should return current user if authenticated", async () => {
    const user: User = {
      id: 1,
      name: "John Doe",
      email: "john.doe@test.com",
      role: Role.USER,
    };
    vi.mocked(getCurrentUser).mockResolvedValue(user);

    const { result } = renderHook(() => useAuth(), { wrapper });

    await waitFor(() => expect(result.current.isLoading).toBe(false));

    expect(result.current.currentUser).toEqual(user);
    expect(result.current.isAuthenticated).toBe(true);
  });

  it("should handle unauthenticated user and retry with refreshJWT", async () => {
    const error = new AxiosError("Unauthorized");
    error.response = { status: 401 } as AxiosResponse;

    const user: User = {
      id: 1,
      name: "John Doe",
      email: "john.doe@test.com",
      role: Role.USER,
    };

    vi.mocked(getCurrentUser)
      .mockRejectedValueOnce(error)
      .mockResolvedValueOnce(user);

    const { result } = renderHook(() => useAuth(), { wrapper });
    await waitFor(() => expect(result.current.isLoading).toBe(false));

    expect(refreshJWT).toHaveBeenCalled();
    expect(getCurrentUser).toHaveBeenCalledTimes(2);
    expect(result.current.currentUser).toEqual(user);
  });

  it("should return null if not authenticated", async () => {
    const error = new AxiosError("Unauthorized");
    error.response = { status: 401 } as AxiosResponse;

    vi.mocked(getCurrentUser).mockRejectedValue(error);
    vi.mocked(refreshJWT).mockRejectedValue(new Error("Token expired"));

    const { result } = renderHook(() => useAuth(), { wrapper });

    await waitFor(() => expect(result.current.isLoading).toBe(false));
    expect(result.current.currentUser).toBeNull();
    expect(result.current.isAuthenticated).toBe(false);
  });

  it("should successfully sign in", () => {
    const mockMutate = vi.fn();
    vi.mocked(useCreateMutation).mockReturnValue({ mutate: mockMutate } as any);

    const { result } = renderHook(() => useAuth(), { wrapper });

    const signInData = {
      email: "john.doe@test.com",
      password: "password",
    };
    act(() => {
      result.current.signIn(signInData);
    });

    expect(mockMutate).toHaveBeenCalledWith(signInData);
  });

  it("should successfully sign up", async () => {
    const mockMutate = vi.fn();
    vi.mocked(useCreateMutation).mockReturnValue({ mutate: mockMutate } as any);

    const { result } = renderHook(() => useAuth(), { wrapper });

    const signUpData = {
      name: "John Doe",
      email: "john.doe@test.com",
      password: "password",
    };

    act(() => {
      result.current.signUp(signUpData);
    });

    expect(mockMutate).toHaveBeenCalledWith(signUpData);
  });

  it("should successfully sign out", async () => {
    const mockMutate = vi.fn();
    vi.mocked(useCreateMutation).mockReturnValue({ mutate: mockMutate } as any);

    const { result } = renderHook(() => useAuth(), { wrapper });

    act(() => {
      result.current.signOut();
    });

    expect(mockMutate).toHaveBeenCalled();
  });

  it("should handle pending state correctly", () => {
    vi.mocked(useCreateMutation).mockReturnValue({ mutate: mockMutate } as any);

    const { result } = renderHook(() => useAuth(), { wrapper });

    waitFor(() => {
      result.current.signOut();
      expect(result.current.isPending).toBe(true);
    });
  });
});
