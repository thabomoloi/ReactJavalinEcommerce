import logo_webp from "@/assets/images/logo.webp";
import logo_png from "@/assets/images/logo.png";
import React from "react";

type LogoProps = Omit<
  React.ImgHTMLAttributes<HTMLImageElement>,
  "src" | "alt"
> & {
  webp?: string; // WebP image source
  fallback?: string; // Fallback image source (e.g., PNG, JPEG)
  alt?: string;
};

const Logo: React.FC<LogoProps> = ({
  webp = logo_webp,
  fallback = logo_png,
  alt = "Oasis Nourish Logo",
  ...props
}) => {
  return (
    <picture>
      <source srcSet={webp} type="image/webp" />
      <img src={fallback} alt={alt} {...props} />
    </picture>
  );
};

export { Logo };
