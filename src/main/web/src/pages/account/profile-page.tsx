import { useActionData, useSubmit } from "react-router-dom";
import { DeleteAccountForm } from "./components/delete-account-form";
import { ProfileForm } from "./components/profile-form";
import { useToast } from "@/hooks/use-toast";
import { useEffect } from "react";
import { useAuth } from "@/hooks/use-auth";

export default function ProfilePage() {
  const { toast } = useToast();
  const { currentUser } = useAuth();
  const submit = useSubmit();
  const actionData = useActionData() as
    | undefined
    | { error: boolean; message: string };

  useEffect(() => {
    if (actionData?.error === true) {
      toast({
        title: actionData?.message,
        variant: "destructive",
      });
    }

    if (actionData?.error === false) {
      toast({
        title: actionData?.message,
        variant: "success",
      });
    }
  }, [actionData, toast]);

  return (
    <div className="space-y-4 md:space-y-8">
      {currentUser != null && (
        <>
          <ProfileForm
            user={currentUser}
            handleSubmit={(data) => {
              submit(data, {
                method: "post",
                action: "/account/profile",
                encType: "application/json",
              });
            }}
          />
          <DeleteAccountForm
            handleSubmit={() => {
              submit(null, {
                method: "delete",
                action: `/account/profile/${currentUser.id}/delete`,
              });
            }}
          />
        </>
      )}
    </div>
  );
}
