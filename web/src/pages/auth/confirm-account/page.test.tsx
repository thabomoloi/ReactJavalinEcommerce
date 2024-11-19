/* eslint-disable @typescript-eslint/no-explicit-any */
import { useAccount } from "@/hooks/use-account";
import { cleanup, render, screen } from "@testing-library/react";
import {
  BrowserRouter as Router,
  useNavigate,
  useParams,
} from "react-router-dom";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import ConfirmAccountPage from "./page";
import { useAuth } from "@/hooks/use-auth";
import { Role } from "@/lib/data/models/types";
import userEvent from "@testing-library/user-event";

vi.mock("@/hooks/use-auth");
vi.mock("@/hooks/use-account");

vi.mock(import("react-router-dom"), async (importOriginal) => {
  const actual = await importOriginal();
  return {
    ...actual,
    useNavigate: vi.fn(),
    useParams: vi.fn(),
    Navigate: ({ to }: { to: string }) => <div>Redirected to {to}</div>,
  } as any;
});

describe("ConfirmAccountPage", () => {
  const mockConfirmAccount = vi.fn();
  const mockSendConfirmationLink = vi.fn();
  const mockNavigate = vi.fn();

  beforeEach(() => {
    vi.clearAllMocks();

    vi.mocked(useAuth).mockReturnValue({
      currentUser: {
        id: 123,
        name: "John Doe",
        email: "john.doe@test.com",
        role: Role.USER,
      },
      isAuthenticated: true,
      isLoading: false,
      isPending: false,
    } as any);

    vi.mocked(useAccount).mockReturnValue({
      confirmAccount: mockConfirmAccount,
      sendConfirmationLink: mockSendConfirmationLink,
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
        <ConfirmAccountPage />
      </Router>
    );

  it("renders the page title and content", () => {
    renderComponent();

    expect(screen.getByText(/confirm account/i)).toBeInTheDocument();
    expect(
      screen.getByText(
        /Your account has not yet been confirmed. Please check your email/i
      )
    ).toBeInTheDocument();
    expect(
      screen.getByRole("button", { name: /resend link/i })
    ).toBeInTheDocument();
  });

  it("redirects to the signin page if `currentUser` is null", () => {
    vi.mocked(useAuth).mockReturnValue({ currentUser: null } as any);

    renderComponent();

    expect(
      screen.getByText(/redirected to \/auth\/signin/i)
    ).toBeInTheDocument();
  });

  it("calls `confirmAccount` with the token on mount", () => {
    renderComponent();

    expect(mockConfirmAccount).toHaveBeenCalledWith("test-token");
    expect(mockNavigate).toHaveBeenCalledWith("/auth/confirm-account");
  });

  it("disables the resend button when `isPending` is true", () => {
    vi.mocked(useAccount).mockReturnValue({
      isPending: true,
      sendConfirmationLink: mockSendConfirmationLink,
      confirmAccount: mockConfirmAccount,
    } as any);

    renderComponent();

    const button = screen.getByRole("button", { name: /resend link/i });
    expect(button).toBeDisabled();
  });

  it("calls `sendConfirmationLink` with the current user ID when resend button is clicked", async () => {
    renderComponent();

    const button = screen.getByRole("button", { name: /resend link/i });
    const user = userEvent.setup();
    await user.click(button);

    expect(mockSendConfirmationLink).toHaveBeenCalledWith(123);
  });

  it("does not call `confirmAccount` if no token is provided", () => {
    vi.mocked(useParams).mockReturnValue({ token: undefined });

    renderComponent();

    expect(mockConfirmAccount).not.toHaveBeenCalled();
    expect(mockNavigate).not.toHaveBeenCalledWith("/auth/confirm-account");
  });
});
