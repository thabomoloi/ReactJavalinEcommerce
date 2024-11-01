import { describe, it, expect, vi, beforeEach, afterEach } from "vitest";
import { useAuth } from "./auth";
import { getCurrentUser, refreshJWT } from "../data/api/auth";
import { Role, User } from "../data/models/types";
import { act } from "react";
import { AxiosError, AxiosResponse } from "axios";

vi.mock("../data/api/auth");

describe("useAuth Store", () => {
  beforeEach(() => {
    useAuth.setState({ currentUser: null, isAuthenticated: false });
  });

  afterEach(() => {
    vi.clearAllMocks();
    vi.resetAllMocks();
  });

  describe("fetchCurrentUser", () => {
    it("sets user data and isAuthenticated to true when getCurrentUser is successful", async () => {
      const mockUser = {
        id: 1,
        name: "John Doe",
        email: "john.doe@test.com",
        role: Role.ADMIN,
      };

      vi.mocked(getCurrentUser).mockResolvedValueOnce(mockUser);

      await act(async () => {
        await useAuth.getState().fetchCurrentUser();
      });

      const state = useAuth.getState();
      expect(state.currentUser).toStrictEqual(mockUser);
      expect(state.isAuthenticated).toBe(true);
    });

    it("sets currentUser to null and isAuthenticated to false when getCurrentUser returns null", async () => {
      vi.mocked(getCurrentUser).mockResolvedValueOnce(null);

      await act(async () => {
        await useAuth.getState().fetchCurrentUser();
      });

      const state = useAuth.getState();
      expect(state.currentUser).toBeNull();
      expect(state.isAuthenticated).toBe(false);
    });
  });

  describe("verifyAuthentication", () => {
    it("calls refreshJWT and fetchCurrentUser on 401 error, and sets isAuthenticated to true", async () => {
      const mockUser: User = {
        id: 1,
        name: "John Doe",
        email: "john.doe@test.com",
        role: Role.USER,
      };

      const axiosError = new AxiosError("", "", undefined, null, {
        status: 401,
      } as AxiosResponse);

      vi.mocked(getCurrentUser)
        .mockRejectedValueOnce(axiosError)
        .mockResolvedValueOnce(mockUser);
      vi.mocked(refreshJWT).mockResolvedValueOnce(undefined);

      await act(async () => {
        await useAuth.getState().verifyAuthentication();
      });

      const state = useAuth.getState();
      expect(refreshJWT).toHaveBeenCalled();
      expect(state.isAuthenticated).toBe(true);
      expect(state.currentUser).not.toBeNull();
      expect(state.currentUser).toStrictEqual(mockUser);
    });

    it("sets isAuthenticated to false if refreshJWT fails", async () => {
      const axiosError = new AxiosError("", "", undefined, null, {
        status: 401,
      } as AxiosResponse);

      vi.mocked(getCurrentUser).mockRejectedValueOnce(axiosError);
      vi.mocked(refreshJWT).mockRejectedValueOnce(
        new Error("Token refresh failed")
      );

      await act(async () => await useAuth.getState().verifyAuthentication());

      const state = useAuth.getState();
      expect(getCurrentUser).toHaveBeenCalled();
      expect(refreshJWT).toHaveBeenCalled();
      expect(state.isAuthenticated).toBe(false);
      expect(state.currentUser).toBeNull();
    });

    it("sets isAuthenticated to false if unexpected error", async () => {
      const error = new Error("Network error");
      vi.mocked(getCurrentUser).mockRejectedValueOnce(error);

      await act(async () => await useAuth.getState().verifyAuthentication());

      const state = useAuth.getState();
      expect(getCurrentUser).toHaveBeenCalled();
      expect(refreshJWT).not.toHaveBeenCalled();
      expect(state.isAuthenticated).toBe(false);
      expect(state.currentUser).toBeNull();
    });
  });
});
