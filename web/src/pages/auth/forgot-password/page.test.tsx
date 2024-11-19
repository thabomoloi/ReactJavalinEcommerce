/* eslint-disable @typescript-eslint/no-explicit-any */
import { BrowserRouter as Router } from "react-router-dom";
import { useAccount } from "@/hooks/use-account";
import { describe, it, vi, expect, beforeEach } from "vitest";
import ForgotPasswordPage from "./page";
import { render, screen, waitFor } from "@testing-library/react";
import { userEvent } from "@testing-library/user-event";

vi.mock("@/hooks/use-account");

describe("ForgotPasswordPage", () => {
  const mockSendResetPasswordLink = vi.fn();
  beforeEach(() => {
    vi.clearAllMocks();
    vi.mocked(useAccount).mockReturnValue({
      sendResetPasswordLink: mockSendResetPasswordLink,
      isPending: false,
    } as any);
  });

  const renderComponent = () =>
    render(
      <Router>
        <ForgotPasswordPage />
      </Router>
    );

  it("renders the email input field and submit button", () => {
    renderComponent();

    expect(screen.getByLabelText(/enter your email/i)).toBeInTheDocument();
    expect(
      screen.getByRole("button", { name: /send password reset link/i })
    ).toBeInTheDocument();
  });

  it("disables the email field and button when `isPending` is true", () => {
    vi.mocked(useAccount).mockReturnValue({
      sendResetPasswordLink: mockSendResetPasswordLink,
      isPending: true,
    } as any);
    renderComponent();

    expect(screen.getByLabelText(/enter your email/i)).toBeDisabled();
    expect(
      screen.getByRole("button", { name: /send password reset link/i })
    ).toBeDisabled();
  });

  it("calls `sendResetPasswordLink` with a valid email address", async () => {
    renderComponent();

    const user = userEvent.setup();
    await user.type(
      screen.getByLabelText(/enter your email/i),
      "john.doe@test.com"
    );

    user.click(
      screen.getByRole("button", { name: /send password reset link/i })
    );

    await waitFor(() =>
      expect(mockSendResetPasswordLink).toHaveBeenCalledWith({
        email: "john.doe@test.com",
      })
    );
  });
});
