import { GiHealthNormal } from "react-icons/gi";
import { useEffect, useState } from "react";
import {
  FaUserPlus,
  FaClipboardQuestion,
  FaRegCopyright,
  FaRightToBracket,
  FaUser,
  FaRightFromBracket,
  FaUserDoctor,
  FaRegCalendarDays,
  FaUserGroup,
} from "react-icons/fa6";
import { IoIosArrowForward, IoIosArrowBack } from "react-icons/io";
import { Link, useNavigate } from "react-router-dom";
import { useDispatch, useSelector } from "react-redux";
import { AppDispatch, RootState } from "@/redux/store";
import { logoutProfile, setProfile } from "@/redux/action/profileActions";

export const MainNavbar = () => {
  const [isVisible, setIsVisible] = useState<boolean>(false);
  const { data, error } = useSelector((state: RootState) => state.profile);
  const dispatch = useDispatch<AppDispatch>();
  const navigate = useNavigate();

  useEffect(() => {
    const getProfile = async () => {
      dispatch(setProfile());
    };
    getProfile();
  }, [dispatch]);

  const logout = () => {
    dispatch(logoutProfile());
    if (localStorage.getItem("persist:profile")) {
      localStorage.removeItem("persist:profile");
    }
    navigate("/login");
  };

  const handleVisibility = () => {
    setIsVisible(!isVisible);
  };

  const loggedUserMenuItems = [
    {
      icon: <FaUser color="#93B1A6" className="mr-1" />,
      text: "Profile",
      link: "/profile",
    },
    {
      icon: <FaRegCalendarDays color="#93B1A6" className="ml-2.5" />,
      text: "Schedule",
      link: "/appointments",
    },
    {
      icon: <FaRightFromBracket color="#93B1A6" onClick={logout} />,
      text: "Logout",
    },
    {
      icon: <FaUserDoctor color="#93B1A6" />,
      text: "Doctors",
      link: "/doctors",
    },
  ];

  const defaultMenuItems = [
    {
      icon: <FaRightToBracket color="#93B1A6" className="mr-2" />,
      text: "Login",
      link: "/login",
    },
    {
      icon: <FaUserPlus color="#93B1A6" className="ml-3.5" />,
      text: "Register",
      link: "/register",
    },
    {
      icon: <FaUserDoctor color="#93B1A6" className="ml-1.5" />,
      text: "Doctors",
      link: "/doctors",
    },
    {
      icon: <FaClipboardQuestion color="#93B1A6" className="mr-5" />,
      text: "FAQ",
      link: "/faq",
    },
    {
      icon: <FaRegCopyright color="#93B1A6" className="ml-1" />,
      text: "License",
      link: "https://www.apache.org/licenses/LICENSE-2.0",
      external: true,
    },
  ];

  return (
    <div className="relative flex flex-row h-[70vh]">
      <div
        className={`fixed left-0 h-[70vh] rounded-r-lg bg-white z-10 transition-all duration-300 flex flex-col items-center ${
          isVisible ? "w-[180px]" : "w-[60px]"
        } overflow-hidden`}
      >
        <div className="p-4">
          <Link to="/">
            <GiHealthNormal className="text-4xl text-red-500" />
          </Link>
        </div>

        <nav className="flex-1 flex flex-col justify-center items-center w-full">
          {error ? (
            <ul
              className={`transition-all duration-300 absolute ${
                isVisible ? "left-2" : "left-[-8px]"
              } space-y-4`}
            >
              {" "}
              {defaultMenuItems.map((item, index) => (
                <div key={index} className="flex justify-center items-center">
                  <Link
                    to={item.link}
                    target={item.external ? "_blank" : ""}
                    className="flex flex-row items-center w-full justify-center"
                  >
                    <li className="text-xl cursor-pointer p-4 flex justify-center">
                      {item.icon}
                    </li>
                    <span
                      className={`transition-all duration-300 text-black text-sm ${
                        isVisible ? "opacity-100" : "opacity-0"
                      } ml-4`}
                    >
                      {item.text}
                    </span>
                  </Link>
                </div>
              ))}
            </ul>
          ) : (
            <ul
              className={`transition-all duration-300 absolute ${
                isVisible ? "left-2" : "left-[-8px]"
              } space-y-4`}
            >
              {loggedUserMenuItems.map((item, index) => (
                <div key={index} className="flex justify-center items-center">
                  <Link
                    to={item.link || ""}
                    className="flex flex-row items-center w-full justify-center"
                  >
                    <li className="text-xl cursor-pointer p-4 flex justify-center">
                      {item.icon}
                    </li>
                    <span
                      className={`transition-all duration-300 text-black text-sm ${
                        isVisible ? "opacity-100" : "opacity-0"
                      } ml-4`}
                    >
                      {item.text}
                    </span>
                  </Link>
                </div>
              ))}
              {data.user.role === "ROLE_DOCTOR" && (
                <div className="flex justify-center items-center">
                  <Link
                    to={"/patients"}
                    className="flex flex-row items-center w-full justify-center"
                  >
                    <li className="text-xl cursor-pointer p-4 flex justify-center">
                      <FaUserGroup color="#93B1A6" className="ml-1" />
                    </li>
                    <span
                      className={`transition-all duration-300 text-black text-sm ${
                        isVisible ? "opacity-100" : "opacity-0"
                      } ml-4`}
                    >
                      Patients
                    </span>
                  </Link>
                </div>
              )}
            </ul>
          )}
        </nav>
      </div>

      {/* Arrow for expanding/collapsing the sidebar */}
      <div
        className="absolute inset-y-0 flex items-center rounded-r-lg cursor-pointer"
        onClick={handleVisibility}
      >
        {isVisible ? (
          <IoIosArrowBack
            className={`text-xl text-gray-800 bg-[#93B1A6] transition-all duration-700 ease-in-out rounded-r-lg opacity-0 ml-44 ${
              isVisible && "opacity-100"
            }`}
          />
        ) : (
          <IoIosArrowForward
            className={`text-xl text-gray-800 bg-[#93B1A6] rounded-r-lg ml-14`}
          />
        )}
      </div>
    </div>
  );
};
