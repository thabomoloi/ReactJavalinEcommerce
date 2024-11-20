import { cn } from "@/lib/utils";
import { Package, Heart, Star, User, LogOut } from "lucide-react";
import { NavLink, useNavigate } from "react-router-dom";
import { useAuth } from "@/hooks/use-auth";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuGroup,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "../ui/dropdown-menu";
import {
  Tooltip,
  TooltipContent,
  TooltipProvider,
  TooltipTrigger,
} from "../ui/tooltip";
import { useIsMobile } from "@/hooks/use-mobile";
import { FaUserCircle } from "react-icons/fa";
import React from "react";

const accountLinks = [
  { title: "Profile", href: "/account/profile", icon: User },
  { title: "Orders", href: "/account/orders", icon: Package },
  { title: "Wishlist", href: "/account/wishlist", icon: Heart },
  { title: "Reviews", href: "/account/reviews", icon: Star },
] as const;

type AccountNavLinkProps = (typeof accountLinks)[number];

function AccountNavLink({ title, href, icon: LinkIcon }: AccountNavLinkProps) {
  return (
    <NavLink
      to={href}
      className={({ isActive }) =>
        cn(
          "w-full px-2 py-1 text-sm font-semibold rounded-md flex items-center hover:bg-slate-200/50",
          {
            "bg-slate-200": isActive,
            "text-secondary-foreground/80": !isActive,
          }
        )
      }
    >
      <LinkIcon className="mr-3" />
      {title}
    </NavLink>
  );
}

function SignOutButton({ onSignOut }: { onSignOut: () => void }) {
  return (
    <button
      className="w-full px-2 py-1 text-sm font-semibold rounded-md flex items-center hover:bg-slate-200/50 text-secondary-foreground/80"
      onClick={onSignOut}
    >
      <LogOut className="mr-3" />
      Sign out
    </button>
  );
}

export function AccountMenu() {
  const { signOut } = useAuth();
  return (
    <nav>
      <ul className="space-y-1">
        {accountLinks.map((link) => (
          <li key={link.href}>
            <AccountNavLink {...link} />
          </li>
        ))}
        <li>
          <SignOutButton onSignOut={signOut} />
        </li>
      </ul>
    </nav>
  );
}

const AccountButton = React.forwardRef<
  HTMLButtonElement,
  React.ButtonHTMLAttributes<HTMLButtonElement>
>(({ onClick, ...props }, ref) => {
  const isMobile = useIsMobile();
  const { isAuthenticated } = useAuth();
  const navigate = useNavigate();

  return (
    <button
      ref={ref}
      onClick={(e) => {
        if (!isAuthenticated) navigate("/auth/signin");
        if (onClick) onClick(e);
      }}
      className={cn("inline-flex items-center justify-center", {
        "h-9 border border-slate-700 hover:bg-slate-700 hover:text-white px-1.5 py-1 rounded-full":
          !isMobile,
      })}
      {...props}
    >
      <span className="sr-only">Account Menu</span>
      <FaUserCircle className={cn("w-6 h-6", { "mr-2": !isMobile })} />
      {!isMobile && (
        <span className="text-sm font-semibold mr-2">My Account</span>
      )}
    </button>
  );
});

AccountButton.displayName = "AccountButton";

export function AccountDropdownMenu() {
  const { isAuthenticated, signOut } = useAuth();

  if (!isAuthenticated) {
    return (
      <TooltipProvider>
        <Tooltip>
          <TooltipTrigger asChild>
            <AccountButton />
          </TooltipTrigger>
          <TooltipContent>Account</TooltipContent>
        </Tooltip>
      </TooltipProvider>
    );
  }

  return (
    <DropdownMenu>
      <TooltipProvider>
        <Tooltip>
          <TooltipTrigger asChild>
            <DropdownMenuTrigger asChild>
              <AccountButton />
            </DropdownMenuTrigger>
          </TooltipTrigger>
          <TooltipContent>Account Menu</TooltipContent>
        </Tooltip>
      </TooltipProvider>
      <DropdownMenuContent className="w-48 mr-4 md:mr-8">
        <DropdownMenuLabel className="font-bold">My Account</DropdownMenuLabel>
        <DropdownMenuGroup className="font-medium">
          <DropdownMenuSeparator />
          {accountLinks.map((link) => (
            <DropdownMenuItem key={link.href} className="p-0 mb-1">
              <AccountNavLink {...link} />
            </DropdownMenuItem>
          ))}
          <DropdownMenuSeparator />
          <SignOutButton onSignOut={signOut} />
        </DropdownMenuGroup>
      </DropdownMenuContent>
    </DropdownMenu>
  );
}
