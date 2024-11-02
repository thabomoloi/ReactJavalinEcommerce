import { Button } from "@/components/ui/button";
import {
  Card,
  CardContent,
  CardDescription,
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
import {
  UserUpdateSchema,
  UserUpdateSchemaType,
} from "@/lib/data/schemas/user";
import { useAuth } from "@/lib/store/auth";
import { zodResolver } from "@hookform/resolvers/zod";
import { Trash2 } from "lucide-react";
import { useEffect } from "react";
import { useForm } from "react-hook-form";
import { useSubmit } from "react-router-dom";

export default function ProfilePage() {
  const auth = useAuth();
  const submit = useSubmit();

  const form = useForm<UserUpdateSchemaType>({
    resolver: zodResolver(UserUpdateSchema),
    defaultValues: {
      name: "",
      email: "",
    },
  });

  useEffect(() => {
    if (auth.currentUser) {
      form.reset({
        name: auth.currentUser.name,
        email: auth.currentUser.email,
      });
    }
  }, [auth.currentUser, form]);

  const onSubmit = (data: UserUpdateSchemaType) => {
    submit(data, {
      method: "patch",
      action: "/account/profile",
      encType: "application/json",
    });
  };

  return (
    <div className="space-y-4 md:space-y-8">
      <Card>
        <CardHeader>
          <CardTitle>Profile Details</CardTitle>
        </CardHeader>
        <CardContent>
          <Form {...form}>
            <form onSubmit={form.handleSubmit(onSubmit)}>
              <div className="space-y-3">
                <FormField
                  control={form.control}
                  name="name"
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel>Name</FormLabel>
                      <FormControl>
                        <Input
                          required
                          {...field}
                          type="text"
                          className="bg-secondary"
                          autoComplete="name"
                        />
                      </FormControl>
                      <FormMessage />
                    </FormItem>
                  )}
                />
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
                        />
                      </FormControl>
                      <FormMessage />
                    </FormItem>
                  )}
                />
              </div>

              <Button type="submit" className="w-full mt-6">
                Save
              </Button>
            </form>
          </Form>
        </CardContent>
      </Card>

      <Card>
        <CardHeader>
          <CardTitle>Danger Zone</CardTitle>
          <CardDescription>These actions cannot be undone.</CardDescription>
        </CardHeader>
        <CardContent>
          <Button
            onClick={() => {
              submit(null, { method: "delete", action: "/account/profile" });
            }}
            variant="outline"
            className="w-full text-destructive hover:text-destructive/80"
          >
            <Trash2 /> Delete Account
          </Button>
        </CardContent>
      </Card>
    </div>
  );
}
