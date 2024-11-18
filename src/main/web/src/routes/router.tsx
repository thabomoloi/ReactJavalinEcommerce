import AuthLayout from "@/pages/auth/auth-layout";
import SignInPage from "@/pages/auth/signin/page";
import SignUpPage from "@/pages/auth/signup/page";
import HomePage from "@/pages/home/home-page";
import RootLayout from "@/pages/root-layout";
import { createBrowserRouter } from "react-router-dom";
import ProfilePage from "@/pages/account/profile-page";
import AccountLayout from "@/pages/account/account-layout";
import { Page } from "@/components/page";
import ConfirmAccountPage from "@/pages/auth/confirm-account/page";
import ForgotPasswordPage from "@/pages/auth/forgot-password/page";
import ResetPasswordPage from "@/pages/auth/reset-password/page";
import { Role } from "@/lib/data/models/types";
import ProfilePageSkeleton from "@/pages/account/profile-page-skeleton";
import ConfirmAccountPageSkeleton from "@/pages/auth/confirm-account/skeleton";

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
              <Page fallback={<ProfilePageSkeleton />}>
                <ProfilePage />
              </Page>
            ),
          },
          {
            path: "profile/:userId/delete",
            element: (
              <Page fallback={<HomePage />}>
                <ProfilePage />
              </Page>
            ),
          },
        ],
      },
    ],
  },
  {
    path: "/auth",
    element: <AuthLayout />,
    children: [
      { path: "signup", element: <SignUpPage /> },
      { path: "signin", element: <SignInPage /> },
      {
        path: "confirm-account",
        element: (
          <Page fallback={<ConfirmAccountPageSkeleton />}>
            <ConfirmAccountPage />
          </Page>
        ),
        children: [{ path: ":token", element: null }],
      },
      {
        path: "forgot-password",
        element: (
          <Page
            fallback={<HomePage />}
            signInRequired={false}
            rolesRequired={[Role.GUEST]}
          >
            <ForgotPasswordPage />
          </Page>
        ),
      },
      {
        path: "reset-password/:token",
        element: (
          <Page
            fallback={<HomePage />}
            signInRequired={false}
            rolesRequired={[Role.GUEST]}
          >
            <ResetPasswordPage />
          </Page>
        ),
      },
    ],
  },
]);
