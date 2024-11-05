import React from "react";
import { Form } from "react-router-dom";

interface SignOutFormProps {
  children?: React.ReactNode;
}
export function SignOutForm({ children }: SignOutFormProps) {
  return (
    <Form method="delete" action="/auth/signout">
      {children}
    </Form>
  );
}
