import { NavLink } from "react-router-dom";
import { Logo } from "../logo";

export function AuthHeader() {
  return (
    <header className="w-full">
      <div className="flex items-center justify-center p-4">
        <NavLink to="/">
          <Logo className="w-56" />
        </NavLink>
      </div>
    </header>
  );
}
