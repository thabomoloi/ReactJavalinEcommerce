import { z } from "zod";

const email = z
  .string()
  .min(1, { message: "Email is required" })
  .email("Invalid email address");

const name = z.string().min(3, "Name must be at least 3 characters long");
const password = z
  .string()
  .min(8, "Password must be at least 8 characters long")
  .max(16, "Password must be at most 16 characters long")
  .regex(/[A-Z]/, "Password must contain at least one uppercase letter")
  .regex(/\d/, "Password must contain at least one digit")
  .regex(
    /[@#$%^&+=!]/,
    "Password must contain at least one special character @#$%^&+=!"
  );

export const SignUpSchema = z.object({
  email,
  name,
  password,
});

export const SignInSchema = z.object({
  email,
  password: z.string().min(1, "Password is required"),
});

export const UserUpdateSchema = z.object({
  id: z.coerce.number().int().min(1),
  name,
  email,
});

export type SignInSchemaType = z.infer<typeof SignInSchema>;
export type SignUpSchemaType = z.infer<typeof SignUpSchema>;
export type UserUpdateSchemaType = z.infer<typeof UserUpdateSchema>;
