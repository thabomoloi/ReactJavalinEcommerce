import { Button } from "@/components/ui/button";
import {
  Card,
  CardDescription,
  CardContent,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Trash2 } from "lucide-react";

export interface DeletedAccountFormProps {
  handleSubmit: () => void;
}

export function DeleteAccountForm({ handleSubmit }: DeletedAccountFormProps) {
  return (
    <Card>
      <CardHeader>
        <CardTitle>Danger Zone</CardTitle>
        <CardDescription>These actions cannot be undone.</CardDescription>
      </CardHeader>
      <CardContent>
        <Button
          onClick={handleSubmit}
          variant="outline"
          className="w-full text-destructive hover:text-destructive/80"
        >
          <Trash2 /> Delete Account
        </Button>
      </CardContent>
    </Card>
  );
}
