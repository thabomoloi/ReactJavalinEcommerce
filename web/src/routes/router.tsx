import { Page } from "@/components/page";
import AccountLayout from "@/pages/account/account-layout";
import ProfilePage from "@/pages/account/profile-page";
import ProfilePageSkeleton from "@/pages/account/profile-page-skeleton";
import HomePage from "@/pages/home/home-page";
import RootLayout from "@/pages/root-layout";
import { BrowserRouter, Route, Routes } from "react-router-dom";
import AuthRoutes from "./auth-routes";

export default function Router() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<RootLayout />}>
          <Route index element={<HomePage />} />
          <Route path="account" element={<AccountLayout />}>
            <Route
              path="profile"
              element={
                <Page fallback={<ProfilePageSkeleton />}>
                  <ProfilePage />
                </Page>
              }
            />
          </Route>
        </Route>
        {AuthRoutes()}
      </Routes>
    </BrowserRouter>
  );
}
