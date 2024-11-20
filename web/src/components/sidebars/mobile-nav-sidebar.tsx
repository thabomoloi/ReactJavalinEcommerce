import {
  Sheet,
  SheetContent,
  SheetDescription,
  SheetHeader,
  SheetTitle,
  SheetTrigger,
} from "@/components/ui/sheet";
import React from "react";
import { Logo } from "../logo";
import {
  Tooltip,
  TooltipContent,
  TooltipProvider,
  TooltipTrigger,
} from "../ui/tooltip";

export function MobileNavSidebar({ children }: { children: React.ReactNode }) {
  return (
    <Sheet>
      <TooltipProvider>
        <Tooltip>
          <TooltipTrigger asChild>
            <SheetTrigger>
              <span className="sr-only">Open menu</span>
              {children}
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
          <SheetDescription>Main Menu</SheetDescription>
        </SheetHeader>
      </SheetContent>
    </Sheet>
  );
}
