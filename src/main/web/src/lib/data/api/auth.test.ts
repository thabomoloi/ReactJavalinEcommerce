import axios from "axios";
import { describe, it, expect, vi, afterEach } from "vitest";
import { getCurrentUser, refreshJWT, signIn, signOut, signUp } from "./auth";
import { UserImpl } from "../models/user";
import { Role } from "../models/types";
import {
  REFRESH_TOKEN_URL,
  SIGN_IN_URL,
  SIGN_OUT_URL,
  SIGN_UP_URL,
} from "@/lib/urls";

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
        email: "john@test.com",
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

  describe("signUp", () => {
    it("returns response data on successful sign up", async () => {
      const mockResponse = "User signed up successfully";
      vi.mocked(axios, true).post.mockResolvedValue({ data: mockResponse });

      const data = {
        name: "John Doe",
        email: "john.doe@test.com",
        password: "Password123!",
      };

      const result = await signUp(data);
      expect(result).toBe(mockResponse);
      expect(axios.post).toHaveBeenCalledWith(SIGN_UP_URL, data);
    });

    it("throws an error if signUp fails", async () => {
      vi.mocked(axios, true).post.mockRejectedValue(
        new Error("Sign up failed")
      );
      await expect(
        signUp({
          name: "John Doe",
          email: "john.doe@test.com",
          password: "Password123!",
        })
      ).rejects.toThrow("Sign up failed");
    });
  });

  describe("signUp", () => {
    it("returns response data on successful sign in", async () => {
      const mockResponse = "User signed in successfully";
      vi.mocked(axios, true).post.mockResolvedValue({ data: mockResponse });

      const data = {
        email: "john.doe@test.com",
        password: "Password123!",
      };

      const result = await signIn(data);
      expect(result).toBe(mockResponse);
      expect(axios.post).toHaveBeenCalledWith(SIGN_IN_URL, data);
    });

    it("throws an error if signIn fails", async () => {
      vi.mocked(axios, true).post.mockRejectedValue(
        new Error("Sign in failed")
      );
      await expect(
        signIn({
          email: "john.doe@test.com",
          password: "Password123!",
        })
      ).rejects.toThrow("Sign in failed");
    });
  });

  describe("signOut", () => {
    it("returns response data on successful sign out", async () => {
      const mockResponse = "User signed out successfully";
      vi.mocked(axios, true).delete.mockResolvedValue({ data: mockResponse });

      const result = await signOut();
      expect(result).toBe(mockResponse);
      expect(axios.delete).toHaveBeenCalledWith(SIGN_OUT_URL);
    });

    it("throws an error if signOut fails", async () => {
      vi.mocked(axios, true).delete.mockRejectedValue(
        new Error("Sign out failed")
      );
      await expect(signOut()).rejects.toThrow("Sign out failed");
    });
  });
});
