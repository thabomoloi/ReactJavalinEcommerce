/* eslint-disable @typescript-eslint/no-explicit-any */
import { BrowserRouter as Router } from "react-router-dom";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import SignInPage from "./page";
import { cleanup, render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { useAuth } from "@/hooks/use-auth";

vi.mock("@/hooks/use-auth");

describe("SignInPage", () => {
  const mockSignIn = vi.fn();

  beforeEach(() => {
    vi.clearAllMocks();
    vi.mocked(useAuth).mockReturnValue({
      signIn: mockSignIn,
      isAuthenticated: false,
      isLoading: false,
      isPending: false,
    } as any);
  });

  afterEach(() => {
    cleanup();
  });

  const renderComponent = () =>
    render(
      <Router>
        <SignInPage />
      </Router>
    );

  it("displays a link to sign in if the user does not have an account", () => {
    renderComponent();
    expect(screen.getByText(/Forgot Password?/i)).toBeInTheDocument();
    expect(screen.getByText(/Don't have an account?/i)).toBeInTheDocument();
    expect(screen.getByText(/Sign up/i)).toBeInTheDocument();
  });

  it("renders the sign-in form with inputs", () => {
    renderComponent();
    expect(screen.getByLabelText(/email/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/password/i)).toBeInTheDocument();
    expect(
      screen.getByRole("button", { name: /sign in/i })
    ).toBeInTheDocument();
  });

  it("disables the form fields and button when loading or pending", () => {
    vi.mocked(useAuth).mockReturnValue({
      signUp: mockSignIn,
      isAuthenticated: false,
      isLoading: true,
      isPending: true,
    } as any);

    renderComponent();

    expect(screen.getByLabelText(/email/i)).toBeDisabled();
    expect(screen.getByLabelText(/password/i)).toBeDisabled();
    expect(screen.getByRole("button", { name: /sign in/i })).toBeDisabled();
  });

  it("submits the form with valid inputs", async () => {
    renderComponent();

    const user = userEvent.setup();
    await user.type(screen.getByLabelText(/email/i), "john.doe@test.com");
    await user.type(screen.getByLabelText(/password/i), "Password123@");

    // Submit the form
    await user.click(screen.getByRole("button", { name: /sign in/i }));

    expect(mockSignIn).toHaveBeenCalled();
  });
});
