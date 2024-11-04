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
import { User } from "@/lib/data/models/types";
import {
  UserUpdateSchema,
  UserUpdateSchemaType,
} from "@/lib/data/schemas/user";
import { zodResolver } from "@hookform/resolvers/zod";
import { useForm } from "react-hook-form";

export interface ProfileFormProps {
  user: User;
  handleSubmit: (data: UserUpdateSchemaType) => void;
}

export function ProfileForm({ user, handleSubmit }: ProfileFormProps) {
  const form = useForm<UserUpdateSchemaType>({
    resolver: zodResolver(UserUpdateSchema),
    defaultValues: {
      id: user.id,
      name: user.name,
      email: user.email,
    },
  });

  const onSubmit = async (data: UserUpdateSchemaType) => {
    handleSubmit(data);
  };

  return (
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
  );
}
