import { Header } from "@/components/header/header";
import { Loading } from "@/components/loading";
import { cn } from "@/lib/utils";
import React from "react";
import { Outlet } from "react-router-dom";

export default function RootLayout() {
  const widthClassName = "w-full px-4 md:px-8 max-w-7xl";
  return (
    <React.Fragment>
      <Loading />
      <div className="flex flex-col items-center">
        <Header widthClassName={widthClassName} />
        <main className={cn(widthClassName, "py-4 md:py-8")}>
          <Outlet />
        </main>
      </div>
    </React.Fragment>
  );
}
