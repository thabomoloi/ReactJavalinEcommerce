import {
  Sheet,
  SheetContent,
  SheetHeader,
  SheetTitle,
  SheetTrigger,
} from "@/components/ui/sheet";
import React from "react";
import {
  Tooltip,
  TooltipContent,
  TooltipProvider,
  TooltipTrigger,
} from "../ui/tooltip";
import { useIsMobile } from "@/hooks/use-mobile";
import { cn } from "@/lib/utils";
import { MdShoppingCart } from "react-icons/md";

const ShoppingCartButton = React.forwardRef<
  HTMLButtonElement,
  React.ButtonHTMLAttributes<HTMLButtonElement>
>(({ onClick, ...props }, ref) => {
  const isMobile = useIsMobile();
  const itemCount = 123;

  return (
    <button
      ref={ref}
      onClick={(e) => {
        if (onClick) onClick(e);
      }}
      className={cn("inline-flex items-center justify-center", {
        "h-9 bg-green-700 hover:bg-green-800 text-white px-2 rounded-full":
          !isMobile,
      })}
      {...props}
    >
      <span className="sr-only">Shopping Cart</span>
      <MdShoppingCart className={cn("w-6 h-6", { "mr-1": !isMobile })} />
      {!isMobile && (
        <span className="font-semibold mr-2 text-sm">{itemCount}</span>
      )}
    </button>
  );
});

export function ShoppingCartSidebar() {
  return (
    <Sheet>
      <TooltipProvider>
        <Tooltip>
          <TooltipTrigger asChild>
            <SheetTrigger asChild>
              <ShoppingCartButton />
            </SheetTrigger>
          </TooltipTrigger>
          <TooltipContent>Shopping Cart</TooltipContent>
        </Tooltip>
      </TooltipProvider>
      <SheetContent>
        <SheetHeader>
          <SheetTitle>My Shopping Cart</SheetTitle>
          {/* <SheetDescription>Shopping Card Description</SheetDescription> */}
        </SheetHeader>
      </SheetContent>
    </Sheet>
  );
}
