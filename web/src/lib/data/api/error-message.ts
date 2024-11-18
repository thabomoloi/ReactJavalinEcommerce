import { AxiosError } from "axios";

export function getErrorMessage(error: Error, defaultMessage: string) {
  if (error instanceof AxiosError) {
    const data = error.response?.data;
    if (data.title) return data.title;
  }
  return defaultMessage;
}
