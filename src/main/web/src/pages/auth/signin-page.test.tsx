import { BrowserRouter as Router, useSubmit } from "react-router-dom";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import SignInPage from "./signin-page";
import { render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";

vi.mock(import("react-router-dom"), async (importOriginal) => {
  const actual = await importOriginal();
  return {
    ...actual,
    useSubmit: vi.fn(),
  };
});

describe("SignInPage", () => {
  beforeEach(() => {
    render(
      <Router>
        <SignInPage />
      </Router>
    );
  });

  afterEach(() => {
    vi.clearAllMocks();
  });

  it("renders the sign-in form with inputs", () => {
    expect(screen.getByLabelText(/email/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/password/i)).toBeInTheDocument();
    expect(
      screen.getByRole("button", { name: /sign in/i })
    ).toBeInTheDocument();
  });

  it("submits the form with valid inputs", async () => {
    const mockSubmit = vi.fn();
    vi.mocked(useSubmit).mockImplementation(() => mockSubmit);

    const user = userEvent.setup();
    await user.type(screen.getByLabelText(/email/i), "john.doe@example.com");
    await user.type(screen.getByLabelText(/password/i), "Password123@");

    // Submit the form
    await user.click(screen.getByRole("button", { name: /sign in/i }));

    await waitFor(() => {
      expect(mockSubmit).toHaveBeenCalledWith(
        {
          email: "john.doe@example.com",
          password: "Password123@",
        },
        {
          method: "post",
          action: "/auth/signin",
          encType: "application/json",
        }
      );
    });
  });

  it("displays a link to sign in if the user does not have an account", () => {
    expect(screen.getByText(/Forgot Password?/i)).toBeInTheDocument();
    expect(screen.getByText(/Don't have an account?/i)).toBeInTheDocument();
    expect(screen.getByText(/Sign up/i)).toBeInTheDocument();
  });
});
