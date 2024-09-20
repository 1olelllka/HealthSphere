import { Label } from "../ui/label";
import { Social } from "./Social";
import { Link } from "react-router-dom";

export const Footer = () => {
  return (
    <>
      <div className="bg-[#183D3D] w-full flex justify-center items-center pt-14 pb-2">
        <div className="container grid grid-cols-7">
          <div className="col-span-2">
            <div className="flex flex-row space-x-3">
              <h1 className="text-4xl font-light text-white">HealthSphere</h1>
            </div>
            <Social />
            <div className="pt-8">
              <h1 className="text-md font-light text-gray-400">
                Contact Us:{" "}
                <Label className="text-sm text-white">olehit32@gmail.com</Label>
              </h1>
            </div>
            <div className="text-start text-gray-400 text-sm font-light pt-5">
              <p>© 2024 HealthSphere</p>
            </div>
          </div>
          <div className="col-span-2">
            <div className="text-start pt-2 pl-10">
              <h1 className="text-2xl font-normal text-white">About us</h1>
              <p className="text-sm text-gray-400 font-light">
                HealthSphere is dedicated to simplifying healthcare with
                user-friendly tools for managing health records, appointments,
                and wellness plans. Our goal is to make personalized healthcare
                more accessible, helping you stay on top of your health every
                day.
              </p>
            </div>
          </div>
          <div className="col-span-1"></div>
          <div className="col-span-2">
            <div className="text-start pt-2 pl-10">
              <h1 className="text-2xl font-normal text-white">Useful Links</h1>
              <Link to="/">
                <h6 className="text-sm font-light text-gray-400 hover:text-gray-300">
                  Home
                </h6>
              </Link>
              <Link to="/faq">
                <h6 className="text-sm font-light text-gray-400 hover:text-gray-300">
                  FAQs
                </h6>
              </Link>
              <Link
                to="https://www.apache.org/licenses/LICENSE-2.0"
                target="_blank"
              >
                <h6 className="text-sm font-light text-gray-400 hover:text-gray-300">
                  License
                </h6>
              </Link>
            </div>
          </div>
        </div>
      </div>
    </>
  );
};
