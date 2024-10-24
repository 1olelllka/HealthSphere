import { SERVER_API } from "@/redux/api/utils";
import { Profile } from "@/redux/reducers/profileReducer";
import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { NotFoundPage } from "@/pages/NotFoundPage";
import { AxiosError } from "axios";
import { useSelector } from "react-redux";
import { RootState } from "@/redux/store";
import { Alert, AlertDescription, AlertTitle } from "@/components/ui/alert";
import { Appointments } from "./Appointments";
import {
  FaMessage,
  FaMobileScreenButton,
  FaRegHospital,
} from "react-icons/fa6";
import female_doctor from "../../assets/female_doctor.png";
import { ArrowLeft, Info } from "lucide-react";
import { LoadingPage } from "@/pages/LoadingPage";
import { ScrollToTop } from "../general/ScrollToTop";

export const DoctorDetail = () => {
  const [data, setDoctor] = useState<Profile | null>(null);
  const profile = useSelector((state: RootState) => state.profile);
  const [loading, setLoading] = useState(true);
  const id = useParams().id;
  const [error, setError] = useState<{
    status: number;
    message: string;
  } | null>(null);
  const navigate = useNavigate();

  useEffect(() => {
    const getDetailDoctor = async (id: string) => {
      try {
        const response = await SERVER_API.get("/doctors/" + id);
        if (response.status === 200) {
          setDoctor(response.data);
          setLoading(false);
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
    getDetailDoctor(id ?? "");
  }, [id]);

  return (
    <>
      {error && error.status == 404 ? (
        <NotFoundPage />
      ) : loading ? (
        <LoadingPage />
      ) : (
        <>
          <div className="flex flex-col pt-10 justify-center items-center relative">
            <div className="container">
              <ScrollToTop />
              {profile.error && profile.error.status != 0 && (
                <Alert className="w-1/3 mx-auto" variant={"destructive"}>
                  <AlertTitle>Error</AlertTitle>
                  <AlertDescription>
                    An error with your profile occurred. Log in to be able to
                    book an appointment.
                  </AlertDescription>
                </Alert>
              )}
              <div className="w-[3.3%] bg-slate-50 p-1 rounded-lg hover:bg-slate-200 transition-color duration-300">
                <ArrowLeft onClick={() => navigate(-1)} size={32} />
              </div>
              <div className="grid grid-cols-3 gap-10 pt-10 pb-10">
                <div className="col-span-1 bg-slate-50 rounded-2xl drop-shadow-lg pb-10 flex flex-row">
                  <div>
                    <div className="flex flex-row p-11 gap-4">
                      {data?.gender &&
                        (data.gender === "MALE" ? (
                          <img
                            src="https://cdn-icons-png.flaticon.com/512/3774/3774299.png"
                            className="rounded-full border border-slate-200 max-w-[60px] max-h-[60px] mt-2"
                          />
                        ) : (
                          <img
                            src={female_doctor}
                            className="rounded-full border border-slate-200 max-w-[60px] max-h-[60px] mt-2"
                          />
                        ))}

                      <h1 className="text-2xl pt-6 font-semibold">
                        {data?.gender &&
                          (data.gender === "MALE" ? "Mr. " : "Mrs. ")}
                        {data?.firstName} {data?.lastName}
                      </h1>
                    </div>
                    <h1 className="text-xl pl-8 font-semibold pt-5">
                      Contact Details:
                    </h1>
                    <div className="flex flex-row gap-2 pl-8 pt-2">
                      <FaMobileScreenButton color="#93B1A6" size={20} />
                      {data?.phoneNumber ? (
                        <h1 className="text-md text-slate-500 font-light">
                          +{data.phoneNumber}
                        </h1>
                      ) : (
                        <h1 className="text-md text-slate-500 font-light">
                          No phone number
                        </h1>
                      )}
                    </div>
                    <div className="flex flex-row gap-2 pl-8 pt-2">
                      <FaMessage color="#93B1A6" size={20} className="pt-1" />
                      <h1 className="text-md text-slate-500 font-light">
                        {data?.user.email}
                      </h1>
                    </div>
                    <div className="flex flex-row gap-2 pl-8 pt-2">
                      <FaRegHospital
                        color="#93B1A6"
                        size={20}
                        className="pt-1"
                      />
                      <h1 className="text-md text-slate-500 font-light">
                        {data?.clinicAddress || "No address"}
                      </h1>
                    </div>
                  </div>
                </div>
                <div className="col-span-2 bg-slate-50 rounded-2xl drop-shadow-lg pl-16 h-64">
                  <h1 className="text-xl font-semibold pt-10">Overview:</h1>
                  <div className="grid grid-rows-2">
                    <div className="grid grid-cols-3">
                      <div className="cols-span-1">
                        <h1 className="text-sm pt-5 font-semibold text-slate-500">
                          Gender:
                        </h1>
                        <h1 className="text-xl font-semibold">
                          {data?.gender &&
                            (data.gender === "MALE" ? "Male" : "Female")}
                        </h1>
                      </div>
                      <div className="col-span-1">
                        <h1 className="text-sm pt-5 font-semibold text-slate-500">
                          Years Of Experience:
                        </h1>
                        <h1 className="text-xl font-semibold">
                          {data?.experienceYears || "Unknown"}
                        </h1>
                      </div>
                      <div className="col-span-1">
                        <h1 className="text-sm pt-5 font-semibold text-slate-500">
                          License Number:
                        </h1>
                        <h1 className="text-xl font-semibold">
                          {data?.licenseNumber || "Unknown"}
                        </h1>
                      </div>
                    </div>
                    <div className="grid grid-cols-3">
                      <div className="cols-span-1">
                        <h1 className="text-sm pt-5 font-semibold text-slate-500">
                          Specializations:
                        </h1>
                        <h1 className="text-xl font-semibold">
                          {data?.specializations?.length
                            ? data.specializations
                                .map((s) => s.specializationName)
                                .join(", ")
                            : "Not Specified"}
                        </h1>
                      </div>
                      <div className="cols-span-1">
                        <h1 className="text-sm pt-5 font-semibold text-slate-500">
                          Registered since:
                        </h1>
                        <h1 className="text-xl font-semibold">
                          {data?.createdAt &&
                            new Date(data.createdAt).toLocaleDateString()}
                        </h1>
                      </div>
                      <div className="cols-span-1">
                        <h1 className="text-sm pt-5 font-semibold text-slate-500">
                          Last Updated:
                        </h1>
                        <h1 className="text-xl font-semibold">
                          {data?.updatedAt &&
                            new Date(data.updatedAt).toLocaleDateString(
                              "en-GB"
                            )}
                        </h1>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
              {profile.data.firstName != "" ? (
                <Appointments />
              ) : (
                <div className="pt-10 flex flex-row gap-4">
                  <Info size={40} className="text-slate-500" />
                  <h1 className="text-2xl font-semibold text-slate-500 pt-2">
                    You must log in to book an appointment
                  </h1>
                </div>
              )}
            </div>
          </div>
        </>
      )}
    </>
  );
};
