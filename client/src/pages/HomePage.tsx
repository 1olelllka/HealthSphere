import { WhyHealthSphere } from "@/components/home/WhyHealthSphere";
import { Stats } from "@/components/home/Stats";
import { Services } from "@/components/home/Services";

export const Home = () => {
  return (
    <>
      <div className="w-full flex justify-center pt-48">
        <div className="container">
          <WhyHealthSphere />
          <Stats />
          <Services />
        </div>
      </div>
    </>
  );
};
