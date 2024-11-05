import { useAuth } from "@/lib/store/auth";
import { useNavigation } from "react-router-dom";

export function useLoading() {
  const navigation = useNavigation();
  const auth = useAuth();
  return {
    isLoading:
      auth.isLoading ||
      navigation.state == "loading" ||
      navigation.state == "submitting",
    isSubmitting: navigation.state == "submitting",
  };
}
