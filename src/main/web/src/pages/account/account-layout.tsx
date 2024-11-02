import { AccountMenu } from "@/components/menu/account-menu";
import { Outlet } from "react-router-dom";

export default function AccountLayout() {
  return (
    <div className="flex gap-6">
      <div className="w-72">
        <AccountMenu />
      </div>
      <div className="flex-grow">
        <Outlet />
      </div>
    </div>
  );
}
