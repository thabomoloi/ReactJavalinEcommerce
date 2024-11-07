import { Button } from "@/components/ui/button";
import {
  Card,
  CardContent,
  CardFooter,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { useLoading } from "@/hooks/use-loading";
import { useToast } from "@/hooks/use-toast";
import { useAuth } from "@/lib/store/auth";
import { LoaderCircleIcon } from "lucide-react";
import { useEffect } from "react";
import { Form, useActionData, useNavigate } from "react-router-dom";

export default function VerifyAccountPage() {
  const { toast } = useToast();
  const loading = useLoading();
  const { currentUser } = useAuth();
  const navigate = useNavigate();

  const actionData = useActionData() as
    | undefined
    | { error: boolean; message: string };

  useEffect(() => {
    if (actionData) {
      toast({
        title: actionData.message,
        variant: actionData.error ? "destructive" : "success",
      });

      if (!actionData.error) {
        navigate("/account/profile");
      }
    }
  }, [actionData, navigate, toast]);

  return (
    <Card className="max-w-lg">
      <CardHeader>
        <CardTitle className="text-center">Account Verification</CardTitle>
      </CardHeader>
      <CardContent>
        <p className="text-center">
          Please click the button below to complete the verification of your
          account.
        </p>
      </CardContent>
      <CardFooter className="justify-center">
        <Form method="post">
          <Input type="hidden" name="userId" value={currentUser?.id ?? ""} />
          <Button disabled={loading.isLoading} type="submit">
            {loading.isSubmitting && (
              <LoaderCircleIcon className="animate-spin mr-2" />
            )}
            Verify Account
          </Button>
        </Form>
      </CardFooter>
    </Card>
  );
}
