import { router } from "@/routes/router";
import { RouterProvider } from "react-router-dom";
import { useAuth } from "@/lib/store/auth";
import { useEffect } from "react";

function App() {
  const { verifyAuthentication } = useAuth();
  useEffect(() => {
    verifyAuthentication();
  }, [verifyAuthentication]);
  return <RouterProvider router={router} />;
}

export default App;
