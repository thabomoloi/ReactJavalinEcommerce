import {
  BrowserRouter as Router,
  useActionData,
  useNavigate,
  useSubmit,
} from "react-router-dom";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import SignUpPage from "./signup-page";
import { render, screen, waitFor, cleanup } from "@testing-library/react";
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

describe("SignUpPage", () => {
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

  it("displays links to terms and privacy policy", () => {
    render(
      <Router>
        <SignUpPage />
      </Router>
    );
    expect(screen.getByText(/Terms of Service/i)).toBeInTheDocument();
    expect(screen.getByText(/Privacy Policy/i)).toBeInTheDocument();
  });

  it("displays a link to sign in if the user already has an account", () => {
    render(
      <Router>
        <SignUpPage />
      </Router>
    );
    expect(screen.getByText(/Already have an account?/i)).toBeInTheDocument();
    expect(screen.getByText(/Sign in/i)).toBeInTheDocument();
  });

  it("renders the sign-up form with inputs", () => {
    render(
      <Router>
        <SignUpPage />
      </Router>
    );
    expect(screen.getByLabelText(/name/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/email/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/password/i)).toBeInTheDocument();
    expect(
      screen.getByRole("button", { name: /sign up/i })
    ).toBeInTheDocument();
  });

  it("submits the form with valid inputs", async () => {
    render(
      <Router>
        <SignUpPage />
      </Router>
    );
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

  it("displays a success toast and navigates on successful sign-up", async () => {
    vi.mocked(useActionData).mockReturnValue({
      error: false,
      message: "Sign-up successful!",
    });

    render(
      <Router>
        <SignUpPage />
      </Router>
    );

    await waitFor(() => {
      expect(toastMock).toHaveBeenCalledWith({
        title: "Sign-up successful!",
        variant: "success",
      });
      expect(navigateMock).toHaveBeenCalledWith("/auth/signin");
    });
  });

  it("displays an error toast on sign-up failure", async () => {
    vi.mocked(useActionData).mockReturnValue({
      error: true,
      message: "Sign-up failed",
    });

    render(
      <Router>
        <SignUpPage />
      </Router>
    );

    await waitFor(() => {
      expect(toastMock).toHaveBeenCalledWith({
        title: "Sign-up failed",
        variant: "destructive",
      });
      expect(navigateMock).not.toHaveBeenCalledWith();
    });
  });
});
