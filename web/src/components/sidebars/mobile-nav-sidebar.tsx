import {
  Sheet,
  SheetContent,
  SheetDescription,
  SheetHeader,
  SheetTitle,
  SheetTrigger,
} from "@/components/ui/sheet";
import { Logo } from "../logo";
import {
  Tooltip,
  TooltipContent,
  TooltipProvider,
  TooltipTrigger,
} from "../ui/tooltip";
import * as VisuallyHidden from "@radix-ui/react-visually-hidden";
import { IoMenu } from "react-icons/io5";

export function MobileNavSidebar() {
  return (
    <Sheet>
      <TooltipProvider>
        <Tooltip>
          <TooltipTrigger asChild>
            <SheetTrigger>
              <span className="sr-only">Open menu</span>
              <IoMenu className="w-6 h-6" />
            </SheetTrigger>
          </TooltipTrigger>
          <TooltipContent>Main menu</TooltipContent>
        </Tooltip>
      </TooltipProvider>
      <SheetContent side="left">
        <SheetHeader>
          <SheetTitle>
            <Logo className="w-40 mx-auto sm:mx-0" />
          </SheetTitle>
          <VisuallyHidden.Root>
            <SheetDescription>Main Menu</SheetDescription>
          </VisuallyHidden.Root>
        </SheetHeader>
      </SheetContent>
    </Sheet>
  );
}
