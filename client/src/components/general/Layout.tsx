import { Navbar } from "@/components/general/HomeNavbar";
import { MainNavbar } from "@/components/general/MainNavbar";
import { Footer } from "@/components/general/Footer";
import { ScrollToTop } from "./ScrollToTop";

export const Layout = ({
  children,
  main,
}: {
  children: React.ReactNode;
  main?: boolean;
}) => (
  <>
    <ScrollToTop />
    <div>
      {main ? (
        <Navbar />
      ) : (
        <div className="left-0 top-[100px] fixed transition-all duration-300 ease-in-out z-10">
          <MainNavbar />
        </div>
      )}
    </div>
    {children}
    <div className="flex bottom-0 mt-20">
      <Footer />
    </div>
  </>
);
