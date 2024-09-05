import { useNavigate } from "react-router-dom";

import { Button } from "@/components/ui/button";
import { Link } from "react-router-dom";

export const NotFoundPage = () => {
  const navigate = useNavigate();

  return (
    <>
      <div className="h-screen flex items-center justify-center flex-col">
        <h1 className="text-4xl text-[#040D12]font-light text-center">
          Opps...
        </h1>
        <h1 className="text-9xl text-[#040D12] font-semibold text-center [text-shadow:3px_3px_4px_var(--tw-shadow-color)] shadow-[#93B1A6]">
          404
        </h1>
        <h2 className="text-[#040D12]">Page was not found.</h2>
        <div className="flex row space-x-2 mt-2">
          <Link to="/">
            <Button
              variant={"ghost"}
              className="[box-shadow:1px_1px_2px_var(--tw-shadow-color)] shadow-slate-400 font-semibold dark:hover:bg-[#040D12]"
            >
              Go home
            </Button>
          </Link>
          <span onClick={() => navigate(-1)}>
            <Button
              variant={"ghost"}
              className="[box-shadow:1px_1px_2px_var(--tw-shadow-color)] shadow-slate-400 font-semibold dark:hover:bg-[#040D12]"
            >
              Go back
            </Button>
          </span>
        </div>
      </div>
    </>
  );
};
