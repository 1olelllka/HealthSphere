import { MedicalRecords } from "../profile/MedicalRecords";
import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { Patient } from "@/redux/reducers/patientsReducer";
import { SERVER_API } from "@/redux/api/utils";
import { AxiosError } from "axios";
import { NotFoundPage } from "@/pages/NotFoundPage";
import { Appointments } from "./Appointments";
import patient_female from "../../assets/patient_female.png";
import { FaHouse, FaMessage, FaMobileScreenButton } from "react-icons/fa6";
import { ArrowLeft } from "lucide-react";
import { UnauthorizedPage } from "@/pages/UnauthorizedPage";

export const PatientDetail = () => {
  const id = useParams().id;
  const [data, setData] = useState<Patient>();
  const [err, setErr] = useState<{ status: number; message: string }>();
  const navigate = useNavigate();

  useEffect(() => {
    const getDetail = async (id: number) => {
      try {
        const response = await SERVER_API.get("/patients/" + id);
        if (response.status === 200) {
          const bloodTypeMap: { [key: string]: string } = {
            OPlus: "O+",
            OMinus: "O-",
            APlus: "A+",
            AMinus: "A-",
            BPlus: "B+",
            BMinus: "B-",
            ABPlus: "AB+",
            ABMinus: "AB-",
          };
          response.data.bloodType =
            bloodTypeMap[
              response.data.bloodType as keyof typeof bloodTypeMap
            ] || "";
          setData(response.data);
        }
      } catch (err) {
        const error = err as AxiosError;
        setErr({
          status: error.response?.status ?? 500,
          message: error.message,
        });
        console.log(err);
      }
    };
    getDetail(parseInt(id ?? "0", 10));
  }, [id]);

  return (
    <>
      {err && err.status === 404 ? (
        <NotFoundPage />
      ) : err && err.status === 401 ? (
        <UnauthorizedPage message={err.message} />
      ) : (
        <div className="flex justify-center">
          <div className="container">
            <div className="w-[3.3%] mt-10 bg-slate-50 p-1 rounded-lg hover:bg-slate-200 transition-color duration-300">
              <ArrowLeft onClick={() => navigate("/")} size={32} />
            </div>
            <div className="grid grid-cols-3 gap-10 pt-10">
              <div className="col-span-1 bg-slate-50 rounded-2xl drop-shadow-lg pb-10 flex flex-row">
                <div>
                  <div className="flex flex-row p-11 gap-4">
                    {data?.gender &&
                      (data.gender === "MALE" ? (
                        <img
                          src="https://cdn-icons-png.flaticon.com/512/1430/1430453.png"
                          className="rounded-full border border-slate-200"
                          width={75}
                          height={75}
                        />
                      ) : (
                        <img
                          src={patient_female}
                          className="rounded-full border border-slate-200 mt-2"
                          width={60}
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
                    <FaHouse color="#93B1A6" size={20} className="pt-1" />
                    <h1 className="text-md text-slate-500 font-light">
                      {data?.address || "No address"}
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
                        Date of Birth:
                      </h1>
                      <h1 className="text-xl font-semibold">
                        {data?.dateOfBirth &&
                          new Date(data.dateOfBirth)
                            .toISOString()
                            .substring(0, 10)}
                      </h1>
                    </div>
                    <div className="col-span-1">
                      <h1 className="text-sm pt-5 font-semibold text-slate-500">
                        BloodType:
                      </h1>
                      <h1 className="text-xl font-semibold">
                        {data?.bloodType || "Unknown"}
                      </h1>
                    </div>
                  </div>
                  <div className="grid grid-cols-3">
                    <div className="cols-span-1">
                      <h1 className="text-sm pt-5 font-semibold text-slate-500">
                        Allergies:
                      </h1>
                      <h1 className="text-xl font-semibold">
                        {data?.allergies || "None"}
                      </h1>
                    </div>
                    <div className="cols-span-1">
                      <h1 className="text-sm pt-5 font-semibold text-slate-500">
                        Registered since:
                      </h1>
                      <h1 className="text-xl font-semibold">
                        {data?.createdAt &&
                          new Date(data.createdAt)
                            .toISOString()
                            .substring(0, 10)}
                      </h1>
                    </div>
                    <div className="cols-span-1">
                      <h1 className="text-sm pt-5 font-semibold text-slate-500">
                        Last Updated:
                      </h1>
                      <h1 className="text-xl font-semibold">
                        {data?.updatedAt &&
                          new Date(data.updatedAt)
                            .toISOString()
                            .substring(0, 10)}
                      </h1>
                    </div>
                  </div>
                </div>
              </div>
            </div>
            <MedicalRecords id={data?.id as number} />
            <Appointments />
          </div>
        </div>
      )}
    </>
  );
};
