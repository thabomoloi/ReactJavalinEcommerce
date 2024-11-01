import { BrowserRouter as Router, useSubmit } from "react-router-dom";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import SignUpPage from "./signup-page";
import { render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";

vi.mock(import("react-router-dom"), async (importOriginal) => {
  const actual = await importOriginal();
  return {
    ...actual,
    useSubmit: vi.fn(),
  };
});

describe("SignUpPage", () => {
  beforeEach(() => {
    render(
      <Router>
        <SignUpPage />
      </Router>
    );
  });

  afterEach(() => {
    vi.clearAllMocks();
  });

  it("renders the sign-up form with inputs", () => {
    expect(screen.getByLabelText(/name/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/email/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/password/i)).toBeInTheDocument();
    expect(
      screen.getByRole("button", { name: /sign up/i })
    ).toBeInTheDocument();
  });

  it("submits the form with valid inputs", async () => {
    const mockSubmit = vi.fn();
    vi.mocked(useSubmit).mockImplementation(() => mockSubmit);

    const user = userEvent.setup();
    await user.type(screen.getByLabelText(/name/i), "John Doe");
    await user.type(screen.getByLabelText(/email/i), "john.doe@example.com");
    await user.type(screen.getByLabelText(/password/i), "Password123@");

    // Submit the form
    await user.click(screen.getByRole("button", { name: /sign up/i }));

    await waitFor(() => {
      expect(mockSubmit).toHaveBeenCalledWith(
        {
          name: "John Doe",
          email: "john.doe@example.com",
          password: "Password123@",
        },
        {
          method: "post",
          action: "/auth/signup",
          encType: "application/json",
        }
      );
    });
  });

  it("displays links to terms and privacy policy", () => {
    expect(screen.getByText(/Terms of Service/i)).toBeInTheDocument();
    expect(screen.getByText(/Privacy Policy/i)).toBeInTheDocument();
  });

  it("displays a link to sign in if the user already has an account", () => {
    expect(screen.getByText(/Already have an account?/i)).toBeInTheDocument();
    expect(screen.getByText(/Sign in/i)).toBeInTheDocument();
  });
});
