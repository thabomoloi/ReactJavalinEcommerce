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
import { useAccount } from "@/hooks/use-account";
import {
  ResetPasswordSchema,
  ResetPasswordSchemaType,
} from "@/lib/data/schemas/user";
import { zodResolver } from "@hookform/resolvers/zod";
import { LoaderCircleIcon } from "lucide-react";
import { useForm } from "react-hook-form";
import { useNavigate, useParams } from "react-router-dom";

export default function ResetPasswordPage() {
  const params = useParams();
  const navigate = useNavigate();

  const { resetPassword, isPending } = useAccount();

  const form = useForm<ResetPasswordSchemaType>({
    resolver: zodResolver(ResetPasswordSchema),
    defaultValues: { password: "" },
  });

  const onSubmit = (data: ResetPasswordSchemaType) => {
    if (params.token) {
      resetPassword(
        { token: params.token, data },
        { onSuccess: () => navigate("/auth/signin") }
      );
    }
  };

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
                        disabled={isPending}
                      />
                    </PasswordField>
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <Button type="submit" className="w-full mt-6" disabled={isPending}>
              {isPending && <LoaderCircleIcon className="animate-spin mr-2" />}
              Reset password
            </Button>
          </form>
        </Form>
      </CardContent>
    </Card>
  );
}
