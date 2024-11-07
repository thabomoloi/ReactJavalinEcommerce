import { Button } from "@/components/ui/button";
import {
  Card,
  CardContent,
  CardFooter,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
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
import { SignInSchema, SignInSchemaType } from "@/lib/data/schemas/user";
import { useAuth } from "@/lib/store/auth";
import { zodResolver } from "@hookform/resolvers/zod";
import { LoaderCircleIcon } from "lucide-react";
import { useEffect } from "react";
import { useForm } from "react-hook-form";
import {
  Link,
  useSubmit,
  useActionData,
  useNavigate,
  Navigate,
} from "react-router-dom";

export default function SignInPage() {
  const { toast } = useToast();
  const submit = useSubmit();
  const navigate = useNavigate();
  const loading = useLoading();

  const actionData = useActionData() as
    | undefined
    | { error: boolean; message: string };

  const auth = useAuth();

  const form = useForm<SignInSchemaType>({
    resolver: zodResolver(SignInSchema),
    defaultValues: {
      email: "",
      password: "",
    },
  });

  const onSubmit = (data: SignInSchemaType) => {
    submit(data, {
      method: "post",
      action: "/auth/signin",
      encType: "application/json",
    });
  };

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
      navigate("/");
    }
  }, [actionData, navigate, toast]);

  if (auth.isAuthenticated) {
    return <Navigate to="/account/profile" replace />;
  }

  return (
    <Card className="w-full max-w-md">
      <CardHeader>
        <CardTitle className="text-xl text-center">Welcome Back!</CardTitle>
      </CardHeader>
      <CardContent>
        <Form {...form}>
          <form onSubmit={form.handleSubmit(onSubmit)}>
            <div className="space-y-3">
              <FormField
                control={form.control}
                name="email"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Email</FormLabel>
                    <FormControl>
                      <Input
                        required
                        {...field}
                        type="email"
                        className="bg-secondary"
                        autoComplete="email"
                        disabled={loading.isLoading}
                      />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
              <FormField
                control={form.control}
                name="password"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Password</FormLabel>
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
            </div>
            <Button
              type="submit"
              className="w-full mt-6"
              disabled={loading.isLoading}
            >
              {loading.isSubmitting && (
                <LoaderCircleIcon className="animate-spin mr-2" />
              )}
              Sign in
            </Button>
          </form>
        </Form>
      </CardContent>
      <CardFooter className="flex-col text-sm text-center">
        <p>
          <Link to="/auth/forgot-password" className="font-bold text-green-700">
            Forgot Password?
          </Link>
        </p>
        <p>
          Don't have an account?{" "}
          <Link to="/auth/signup" className="font-bold text-green-700">
            Sign up
          </Link>
          .
        </p>
      </CardFooter>
    </Card>
  );
}