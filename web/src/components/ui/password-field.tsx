import * as React from "react";

import { cn } from "@/lib/utils";
import { Eye, EyeOff } from "lucide-react";
import {
  Tooltip,
  TooltipContent,
  TooltipProvider,
  TooltipTrigger,
} from "./tooltip";

interface PasswordFieldProps extends React.InputHTMLAttributes<HTMLDivElement> {
  children: React.ReactElement;
}

const PasswordField = React.forwardRef<HTMLDivElement, PasswordFieldProps>(
  ({ className, children, id, ...props }, ref) => {
    const [visible, setVisible] = React.useState(false);
    const [inputType, setInputType] = React.useState("password");

    React.useEffect(() => {
      setInputType(visible ? "text" : "password");
    }, [visible]);

    const toggleVisiblity = () => setVisible((prev) => !prev);
    const PasswordIcon = visible ? EyeOff : Eye;

    return (
      <div className={cn("flex items-center", className)} ref={ref} {...props}>
        {React.cloneElement(children, {
          id,
          type: inputType,
          className: cn(children.props.className, "pr-9"),
        })}
        <TooltipProvider>
          <Tooltip>
            <TooltipTrigger
              type="button"
              className="flex items-center justify-center w-9 h-9 -ml-9 rounded"
              onClick={toggleVisiblity}
            >
              <PasswordIcon className="text-muted-foreground" />
            </TooltipTrigger>
            <TooltipContent>
              <p>{visible ? "Hide " : "Show "} password</p>
            </TooltipContent>
          </Tooltip>
        </TooltipProvider>
      </div>
    );
  }
);

PasswordField.displayName = "PasswordField";

export { PasswordField };
