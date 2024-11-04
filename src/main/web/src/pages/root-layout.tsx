import { Outlet } from "react-router-dom";

export default function RootLayout() {
  return (
    <div className="flex flex-col items-center">
      <main className="w-full p-4 md:p-8 max-w-7xl">
        <Outlet />
      </main>
    </div>
  );
}
