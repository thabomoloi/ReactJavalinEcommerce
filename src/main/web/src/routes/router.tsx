import AuthLayout from "@/pages/auth/auth-layout";
import SignInPage from "@/pages/auth/signin-page";
import SignUpPage from "@/pages/auth/signup-page";
import HomePage from "@/pages/home/home-page";
import RootLayout from "@/pages/root-layout";
import { createBrowserRouter, Navigate } from "react-router-dom";
import {
  deleteAccountAction,
  profileAction,
  resetPasswordAction,
  sendConfirmationLinkAction,
  sendResetPasswordLinkAction,
  signInAction,
  signOutAction,
  signUpAction,
  verifyAccountAction,
} from "./actions";
import ProfilePage from "@/pages/account/profile-page";
import AccountLayout from "@/pages/account/account-layout";
import { ProtectedPage } from "@/components/protected-page";
import UnverifiedPage from "@/pages/auth/unverified-page";
import VerifyAccountPage from "@/pages/auth/verify-account-page";
import ForgotPasswordPage from "@/pages/auth/forgot-password-page";
import ResetPasswordPage from "@/pages/auth/reset-password-page";
import { Role } from "@/lib/data/models/types";

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
      {
        path: "unverified",
        element: (
          <ProtectedPage fallback={<HomePage />}>
            <UnverifiedPage />
          </ProtectedPage>
        ),
        action: sendConfirmationLinkAction,
      },
      {
        path: "confirm/:userId/:token",

        element: (
          <ProtectedPage fallback={<HomePage />}>
            <VerifyAccountPage />
          </ProtectedPage>
        ),
        action: verifyAccountAction,
      },
      {
        path: "forgot-password",
        element: (
          <ProtectedPage
            fallback={<HomePage />}
            signInRequired={false}
            rolesRequired={[Role.GUEST]}
          >
            <ForgotPasswordPage />
          </ProtectedPage>
        ),
        action: sendResetPasswordLinkAction,
      },
      {
        path: "reset-password/:userId/:token",
        element: (
          <ProtectedPage
            fallback={<HomePage />}
            signInRequired={false}
            rolesRequired={[Role.GUEST]}
          >
            <ResetPasswordPage />
          </ProtectedPage>
        ),
        action: resetPasswordAction,
      },
    ],
  },
]);
