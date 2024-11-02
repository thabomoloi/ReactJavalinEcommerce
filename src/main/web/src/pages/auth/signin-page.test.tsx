import {
  BrowserRouter as Router,
  useActionData,
  useNavigate,
  useSubmit,
} from "react-router-dom";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import SignInPage from "./signin-page";
import { cleanup, render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";

vi.mock(import("react-router-dom"), async (importOriginal) => {
  const actual = await importOriginal();
  return {
    ...actual,
    useSubmit: vi.fn(),
    useNavigate: vi.fn(),
    useActionData: vi.fn(),
  };
});

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

describe("SignInPage", () => {
  let navigateMock: typeof vi.fn;
  beforeEach(() => {
    navigateMock = vi.fn();
    vi.mocked(useNavigate).mockReturnValue(navigateMock);
    vi.mocked(useActionData).mockReturnValue(undefined);
  });

  afterEach(() => {
    vi.clearAllMocks();
    cleanup();
  });

  it("displays a link to sign in if the user does not have an account", () => {
    render(
      <Router>
        <SignInPage />
      </Router>
    );
    expect(screen.getByText(/Forgot Password?/i)).toBeInTheDocument();
    expect(screen.getByText(/Don't have an account?/i)).toBeInTheDocument();
    expect(screen.getByText(/Sign up/i)).toBeInTheDocument();
  });

  it("renders the sign-in form with inputs", () => {
    render(
      <Router>
        <SignInPage />
      </Router>
    );
    expect(screen.getByLabelText(/email/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/password/i)).toBeInTheDocument();
    expect(
      screen.getByRole("button", { name: /sign in/i })
    ).toBeInTheDocument();
  });

  it("submits the form with valid inputs", async () => {
    render(
      <Router>
        <SignInPage />
      </Router>
    );
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

  it("displays a success toast and navigates on successful sign-in", async () => {
    vi.mocked(useActionData).mockReturnValue({
      error: false,
      message: "Sign-in successful!",
    });

    render(
      <Router>
        <SignInPage />
      </Router>
    );

    await waitFor(() => {
      expect(toastMock).toHaveBeenCalledWith({
        title: "Sign-in successful!",
        variant: "success",
      });
      expect(navigateMock).toHaveBeenCalledWith("/");
    });
  });

  it("displays an error toast on sign-in failure", async () => {
    vi.mocked(useActionData).mockReturnValue({
      error: true,
      message: "Sign-in failed",
    });

    render(
      <Router>
        <SignInPage />
      </Router>
    );

    await waitFor(() => {
      expect(toastMock).toHaveBeenCalledWith({
        title: "Sign-in failed",
        variant: "destructive",
      });
      expect(navigateMock).not.toHaveBeenCalledWith();
    });
  });
});
