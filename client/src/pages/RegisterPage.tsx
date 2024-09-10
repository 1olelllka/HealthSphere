import { Register } from "@/components/auth/Register";
import { Footer } from "@/components/general/Footer";

export const RegisterPage = () => {
  return (
    <>
      <div className="h-[100vh] flex justify-center items-center">
        <Register />
      </div>
      <div className="bottom-0">
        <Footer />
      </div>
    </>
  );
};
