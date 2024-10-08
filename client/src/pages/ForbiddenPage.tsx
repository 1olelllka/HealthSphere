import { useNavigate } from "react-router-dom";

import { Button } from "@/components/ui/button";
import { Link } from "react-router-dom";

export const ForbiddenPage = (props: { message?: string }) => {
  const navigate = useNavigate();
  const message = props.message
    ? props.message
    : "Forbidden. You are not allowed to view this page.";
  console.log(message);

  return (
    <>
      <div className="h-screen flex items-center justify-center flex-col">
        <h1 className="text-9xl text-[#040D12] font-semibold text-center [text-shadow:3px_3px_4px_var(--tw-shadow-color)] shadow-[#93B1A6]">
          403
        </h1>
        <h2 className="text-[#040D12]">{message}</h2>
        <div className="flex row space-x-2 mt-2">
          <Link to="/">
            <Button
              variant={"ghost"}
              className="[box-shadow:1px_1px_2px_var(--tw-shadow-color)] shadow-slate-400 font-semibold hover:bg-[#040D12] hover:text-white"
            >
              Go home
            </Button>
          </Link>
          <span onClick={() => navigate(-1)}>
            <Button
              variant={"ghost"}
              className="[box-shadow:1px_1px_2px_var(--tw-shadow-color)] shadow-slate-400 font-semibold hover:bg-[#040D12] hover:text-white"
            >
              Go back
            </Button>
          </span>
        </div>
      </div>
    </>
  );
};
