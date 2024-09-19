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
} from "react-icons/fa6";
import { Link, useNavigate } from "react-router-dom";
import { ArrowRightIcon } from "lucide-react";
import { useDispatch, useSelector } from "react-redux";
import { AppDispatch, RootState } from "@/redux/store";
import { logoutProfile } from "@/redux/action/profileActions";

export const MainNavbar = () => {
  const [hoveredIndex, setHoveredIndex] = useState<number | null>(null);
  const [isVisible, setIsVisible] = useState<boolean>(false);
  const profile = useSelector((state: RootState) => state.profile.data);
  const dispatch = useDispatch<AppDispatch>();
  const navigate = useNavigate();

  const logout = () => {
    dispatch(logoutProfile());

    if (localStorage.getItem("persist:profile")) {
      localStorage.removeItem("persist:profile");
    }
    navigate("/login");
  };

  useEffect(() => {
    const handleMouseMove = (e: MouseEvent) => {
      if (e.clientX <= 40) {
        setIsVisible(true);
      } else {
        setIsVisible(false);
      }
    };
    window.addEventListener("mousemove", handleMouseMove);

    return () => {
      window.removeEventListener("mousemove", handleMouseMove);
    };
  }, []);

  const loggedUserMenuItems = [
    {
      icon: <FaUser color="#93B1A6" />,
      text: "Profile",
      link: "/profile",
    },
    {
      icon: <FaRegCalendarDays color="#93B1A6" />,
      text: "Schedule",
      link: "/appointments",
    },
    {
      icon: <FaUserDoctor color="#93B1A6" className="ml-0.5" />,
      text: "Doctors",
      link: "/doctors",
    },
    {
      icon: (
        <FaRightFromBracket
          color="#93B1A6"
          onClick={() => logout()}
          className="ml-1"
        />
      ),
      text: "Logout",
    },
  ];

  const defaultMenuItems = [
    {
      icon: <FaRightToBracket color="#93B1A6" />,
      text: "Login",
      link: "/login",
    },
    {
      icon: <FaUserPlus color="#93B1A6" className="ml-1" />,
      text: "Register",
      link: "/register",
    },
    {
      icon: <FaUserDoctor color="#93B1A6" className="ml-0.5" />,
      text: "Doctors",
      link: "/doctors",
    },
    {
      icon: <FaClipboardQuestion color="#93B1A6" className="ml-0.5" />,
      text: "FAQ",
      link: "/faq",
    },
    {
      icon: <FaRegCopyright color="#93B1A6" className="ml-0.5" />,
      text: "License",
      link: "https://www.apache.org/licenses/LICENSE-2.0",
      external: true,
    },
  ];

  return (
    <>
      <div className="absolute top-60 p-1 bg-[#93B1A6] rounded-r-lg">
        <ArrowRightIcon
          className={`transition-opacity duration-300 ease-in-out text-3xl text-gray-800 ${
            isVisible ? "opacity-0" : "opacity-100"
          }`}
        />
      </div>
      <div
        className={`fixed h-[70vh] rounded-r-lg bg-white z-10 transition-all duration-300 w-[60px] ${
          isVisible ? "opacity-100" : "opacity-0"
        }`}
      >
        <h2 className="text-2xl p-4">
          <Link to="/">
            <GiHealthNormal />
          </Link>
        </h2>
        <nav>
          {profile.firstName.length == 0 ? (
            <ul className="text-gray-800 mt-20">
              {defaultMenuItems.map((item, index) => (
                <div key={index} className="pt-2">
                  <Link
                    to={item.link}
                    target={item.external ? "_blank" : ""}
                    className="grid grid-cols-2 flex flex-row"
                  >
                    <li
                      onMouseEnter={() => setHoveredIndex(index)}
                      onMouseLeave={() => setHoveredIndex(null)}
                      className="text-xl cursor-pointer p-4"
                    >
                      {item.icon}
                    </li>
                    <span
                      className={`transition-all rounded-md ml-4 duration-300 ease-in-out px-2 pt-5 text-black text-xs
                    ${hoveredIndex === index ? "opacity-100" : "opacity-0"}`}
                    >
                      {item.text}
                    </span>
                  </Link>
                </div>
              ))}
            </ul>
          ) : (
            <ul className="flex flex-col text-gray-800 mt-20">
              {loggedUserMenuItems.map((item, index) => (
                <div className="pt-2" key={index}>
                  <Link to={item.link || ""}>
                    <li
                      onMouseEnter={() => setHoveredIndex(index)}
                      onMouseLeave={() => setHoveredIndex(null)}
                      className="grid grid-cols-2 text-xl cursor-pointer p-4"
                    >
                      {item.icon}
                      <span
                        className={`transition-all p-1 rounded-md ml-4 duration-300 ease-in-out px-2 text-black text-xs
                  ${hoveredIndex === index ? "opacity-100" : "opacity-0"}`}
                      >
                        {item.text}
                      </span>
                    </li>
                  </Link>
                </div>
              ))}
            </ul>
          )}
        </nav>
      </div>
    </>
  );
};
