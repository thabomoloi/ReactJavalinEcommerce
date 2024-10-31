import axios from "axios";
import { describe, it, expect, vi, afterEach } from "vitest";
import { getCurrentUser, refreshJWT } from "./auth";
import { UserImpl } from "../models/user";
import { Role } from "../models/types";
import { REFRESH_TOKEN_URL } from "@/lib/urls";

vi.mock("axios");

describe("Auth Service", () => {
  afterEach(() => {
    vi.clearAllMocks();
  });

  describe("getCurrentUser", () => {
    it("returns a User instance on successful instance", async () => {
      const mockUserData = {
        id: 1,
        name: "John Doe",
        email: "john@example.com",
        role: "ADMIN",
      };

      vi.mocked(axios, true).get.mockResolvedValue({
        status: 200,
        data: mockUserData,
      });

      const user = await getCurrentUser();

      expect(user).toBeInstanceOf(UserImpl);
      expect(user?.id).toBe(mockUserData.id);
      expect(user?.name).toBe(mockUserData.name);
      expect(user?.email).toBe(mockUserData.email);
      expect(user?.role).toBe(Role.ADMIN);
    });

    it("returns null on a failed response", async () => {
      vi.mocked(axios, true).get.mockResolvedValue({ status: 404 });
      const user = await getCurrentUser();
      expect(user).toBeNull();
    });
  });

  describe("refreshJWT", () => {
    it("calls axios.post with REFRESH_TOKEN_URL", async () => {
      vi.mocked(axios, true).post.mockResolvedValue({ status: 200 });
      await refreshJWT();
      expect(axios.post).toHaveBeenCalledWith(REFRESH_TOKEN_URL);
    });

    it("throws an error if axios.post fails", async () => {
      vi.mocked(axios, true).post.mockRejectedValue(
        new Error("Token refresh failed")
      );
      await expect(refreshJWT()).rejects.toThrow("Token refresh failed");
    });
  });
});
