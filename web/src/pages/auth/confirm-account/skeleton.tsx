import {
  Card,
  CardContent,
  CardFooter,
  CardHeader,
} from "@/components/ui/card";
import { Skeleton } from "@/components/ui/skeleton";

export default function ConfirmAccountPageSkeleton() {
  return (
    <Card className="w-full max-w-lg">
      <CardHeader>
        <Skeleton className="skeleton h-6 w-1/3" />
      </CardHeader>
      <CardContent>
        <div className="space-y-3">
          <Skeleton className="h-4 w-full" />
          <Skeleton className="h-4 w-5/6" />
          <Skeleton className="h-4 w-3/4" />
        </div>
      </CardContent>
      <CardFooter className="justify-end">
        <Skeleton className="h-10 w-32" />
      </CardFooter>
    </Card>
  );
}
