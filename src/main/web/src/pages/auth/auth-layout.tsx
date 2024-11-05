import { Loading } from "@/components/loading";
import { Outlet } from "react-router-dom";

export default function AuthLayout() {
  return (
    <div className="h-full flex flex-col">
      <Loading />
      {/* <AuthHeader /> */}
      <main className="p-4 flex-grow">
        <div className="flex flex-col justify-center items-center h-full">
          <Outlet />
        </div>
      </main>
      {/* <Footer /> */}
    </div>
  );
}
