import { afterEach, describe, expect, it } from "vitest";
import { render, screen, cleanup } from "@testing-library/react";
import { PasswordField } from "./password-field";
import { Input } from "./input";
import userEvent from "@testing-library/user-event";
import "@testing-library/jest-dom";

describe("PasswordField Component", () => {
  afterEach(() => {
    cleanup();
  });

  it("renders input field of type 'password'", () => {
    render(
      <PasswordField>
        <Input type="password" placeholder="Password" />
      </PasswordField>
    );
    expect(screen.getByPlaceholderText("Password")).toBeInTheDocument();
  });

  it("toggles password visibility when icon is clicked", async () => {
    const user = userEvent.setup();

    render(
      <PasswordField>
        <Input type="password" placeholder="Password" />
      </PasswordField>
    );

    const passwordInput = screen.getByPlaceholderText("Password");
    const toggleButton = screen.getByRole("button");

    expect(passwordInput).toHaveAttribute("type", "password");

    await user.click(toggleButton);
    expect(passwordInput).toHaveAttribute("type", "text");

    await user.click(toggleButton);
    expect(passwordInput).toHaveAttribute("type", "password");
  });

  it("should render the PasswordField with additional class names", () => {
    render(
      <PasswordField className="extra-class">
        <input />
      </PasswordField>
    );

    const container = screen.getByRole("button").parentElement;
    expect(container).toHaveClass("flex items-center extra-class");
  });
});
