import { useLoading } from "@/hooks/use-loading";
import BarLoader from "react-spinners/BarLoader";

export function Loading() {
  const loading = useLoading();

  return (
    <div className="fixed top-0 left-0 w-screen z-10">
      <BarLoader
        cssOverride={{ width: "100vw" }}
        color="#0ea5e9"
        loading={loading.isLoading}
      />
    </div>
  );
}
