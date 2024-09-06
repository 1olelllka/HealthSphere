import { useState } from "react";

export const WhyHealthSphere = () => {
  const [visible, setVisible] = useState<boolean>(false);

  const toggleVisible = () => {
    if (window.scrollY >= 300) {
      setVisible(true);
    } else {
      setVisible(false);
    }
  };

  window.addEventListener("scroll", toggleVisible);

  return (
    <div
      className={`grid grid-cols-3 transition-all duration-500 ease-in-out ${
        visible ? "opacity-100 translate-y-0" : "opacity-0 translate-y-[-20px]"
      }`}
    >
      <div className="col-span-2">
        <h1 className="text-5xl">Why HealthSphere?</h1>
        <h1 className="text-lg text-gray-800 pt-10">
          Because your health deserves a complete solution! HealthSphere offers
          all-in-one health management options tailored to your needs. From
          personalized wellness plans to expert guidance, we make staying
          healthy simple, convenient, and empowering. Take control of your
          well-being with HealthSphere—where better health meets smarter living.
        </h1>
      </div>
      <div className="col-span-1">
        <img
          src="https://cms-api-in.myhealthcare.co/image/20220910103120.jpeg"
          className="z-0 rounded-3xl"
        />
      </div>
    </div>
  );
};
