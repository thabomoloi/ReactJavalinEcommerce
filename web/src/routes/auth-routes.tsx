import { Page } from "@/components/page";
import { Role } from "@/lib/data/models/types";
import AuthLayout from "@/pages/auth/auth-layout";
import ConfirmAccountPage from "@/pages/auth/confirm-account/page";
import ConfirmAccountPageSkeleton from "@/pages/auth/confirm-account/skeleton";
import ForgotPasswordPage from "@/pages/auth/forgot-password/page";
import { ForgotPasswordPageSkeleton } from "@/pages/auth/forgot-password/skeleton";
import ResetPasswordPage from "@/pages/auth/reset-password/page";
import { ResetPasswordPageSkeleton } from "@/pages/auth/reset-password/skeleton";
import SignInPage from "@/pages/auth/signin/page";
import { SignInPageSkeleton } from "@/pages/auth/signin/skeleton";
import SignUpPage from "@/pages/auth/signup/page";
import { SignUpPageSkeleton } from "@/pages/auth/signup/skeleton";
import React from "react";
import { Route } from "react-router-dom";

export default function AuthRoutes() {
  return (
    <React.Fragment>
      <Route path="/auth" element={<AuthLayout />}>
        <Route
          path="signin"
          element={
            <Page
              signInRequired={false}
              rolesRequired={[Role.GUEST]}
              fallback={<SignInPageSkeleton />}
            >
              <SignInPage />
            </Page>
          }
        />
        <Route
          path="signup"
          element={
            <Page
              signInRequired={false}
              rolesRequired={[Role.GUEST]}
              fallback={<SignUpPageSkeleton />}
            >
              <SignUpPage />
            </Page>
          }
        />
        <Route
          path="confirm-account"
          element={
            <Page fallback={<ConfirmAccountPageSkeleton />}>
              <ConfirmAccountPage />
            </Page>
          }
        >
          <Route path=":token" element={null} />
        </Route>
        <Route
          path="forgot-password"
          element={
            <Page
              fallback={<ForgotPasswordPageSkeleton />}
              signInRequired={false}
              rolesRequired={[Role.GUEST]}
            >
              <ForgotPasswordPage />
            </Page>
          }
        />
        <Route
          path="reset-password/:token"
          element={
            <Page
              fallback={<ResetPasswordPageSkeleton />}
              signInRequired={false}
              rolesRequired={[Role.GUEST]}
            >
              <ResetPasswordPage />
            </Page>
          }
        />
      </Route>
    </React.Fragment>
  );
}
