import { RootState } from "@/redux/store";
import { useSelector } from "react-redux";
import { Doctor } from "./Doctor";
import { Patient } from "./Patient";
import { ForbiddenPage } from "@/pages/ForbiddenPage";
import { useNavigate } from "react-router-dom";
import { ArrowLeft } from "lucide-react";
import { UnauthorizedPage } from "@/pages/UnauthorizedPage";
import { LoadingPage } from "@/pages/LoadingPage";

export const Profile = () => {
  const { data, error, loading } = useSelector(
    (state: RootState) => state.profile
  );
  const navigate = useNavigate();

  return (
    <>
      {error && error.status == 403 ? (
        <ForbiddenPage />
      ) : error && error.status === 401 ? (
        <UnauthorizedPage message={error.message} />
      ) : loading ? (
        <LoadingPage />
      ) : (
        <div className="flex flex-col pt-10 justify-center items-center relative">
          <div className="container">
            <div className="flex w-[100px] gap-4 grid grid-cols-2 items-start">
              <div className="bg-slate-50 p-1 rounded-lg hover:bg-slate-200 transition-color duration-300">
                <ArrowLeft onClick={() => navigate("/")} size={32} />
              </div>
              <h1 className="text-4xl mt-1">Profile</h1>
            </div>
            {data && data.user.role === "ROLE_DOCTOR" ? (
              <Doctor />
            ) : data && data.user.role === "ROLE_PATIENT" ? (
              <Patient />
            ) : (
              <></>
            )}
          </div>
        </div>
      )}
    </>
  );
};
