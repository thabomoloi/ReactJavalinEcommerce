import { describe, it, expect } from "vitest";
import { AxiosError, AxiosResponse } from "axios";
import { getErrorMessage } from "./error-message";

describe("getErrorMessage", () => {
  const defaultMessage = "An error occurred";

  it("should return the title from AxiosError response data", () => {
    const error = new AxiosError("Request failed");
    error.response = {
      data: { title: "Custom Error Title" },
      status: 400,
      statusText: "Bad Request",
    } as AxiosResponse;

    const result = getErrorMessage(error, defaultMessage);
    expect(result).toBe("Custom Error Title");
  });

  it("should return the default message if AxiosError has no title", () => {
    const error = new AxiosError("Request failed");
    error.response = {
      data: {},
      status: 400,
      statusText: "Bad Request",
    } as AxiosResponse;

    const result = getErrorMessage(error, defaultMessage);
    expect(result).toBe(defaultMessage);
  });

  it("should return the default message if error is not an AxiosError", () => {
    const error = new Error("Some other error");

    const result = getErrorMessage(error, defaultMessage);
    expect(result).toBe(defaultMessage);
  });
});
