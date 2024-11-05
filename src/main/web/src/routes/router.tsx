import AuthLayout from "@/pages/auth/auth-layout";
import SignInPage from "@/pages/auth/signin-page";
import SignUpPage from "@/pages/auth/signup-page";
import HomePage from "@/pages/home/home-page";
import RootLayout from "@/pages/root-layout";
import { createBrowserRouter, Navigate } from "react-router-dom";
import {
  deleteAccountAction,
  profileAction,
  signInAction,
  signOutAction,
  signUpAction,
} from "./actions";
import ProfilePage from "@/pages/account/profile-page";
import AccountLayout from "@/pages/account/account-layout";
import { ProtectedPage } from "@/components/protected-page";

export const router = createBrowserRouter([
  {
    path: "/",
    element: <RootLayout />,
    children: [
      { index: true, element: <HomePage /> },
      {
        path: "/account",
        element: <AccountLayout />,
        children: [
          {
            path: "profile",
            element: (
              <ProtectedPage fallback={<HomePage />}>
                <ProfilePage />
              </ProtectedPage>
            ),
            action: profileAction,
          },
          {
            path: "profile/:userId/delete",
            element: (
              <ProtectedPage fallback={<HomePage />}>
                <ProfilePage />
              </ProtectedPage>
            ),
            action: deleteAccountAction,
          },
        ],
      },
    ],
  },
  {
    path: "/auth",
    element: <AuthLayout />,
    children: [
      { path: "signup", element: <SignUpPage />, action: signUpAction },
      { path: "signin", element: <SignInPage />, action: signInAction },
      {
        path: "signout",
        element: <Navigate to="/" replace />,
        action: signOutAction,
      },
    ],
  },
]);
