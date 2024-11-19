/* eslint-disable @typescript-eslint/no-explicit-any */
import { useAccount } from "@/hooks/use-account";
import { cleanup, render, screen } from "@testing-library/react";
import {
  BrowserRouter as Router,
  useNavigate,
  useParams,
} from "react-router-dom";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import ResetPasswordPage from "./page";
import userEvent from "@testing-library/user-event";

vi.mock("@/hooks/use-account");

vi.mock(import("react-router-dom"), async (importOriginal) => {
  const actual = await importOriginal();
  return {
    ...actual,
    useNavigate: vi.fn(),
    useParams: vi.fn(),
  };
});

describe("ResetPasswordPage", () => {
  const mockResetPassword = vi.fn();
  const mockNavigate = vi.fn();

  beforeEach(() => {
    vi.clearAllMocks();
    vi.mocked(useAccount).mockReturnValue({
      resetPassword: mockResetPassword,
      isPending: false,
    } as any);
    vi.mocked(useNavigate).mockReturnValue(mockNavigate);
    vi.mocked(useParams).mockReturnValue({ token: "test-token" });
  });

  afterEach(() => {
    cleanup();
  });

  const renderComponent = () =>
    render(
      <Router>
        <ResetPasswordPage />
      </Router>
    );

  it("renders the password input field and submit button", () => {
    renderComponent();

    expect(screen.getByLabelText(/new password/i)).toBeInTheDocument();
    expect(
      screen.getByRole("button", { name: /reset password/i })
    ).toBeInTheDocument();
  });

  it("disables the input field and button when `isPending` is true", () => {
    vi.mocked(useAccount).mockReturnValue({
      resetPassword: mockResetPassword,
      isPending: true,
    } as any);

    renderComponent();

    expect(screen.getByLabelText(/new password/i)).toBeDisabled();
    expect(
      screen.getByRole("button", { name: /reset password/i })
    ).toBeDisabled();
  });

  it("calls `resetPassword` with correct token and password", async () => {
    renderComponent();

    const user = userEvent.setup();
    await user.type(screen.getByLabelText(/new password/i), "newPassword123@");
    await user.click(screen.getByRole("button", { name: /reset password/i }));

    expect(mockResetPassword).toHaveBeenCalledWith(
      {
        token: "test-token",
        data: { password: "newPassword123@" },
      },
      { onSuccess: expect.any(Function) }
    );
  });

  it("navigates to sign-in page after successful reset", async () => {
    mockResetPassword.mockImplementation((_data, { onSuccess }) => {
      onSuccess();
    });

    renderComponent();
    const user = userEvent.setup();
    await user.type(screen.getByLabelText(/new password/i), "newPassword123@");
    await user.click(screen.getByRole("button", { name: /reset password/i }));

    expect(mockNavigate).toHaveBeenCalledWith("/auth/signin");
  });

  it("does not call `resetPassword` if no token is provided", async () => {
    vi.mocked(useParams).mockReturnValue({ token: undefined });

    renderComponent();

    const user = userEvent.setup();
    await user.type(screen.getByLabelText(/new password/i), "newPassword123@");
    await user.click(screen.getByRole("button", { name: /reset password/i }));

    expect(mockResetPassword).not.toHaveBeenCalled();
  });
});
