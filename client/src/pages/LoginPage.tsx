import { Login } from "@/components/auth/Login";
import { Footer } from "@/components/general/Footer";

export const LoginPage = () => {
  return (
    <>
      <div className="h-[80vh] flex justify-center items-center">
        <Login />
      </div>
      <div className="bottom-0">
        <Footer />
      </div>
    </>
  );
};
