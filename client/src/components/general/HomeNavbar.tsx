import { useState } from "react";
import { MainNavbar } from "./MainNavbar";

export const Navbar = () => {
  const [fixed, setFixed] = useState(false);

  const setFix = () => {
    if (window.scrollY >= 150) {
      setFixed(true);
    } else {
      setFixed(false);
    }
  };

  window.addEventListener("scroll", setFix);
  return (
    <>
      <div className="bg-gradient-to-r from-[#93B1A6] to-white w-full flex justify-center pb-10">
        <div className="container">
          <div className="relative">
            <div
              className={`transition-all duration-300 ease-in-out ${
                fixed
                  ? "opacity-0 translate-y-[-20px]"
                  : "opacity-100 translate-y-0 pt-10"
              }`}
            >
              <div className="flex flex-row space-x-5 justify-end">
                <h1 className="text-xl text-[#040D12]">Log In</h1>
                <h1 className="text-xl text-[#040D12]">Sign Up</h1>
              </div>
            </div>
            <div
              className={`fixed transition-all duration-300 ease-in-out flex flex-row grid grid-cols-9 ${
                fixed
                  ? "opacity-100 translate-y-0"
                  : "opacity-0 translate-y-[-20px]"
              }`}
            >
              <MainNavbar />
            </div>
          </div>
          <div className="flex flex-col hover:text-[#183D3D] transition-all pt-20 space-y-8">
            <h1 className="flex text-6xl font-bold justify-start">
              HealthSphere{" "}
            </h1>
            <h1 className="flex text-5xl font-light justify-end">
              "...where wellness meets simplicity."
            </h1>
          </div>
        </div>
      </div>
    </>
  );
};
