import AuthLayout from "@/pages/auth/auth-layout";
import SignInPage from "@/pages/auth/signin-page";
import SignUpPage from "@/pages/auth/signup-page";
import HomePage from "@/pages/home/home-page";
import RootLayout from "@/pages/root-layout";
import { createBrowserRouter } from "react-router-dom";
import {
  deleteAccountAction,
  profileAction,
  signInAction,
  signUpAction,
} from "./actions";
import ProfilePage from "@/pages/account/profile-page";
import AccountLayout from "@/pages/account/account-layout";

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
          { path: "profile", element: <ProfilePage />, action: profileAction },
          {
            path: "profile/:userId/delete",
            element: <ProfilePage />,
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
    ],
  },
]);
