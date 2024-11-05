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
import { Form, useActionData } from "react-router-dom";

export default function UnverifiedPage() {
  const { toast } = useToast();
  const loading = useLoading();
  const { currentUser } = useAuth();
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
    <Card className="max-w-lg">
      <CardHeader>
        <CardTitle className="text-center">Verify your Account</CardTitle>
      </CardHeader>
      <CardContent>
        <p className="text-center">
          Your account has not yet been verified. Please check your email for a
          verification link, or resend verification link if you didn't receive
          it.
        </p>
      </CardContent>
      <CardFooter className="justify-center">
        <Form method="post" action="/auth/unverified">
          <Input type="hidden" name="userId" value={currentUser?.id ?? ""} />
          <Button disabled={loading.isLoading} type="submit">
            {loading.isSubmitting && (
              <LoaderCircleIcon className="animate-spin mr-2" />
            )}
            Resend Verification Link
          </Button>
        </Form>
      </CardFooter>
    </Card>
  );
}
