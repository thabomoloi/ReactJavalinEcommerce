import { AccountDropdownMenu } from "../menu/account-menu";
import { SearchForm } from "./search-form";
import { useIsMobile } from "@/hooks/use-mobile";
import { Logo } from "../logo";
import { cn } from "@/lib/utils";
import { Link } from "react-router-dom";
import { ShoppingCartSidebar } from "../sidebars/shopping-cart-sidebar";
import { MobileNavSidebar } from "../sidebars/mobile-nav-sidebar";
import { IoMenu } from "react-icons/io5";

function MenuButton() {
  return (
    <MobileNavSidebar>
      <IoMenu className="w-6 h-6" />
    </MobileNavSidebar>
  );
}

// function AccountButton({ isMobile }: { isMobile: boolean }) {
//   return (
//     <AccountDropdownMenu>
//       {isMobile && <FaUserCircle className="w-6 h-6" />}
//       {!isMobile && (
//         <React.Fragment>
//           <button>
//             {" "}
//             <FaUserCircle className="w-6 h-6" /> My Account
//           </button>
//         </React.Fragment>
//       )}
//     </AccountDropdownMenu>
//   );
// }

export function Header({ widthClassName }: { widthClassName: string }) {
  const isMobile = useIsMobile();
  if (isMobile)
    return (
      <header className="w-full text-foreground/80">
        <div className="bg-background">
          <div
            className={cn(
              "flex items-center justify-between gap-4 mx-auto",
              widthClassName,
              "py-4"
            )}
          >
            <MenuButton />
            <Link to="/">
              <Logo className="w-40" />
            </Link>
            <div className="flex gap-4 items-center">
              <ShoppingCartSidebar />
              <AccountDropdownMenu />
            </div>
          </div>
          <div className="flex justify-center bg-green-700 shadow">
            <div className={cn(widthClassName, "py-4")}>
              <SearchForm />
            </div>
          </div>
        </div>
      </header>
    );

  return (
    <header className="w-full text-foreground/80">
      <div className="bg-background">
        <div
          className={cn(
            "flex items-center justify-between gap-4 mx-auto",
            widthClassName,
            "py-4"
          )}
        >
          <Link to="/">
            <Logo className="w-40" />
          </Link>

          <div className="flex gap-2 items-center flex-grow ml-4">
            <div className="flex-grow max-w-80 ml-auto mr-2">
              <SearchForm />
            </div>
            <AccountDropdownMenu />
            <ShoppingCartSidebar />
          </div>
        </div>
        <div className="flex justify-center bg-green-700 shadow">
          <div className={cn(widthClassName, "py-4")}>
            Home | Products | Orders
          </div>
        </div>
      </div>
    </header>
  );
}
