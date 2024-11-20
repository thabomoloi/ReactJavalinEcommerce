import { IoSearch } from "react-icons/io5";
import { Input } from "../ui/input";

export function SearchForm() {
  return (
    <div className="w-full">
      <div className="flex items-center">
        <Input
          placeholder="Search products, categories ...."
          name="query"
          className="rounded-full px-5 pr-12 bg-neutral-50"
          required
        />
        <div className="-ml-10  p-0 rounded-full flex items-center">
          <span className="sr-only">Search products</span>
          <IoSearch className="w-6 h-6" />
        </div>
      </div>
    </div>
  );
}
