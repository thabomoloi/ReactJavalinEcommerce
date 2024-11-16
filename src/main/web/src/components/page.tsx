import { useAuth } from "@/hooks/use-auth";
import { Role } from "@/lib/data/models/types";
import { CircleAlert } from "lucide-react";
import React from "react";
import { Navigate, useLocation } from "react-router-dom";

interface PageProps {
  rolesRequired?: Role[];
  signInRequired?: boolean;
  children?: React.ReactNode;
  fallback: React.ReactNode;
}

export function Page({
  rolesRequired = [Role.USER, Role.UNVERIFIED_USER, Role.ADMIN],
  signInRequired = true,
  children,
  fallback,
}: PageProps) {
  const { currentUser, isAuthenticated, isLoading } = useAuth();

  const { pathname } = useLocation();

  if (isLoading) return <>{fallback}</>;

  const role = currentUser?.role ?? Role.GUEST;

  if (signInRequired && !isAuthenticated) {
    return <Navigate to="/auth/signin" replace />;
  }

  if (
    !signInRequired &&
    isAuthenticated &&
    rolesRequired.length == 1 &&
    rolesRequired[0] == Role.GUEST
  ) {
    return <Navigate to="/account/profile" replace />;
  }

  if (
    isAuthenticated &&
    role != Role.UNVERIFIED_USER &&
    ("/auth/unverified" == pathname || pathname.startsWith("/auth/confirm"))
  ) {
    return <Navigate to="/account/profile" replace />;
  }

  if (rolesRequired.includes(role)) {
    return children;
  }

  if (role == Role.UNVERIFIED_USER) {
    return <Navigate to="/auth/confirm" replace />;
  }

  return (
    <div className="flex items-center gap-4 bg-yellow-100 border-2 p-4 rounded-md border-yellow-500/25">
      <div className="bg-yellow-200/50 rounded-full p-2">
        <CircleAlert strokeWidth={3} className="w-8 h-8 text-yellow-800" />
      </div>
      <div>
        <p className="font-bold text-yellow-900">Warning!</p>
        <p className="text-yellow-800 text-sm">
          You do not have permission to access this page.
        </p>
      </div>
    </div>
  );
}
