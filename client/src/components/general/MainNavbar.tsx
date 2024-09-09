import { RootState } from "@/redux/store";
import { useSelector } from "react-redux";
import { Link, useNavigate } from "react-router-dom";

export const MainNavbar = () => {
  const profile = useSelector((state: RootState) => state.profile);
  const navigate = useNavigate();

  return (
    <>
      <h1
        className="text-2xl font-bold cursor-pointer hover:text-[#183D3D] transition-all col-span-6"
        onClick={() => window.scrollTo(0, 0)}
      >
        HealthSphere
      </h1>
      <div className="col-span-1">
        <Link to={"/faq"}>
          <h1 className="text-xl text-[#040D12] cursor-pointer">FAQs</h1>
        </Link>
      </div>
      <div className="col-span-1">
        <h1
          className="text-xl text-[#040D12] cursor-pointer"
          onClick={() =>
            window.open("https://www.apache.org/licenses/LICENSE-2.0", "_blank")
          }
        >
          License
        </h1>
      </div>
      {!profile.firstName && (
        <div className="flex flex-row gap-5 justify-end col-span-1">
          <h1
            className="text-xl text-[#040D12] cursor-pointer"
            onClick={() => navigate("/login")}
          >
            Log In
          </h1>
          <h1 className="text-xl text-[#040D12]">Sign Up</h1>
        </div>
      )}
    </>
  );
};
