import { Button } from "@/components/ui/button";
import { Card, CardHeader, CardTitle, CardContent } from "@/components/ui/card";
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form";
import { Input } from "@/components/ui/input";
import { useLoading } from "@/hooks/use-loading";
import { useToast } from "@/hooks/use-toast";
import {
  ForgotPasswordSchema,
  ForgotPasswordSchemaType,
} from "@/lib/data/schemas/user";
import { zodResolver } from "@hookform/resolvers/zod";
import { LoaderCircleIcon } from "lucide-react";
import { useEffect } from "react";
import { useForm } from "react-hook-form";
import { useSubmit, useActionData } from "react-router-dom";

export default function ForgotPasswordPage() {
  const submit = useSubmit();
  const loading = useLoading();
  const { toast } = useToast();

  const actionData = useActionData() as
    | undefined
    | { error: boolean; message: string };

  const form = useForm<ForgotPasswordSchemaType>({
    resolver: zodResolver(ForgotPasswordSchema),
    defaultValues: {
      email: "",
    },
  });

  const onSubmit = (data: ForgotPasswordSchemaType) => {
    submit(data, {
      method: "post",
      action: "/auth/forgot-password",
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
    }
  }, [actionData, toast]);

  return (
    <Card className="w-full max-w-md">
      <CardHeader>
        <CardTitle className="text-xl text-center">Forgot Password</CardTitle>
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
                    <FormLabel>Enter Your Email</FormLabel>
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
            </div>
            <Button
              type="submit"
              className="w-full mt-6"
              disabled={loading.isLoading}
            >
              {loading.isSubmitting && (
                <LoaderCircleIcon className="animate-spin mr-2" />
              )}
              Send password reset link
            </Button>
          </form>
        </Form>
      </CardContent>
    </Card>
  );
}
