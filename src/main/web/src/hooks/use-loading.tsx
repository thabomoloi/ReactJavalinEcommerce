import { useAuth } from "./use-auth";

export function useLoading() {
  const auth = useAuth();
  return auth.isLoading;
}
