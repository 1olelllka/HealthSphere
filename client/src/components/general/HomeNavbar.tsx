import { useState, useEffect } from "react";
import { MainNavbar } from "./MainNavbar";
import { FaArrowDown } from "react-icons/fa6";
import { Events, animateScroll as scroll, scrollSpy } from "react-scroll";

export const Navbar = () => {
  const [fixed, setFixed] = useState(false);

  useEffect(() => {
    Events.scrollEvent.register("begin", () => {});

    Events.scrollEvent.register("end", () => {});

    scrollSpy.update();

    return () => {
      Events.scrollEvent.remove("begin");
      Events.scrollEvent.remove("end");
    };
  }, []);

  const scrollTo = () => {
    scroll.scrollTo(750);
  };

  const setFix = () => {
    if (window.scrollY >= 400) {
      setFixed(true);
    } else {
      setFixed(false);
    }
  };

  window.addEventListener("scroll", setFix);
  return (
    <>
      <div className="bg-gradient-to-r from-[#93B1A6] to-white w-screen h-screen">
        <div className="flex flex-col justify-center items-center h-screen relative">
          <div className="container">
            <div className="flex flex-col hover:text-[#183D3D] transition-all space-y-8">
              <h1 className="flex text-6xl font-bold justify-start">
                HealthSphere{" "}
              </h1>
              <h1 className="flex text-5xl font-light justify-end">
                "...where wellness meets simplicity."
              </h1>
            </div>
            <div className="absolute bottom-8 left-0 right-0 w-screen flex justify-center">
              <a onClick={scrollTo}>
                <FaArrowDown className="text-4xl animate-arrow" />
              </a>
            </div>
            <div
              className={`left-0 top-[100px] fixed transition-all duration-300 ease-in-out z-10 ${
                fixed
                  ? "opacity-100 translate-y-0"
                  : "opacity-0 translate-y-[-20px]"
              }`}
            >
              <MainNavbar />
            </div>
          </div>
        </div>
      </div>
    </>
  );
};
