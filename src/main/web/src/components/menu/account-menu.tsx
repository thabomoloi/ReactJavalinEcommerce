import { cn } from "@/lib/utils";
import { Package, Heart, Star, User, LogOut } from "lucide-react";
import { NavLink } from "react-router-dom";

const accountLinks = [
  { title: "Profile", href: "/account/profile", icon: User },
  { title: "Orders", href: "/account/orders", icon: Package },
  { title: "Wishlist", href: "/account/wishlist", icon: Heart },
  { title: "Reviews", href: "/account/reviews", icon: Star },
  { title: "Sign out", href: "/auth/signout", icon: LogOut },
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
        </ul>
      </nav>
    </div>
  );
}
