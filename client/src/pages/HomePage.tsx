import { useState } from "react";
import { BiHealth } from "react-icons/bi";
import { FaRegUser } from "react-icons/fa";
import { FaUserDoctor } from "react-icons/fa6";
import { GiMedicines } from "react-icons/gi";

import Counter from "react-countup";

export const Home = () => {
  const [visible, setVisible] = useState<boolean>(false);

  const toggleVisible = () => {
    if (window.scrollY >= 290) {
      setVisible(true);
    } else {
      setVisible(false);
    }
  };

  window.addEventListener("scroll", toggleVisible);

  return (
    <>
      <div className="w-full flex justify-center pt-48">
        <div className="container">
          <div
            className={`grid grid-cols-3 transition-all duration-400 ease-in-out ${
              visible
                ? "opacity-100 translate-y-0"
                : "opacity-0 translate-y-[-20px]"
            }`}
          >
            <div className="col-span-2">
              <h1 className="text-5xl">Why HealthSphere?</h1>
              <h1 className="text-lg text-gray-800 pt-10">
                Because your health deserves a complete solution! HealthSphere
                offers all-in-one health management options tailored to your
                needs. From personalized wellness plans to expert guidance, we
                make staying healthy simple, convenient, and empowering. Take
                control of your well-being with HealthSphere—where better health
                meets smarter living.
              </h1>
            </div>
            <div className="col-span-1">
              <img
                src="https://cms-api-in.myhealthcare.co/image/20220910103120.jpeg"
                className="z-0 rounded-3xl"
              />
            </div>
          </div>
          <div className="flex flex-col justify-center items-center pt-20">
            <BiHealth className="text-6xl text-[#93B1A6]" />
            <h1 className="text-4xl text-center pt-10 text-slate-600">
              "Wellness Simplified – Right Here, Right Now"
            </h1>
          </div>
          <div className="pt-28 grid grid-cols-3 gap-10">
            <div className="cols-lg-1 bg-slate-50 rounded-3xl pl-10 pt-12 drop-shadow-lg">
              <h1 className="text-5xl">
                <Counter duration={6} start={0} end={10000} />+
              </h1>
              <h1 className="text-md text-slate-600 pt-5 pb-28">
                Approximate number of our customers.
              </h1>
              <FaRegUser className="text-lg text-slate-400 mb-8" />
            </div>
            <div className="cols-lg-1 bg-slate-50 rounded-3xl px-10 pt-12 drop-shadow-lg">
              <h1 className="text-5xl">
                <Counter duration={6} start={0} end={100} />+
              </h1>
              <h1 className="text-md text-slate-600 pt-5 pb-28">
                Approximate number of our doctors.
              </h1>
              <FaUserDoctor className="text-lg text-slate-400 mb-8" />
            </div>
            <div className="cols-lg-1 bg-slate-50 rounded-3xl px-10 pt-12 drop-shadow-lg">
              <h1 className="text-5xl">
                <Counter duration={6} start={0} end={96} />%
              </h1>
              <h1 className="text-md text-slate-600 pt-5 pb-24">
                Of our customers are satisfied with our services.
              </h1>
              <GiMedicines className="text-lg text-slate-400 mb-9" />
            </div>
          </div>
        </div>
      </div>
    </>
  );
};
