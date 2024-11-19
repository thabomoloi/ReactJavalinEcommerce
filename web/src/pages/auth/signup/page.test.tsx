/* eslint-disable @typescript-eslint/no-explicit-any */
import { BrowserRouter as Router } from "react-router-dom";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import SignUpPage from "./page";
import { render, screen, cleanup } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { useAuth } from "@/hooks/use-auth";

vi.mock("@/hooks/use-auth");

describe("SignUpPage", () => {
  const mockSignUp = vi.fn();

  beforeEach(() => {
    vi.clearAllMocks();
    vi.mocked(useAuth).mockReturnValue({
      signUp: mockSignUp,
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
        <SignUpPage />
      </Router>
    );

  it("displays links to terms and privacy policy", () => {
    renderComponent();
    expect(screen.getByText(/Terms of Service/i)).toBeInTheDocument();
    expect(screen.getByText(/Privacy Policy/i)).toBeInTheDocument();
  });

  it("displays a link to sign in if the user already has an account", () => {
    renderComponent();
    expect(screen.getByText(/Already have an account?/i)).toBeInTheDocument();
    expect(screen.getByText(/Sign in/i)).toBeInTheDocument();
  });

  it("renders the sign-up form with inputs", () => {
    renderComponent();
    expect(screen.getByLabelText(/name/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/email/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/password/i)).toBeInTheDocument();
    expect(
      screen.getByRole("button", { name: /sign up/i })
    ).toBeInTheDocument();
  });

  it("disables the form fields and button when loading or pending", () => {
    vi.mocked(useAuth).mockReturnValue({
      signUp: mockSignUp,
      isAuthenticated: false,
      isLoading: true,
      isPending: true,
    } as any);

    renderComponent();

    expect(screen.getByLabelText(/name/i)).toBeDisabled();
    expect(screen.getByLabelText(/email/i)).toBeDisabled();
    expect(screen.getByLabelText(/password/i)).toBeDisabled();
    expect(screen.getByRole("button", { name: /sign up/i })).toBeDisabled();
  });

  it("submits the form with valid inputs", async () => {
    renderComponent();

    const user = userEvent.setup();
    await user.type(screen.getByLabelText(/name/i), "John Doe");
    await user.type(screen.getByLabelText(/email/i), "john.doe@test.com");
    await user.type(screen.getByLabelText(/password/i), "Password123@");

    // Submit the form
    await user.click(screen.getByRole("button", { name: /sign up/i }));

    expect(mockSignUp).toHaveBeenCalled();
  });
});
