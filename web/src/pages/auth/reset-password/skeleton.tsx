import { Card, CardContent, CardHeader } from "@/components/ui/card";
import { Skeleton } from "@/components/ui/skeleton";

export function ResetPasswordPageSkeleton() {
  return (
    <Card className="max-w-md w-full">
      <CardHeader>
        <Skeleton className="h-6 w-1/3 mx-auto" />
      </CardHeader>
      <CardContent>
        <div className="space-y-6">
          <div>
            <Skeleton className="h-5 w-1/4 mb-2" />
            <Skeleton className="h-10 w-full" />
          </div>
          <Skeleton className="h-10 w-full" />
        </div>
      </CardContent>
    </Card>
  );
}
