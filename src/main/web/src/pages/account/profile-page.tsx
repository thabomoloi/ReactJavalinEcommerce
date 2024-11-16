import { DeleteAccountForm } from "./components/delete-account-form";
import { ProfileForm } from "./components/profile-form";
import { useAuth } from "@/hooks/use-auth";
import { useAccount } from "@/hooks/use-account";

export default function ProfilePage() {
  const { currentUser } = useAuth();
  const { updateAccount, deleteAccount } = useAccount();

  return (
    <div className="space-y-4 md:space-y-8">
      {currentUser != null && (
        <>
          <ProfileForm
            user={currentUser}
            handleSubmit={(data) => updateAccount(data)}
          />
          <DeleteAccountForm
            handleSubmit={() => deleteAccount(currentUser.id)}
          />
        </>
      )}
    </div>
  );
}
