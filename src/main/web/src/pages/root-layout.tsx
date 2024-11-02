import { Outlet } from "react-router-dom";

export default function RootLayout() {
  return (
    <div className="flex flex-col items-center">
      <main className="w-full max-w-7xl">
        <Outlet />
      </main>
    </div>
  );
}
