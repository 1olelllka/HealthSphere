import { SERVER_API } from "@/redux/api/utils";
import { Profile } from "@/redux/reducers/profileReducer";
import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { HoverCard, HoverCardContent } from "../ui/hover-card";
import { HoverCardTrigger } from "@radix-ui/react-hover-card";
import { Button } from "../ui/button";
import { NotFoundPage } from "@/pages/NotFoundPage";
import { AxiosError } from "axios";
import { useDispatch, useSelector } from "react-redux";
import { AppDispatch, RootState } from "@/redux/store";
import { setProfile } from "@/redux/action/profileActions";
import { Alert, AlertDescription, AlertTitle } from "@/components/ui/alert";
import { Appointments } from "./Appointments";

export const DoctorDetail = () => {
  const [doctor, setDoctor] = useState<Profile | null>(null);
  const profile = useSelector((state: RootState) => state.profile);
  const dispatch = useDispatch<AppDispatch>();
  const id = useParams().id;
  const [error, setError] = useState<{
    status: number;
    message: string;
  } | null>(null);

  useEffect(() => {
    const getDetailDoctor = async (id: string) => {
      try {
        const response = await SERVER_API.get("/doctors/" + id);
        if (response.status === 200) {
          setDoctor(response.data);
        }
      } catch (err) {
        const axiosError = err as AxiosError;
        if (axiosError.response?.status == 404) {
          setError({
            status: 404,
            message: "Doctor with such id was not found.",
          });
        } else if (axiosError.response?.status == 403) {
          setError({
            status: 403,
            message: "You don't have permission to access this page.",
          });
        }
        console.log(err);
      }
    };
    const getProfile = async () => {
      dispatch(setProfile());
    };
    getProfile();
    getDetailDoctor(id ?? "");
  }, [dispatch, id]);

  console.log(profile);
  return (
    <>
      {error?.status === 404 ? (
        <NotFoundPage />
      ) : (
        <div className="flex flex-col pt-10 justify-center items-center">
          <div className="container">
            {profile.error && profile.error.status != 0 && (
              <Alert className="w-1/3 mx-auto" variant={"destructive"}>
                <AlertTitle>Error</AlertTitle>
                <AlertDescription>
                  An error with your profile occurred. Log in to be able to book
                  an appointment.
                </AlertDescription>
              </Alert>
            )}
            <div className="grid grid-cols-3 gap-10 pt-10">
              <div className="cols-span-1">
                <img
                  src="https://cdn-icons-png.flaticon.com/512/3774/3774299.png"
                  className="rounded-full"
                />
              </div>
              <div className="cols-span-1 space-y-4 bg-slate-50 p-10 rounded-3xl drop-shadow-lg">
                <h1 className="text-2xl">Personal Information</h1>
                <h1 className="text-4xl">
                  Dr. {doctor?.firstName} {doctor?.lastName}
                </h1>
                <div className="flex flex-row space-x-4">
                  {doctor?.specializations?.map((item) => (
                    <div className="bg-slate-200 rounded-md p-2 ">
                      <h1 key={item.id} className="text-md text-slate-950">
                        {item.specializationName}{" "}
                      </h1>
                    </div>
                  ))}
                </div>
                {doctor?.experienceYears && (
                  <h1 className="text-xl">
                    {doctor?.experienceYears} years of practical experience
                  </h1>
                )}
                <div className="absolute bottom-0 flex flex-row gap-4 pb-8">
                  <HoverCard>
                    <HoverCardTrigger>
                      <Button
                        variant={"ghost"}
                        className="[box-shadow:1px_1px_2px_var(--tw-shadow-color)] shadow-slate-400 font-semibold hover:bg-[#040D12] hover:text-white"
                      >
                        Show Additional Information
                      </Button>
                    </HoverCardTrigger>
                    <HoverCardContent>
                      <div className="flex flex-col">
                        <h1>License: {doctor?.licenseNumber}</h1>
                        {doctor?.createdAt && (
                          <h1>
                            Registered:{" "}
                            {new Date(doctor.createdAt)
                              .toISOString()
                              .substring(0, 10)}
                          </h1>
                        )}
                        {doctor?.updatedAt && (
                          <h1>
                            Last Update:{" "}
                            {new Date(doctor.updatedAt)
                              .toISOString()
                              .substring(0, 10)}
                          </h1>
                        )}
                      </div>
                    </HoverCardContent>
                  </HoverCard>
                </div>
              </div>
              <div className="col-span-1 space-y-2 bg-slate-50 p-10 rounded-3xl drop-shadow-lg">
                <h1 className="text-2xl">Contact Information</h1>
                <h1 className="text-xl text-slate-500">{doctor?.user.email}</h1>
                {doctor?.phoneNumber ? (
                  <h1 className="text-xl text-slate-500">
                    +{doctor.phoneNumber}
                  </h1>
                ) : (
                  <h1 className="text-xl text-slate-500">No phone number</h1>
                )}
                <h1 className="text-xl text-slate-500">
                  {doctor?.clinicAddress}
                </h1>
              </div>
            </div>
            {profile.data.id != 0 && <Appointments id={doctor?.id as number} />}
          </div>
        </div>
      )}
    </>
  );
};
