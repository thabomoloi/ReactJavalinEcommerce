import { Button } from "@/components/ui/button";
import {
  Card,
  CardContent,
  CardFooter,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { useAccount } from "@/hooks/use-account";
import { useAuth } from "@/hooks/use-auth";
import { LoaderCircleIcon } from "lucide-react";
import { Navigate, useParams, useNavigate } from "react-router-dom";
import { useEffect } from "react";

export default function ConfirmAccountPage() {
  const { currentUser } = useAuth();
  const { isPending, sendConfirmationLink, confirmAccount } = useAccount();
  const params = useParams();
  const navigate = useNavigate();

  useEffect(() => {
    if (params.token) {
      confirmAccount(params.token);
      navigate("/auth/confirm-account");
    }
  }, [confirmAccount, navigate, params.token]);

  if (currentUser == null) {
    return <Navigate to="/auth/signin" />;
  }

  return (
    <Card className="w-full max-w-lg">
      <CardHeader>
        <CardTitle>Confirm Account</CardTitle>
      </CardHeader>
      <CardContent>
        <p>
          Your account has not yet been confirmed. Please check your email for a
          confirmation link, or resend link if you didn't receive one.
        </p>
      </CardContent>
      <CardFooter className="justify-end">
        <Button
          disabled={isPending}
          onClick={() => sendConfirmationLink(currentUser.id)}
        >
          {isPending && <LoaderCircleIcon className="animate-spin mr-2" />}
          Resend Link
        </Button>
      </CardFooter>
    </Card>
  );
}
