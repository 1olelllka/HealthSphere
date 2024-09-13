import { setProfile } from "@/redux/action/profileActions";
import { AppDispatch, RootState } from "@/redux/store";
import { useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import { Doctor } from "./Doctor";
import { Patient } from "./Patient";
import { ForbiddenPage } from "@/pages/ForbiddenPage";

export const Profile = () => {
  const { data, error } = useSelector((state: RootState) => state.profile);
  const dispatch = useDispatch<AppDispatch>();

  useEffect(() => {
    const getProfile = async () => {
      dispatch(setProfile());
    };

    getProfile();
  }, [dispatch]);

  return (
    <>
      {error && error.status == 403 ? (
        <ForbiddenPage />
      ) : (
        <div className="flex flex-col pt-28 justify-center items-center">
          <div className="container">
            <h1 className="text-4xl">Profile</h1>
            {data && data.user.role === "ROLE_DOCTOR" ? (
              <Doctor data={data} />
            ) : data && data.user.role === "ROLE_PATIENT" ? (
              <Patient data={data} />
            ) : (
              <></>
            )}
          </div>
        </div>
      )}
    </>
  );
};
