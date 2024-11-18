import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form";
import { Input } from "@/components/ui/input";
import { PasswordField } from "@/components/ui/password-field";
import { useLoading } from "@/hooks/use-loading";
import { useToast } from "@/hooks/use-toast";
import {
  ResetPasswordSchema,
  ResetPasswordSchemaType,
} from "@/lib/data/schemas/user";
import { zodResolver } from "@hookform/resolvers/zod";
import { LoaderCircleIcon } from "lucide-react";
import { useEffect } from "react";
import { useForm } from "react-hook-form";
import {
  useActionData,
  useNavigate,
  useParams,
  useSubmit,
} from "react-router-dom";

export default function ResetPasswordPage() {
  const { toast } = useToast();
  const loading = useLoading();
  const params = useParams();
  const navigate = useNavigate();
  const submit = useSubmit();

  const actionData = useActionData() as
    | undefined
    | { error: boolean; message: string };

  const form = useForm<ResetPasswordSchemaType>({
    resolver: zodResolver(ResetPasswordSchema),
    defaultValues: { password: "" },
  });

  const onSubmit = (data: ResetPasswordSchemaType) => {
    submit(data, {
      method: "post",
      action: `/auth/reset-password/${params.userId}/${params.token}`,
      encType: "application/json",
    });
  };

  useEffect(() => {
    if (actionData) {
      toast({
        title: actionData.message,
        variant: actionData.error ? "destructive" : "success",
      });

      if (!actionData.error) {
        navigate("/auth/signin");
      }
    }
  }, [actionData, navigate, toast]);

  return (
    <Card className="max-w-md w-full">
      <CardHeader>
        <CardTitle className="text-center">Reset Password</CardTitle>
      </CardHeader>
      <CardContent>
        <Form {...form}>
          <form onSubmit={form.handleSubmit(onSubmit)}>
            <FormField
              control={form.control}
              name="password"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>New Password</FormLabel>
                  <FormControl>
                    <PasswordField>
                      <Input
                        required
                        {...field}
                        type="password"
                        className="bg-secondary"
                        autoComplete="current-password"
                        disabled={loading.isLoading}
                      />
                    </PasswordField>
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <Button
              type="submit"
              className="w-full mt-6"
              disabled={loading.isLoading}
            >
              {loading.isSubmitting && (
                <LoaderCircleIcon className="animate-spin mr-2" />
              )}
              Reset password
            </Button>
          </form>
        </Form>
      </CardContent>
    </Card>
  );
}
