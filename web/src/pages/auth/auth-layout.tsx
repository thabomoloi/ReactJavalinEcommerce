import { AuthHeader } from "@/components/header/auth-header";
import { Loading } from "@/components/loading";
import { cn } from "@/lib/utils";
import React from "react";
import { Outlet } from "react-router-dom";

export default function AuthLayout() {
  const widthClassName = "w-full px-4 md:px-8 max-w-7xl";

  return (
    <React.Fragment>
      <Loading />
      <div className="flex flex-col items-center">
        <AuthHeader />

        <main className={cn(widthClassName, "py-4 md:py-8")}>
          <div className="flex flex-col justify-center items-center h-full">
            <Outlet />
          </div>
        </main>
        {/* <Footer /> */}
      </div>
    </React.Fragment>
  );
}
