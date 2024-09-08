import { Navbar } from "@/components/general/HomeNavbar";
import { Footer } from "@/components/general/Footer";
import { MainNavbar } from "@/components/general/MainNavbar";

export const Layout = ({
  children,
  main,
}: {
  children: React.ReactNode;
  main?: boolean;
}) => (
  <>
    <div>
      <div className="flex items-start">
        {main ? (
          <Navbar />
        ) : (
          <div className="top-0 left-0 right-0 px-16 pt-5 pb-4 bg-primary border-b-2 border-gray-400 z-10 fixed flex flex-row grid grid-cols-9">
            <MainNavbar />
          </div>
        )}
      </div>
      {children}
      <div className="flex bottom-0 mt-20">
        <Footer />
      </div>
    </div>
  </>
);
