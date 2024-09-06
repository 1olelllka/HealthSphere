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
          <div className="flex justify-center w-full pt-5">
            <div className="fixed flex flex-row grid grid-cols-9 ">
              <MainNavbar />
            </div>
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
