import { signOut } from "@/lib/data/api/user";
import { cn } from "@/lib/utils";
import { Package, Heart, Star, User, LogOut } from "lucide-react";
import { NavLink } from "react-router-dom";

const accountLinks = [
  { title: "Profile", href: "/account/profile", icon: User },
  { title: "Orders", href: "/account/orders", icon: Package },
  { title: "Wishlist", href: "/account/wishlist", icon: Heart },
  { title: "Reviews", href: "/account/reviews", icon: Star },
];

export function AccountMenu() {
  return (
    <div>
      <nav>
        <ul className="space-y-1">
          {accountLinks.map((link, idx) => (
            <li key={idx}>
              <NavLink
                to={link.href}
                className={({ isActive }) =>
                  cn(
                    "px-2 py-1 font-semibold text-sm rounded-md flex items-center hover:bg-slate-200/50",
                    {
                      "bg-slate-200": isActive,
                      "text-secondary-foreground/80": !isActive,
                    }
                  )
                }
              >
                <link.icon className="inline-block mr-3" /> {link.title}
              </NavLink>
            </li>
          ))}
          <li>
            <button
              onClick={() => signOut()}
              className="px-2 py-1 font-semibold text-sm rounded-md flex items-center hover:bg-slate-200/50 text-secondary-foreground/80 w-full"
            >
              <LogOut className="inline-block mr-3" />
              Sign out
            </button>
          </li>
        </ul>
      </nav>
    </div>
  );
}
