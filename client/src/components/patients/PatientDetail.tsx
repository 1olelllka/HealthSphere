import { MedicalRecords } from "../profile/MedicalRecords";
import { useDispatch } from "react-redux";
import { AppDispatch } from "@/redux/store";
import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { Patient } from "@/redux/reducers/patientsReducer";
import { SERVER_API } from "@/redux/api/utils";
import { AxiosError } from "axios";
import { NotFoundPage } from "@/pages/NotFoundPage";
import { Appointments } from "./Appointments";

export const PatientDetail = () => {
  const id = useParams().id;
  const [data, setData] = useState<Patient>();
  const [err, setErr] = useState<{ status: number; message: string }>();
  const dispatch = useDispatch<AppDispatch>();

  useEffect(() => {
    const getDetail = async (id: number) => {
      try {
        const response = await SERVER_API.get("/patients/" + id);
        if (response.status === 200) {
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
  }, [dispatch, id]);

  return (
    <>
      {err && err.status === 404 ? (
        <NotFoundPage />
      ) : (
        <div className="flex flex-col pt-28 justify-center items-center">
          <div className="container">
            <div className="grid grid-cols-3 gap-10 pt-10">
              <div className="cols-span-1">
                <img
                  src="https://cdn-icons-png.flaticon.com/512/1430/1430453.png"
                  className="rounded-full"
                />
              </div>
              <div className="cols-span-1 space-y-5 bg-slate-50 p-10 rounded-3xl drop-shadow-lg">
                <h1 className="text-2xl">Personal Information</h1>
                <h1 className="text-4xl">
                  {data?.firstName} {data?.lastName}
                </h1>
                <h1 className="text-xl">
                  {data?.dateOfBirth &&
                    new Date(data?.dateOfBirth)
                      .toISOString()
                      .substring(0, 10)}{" "}
                  |{" "}
                  {data?.gender &&
                    (data?.gender === "MALE" ? "Male" : "Female")}
                </h1>
              </div>
              <div className="col-span-1 space-y-2 bg-slate-50 p-10 rounded-3xl drop-shadow-lg">
                <h1 className="text-3xl">Contact Information</h1>
                <h1 className="text-xl text-slate-500">{data?.user?.email}</h1>
                {data?.phoneNumber ? (
                  <h1 className="text-xl text-slate-500">
                    +{data?.phoneNumber}
                  </h1>
                ) : (
                  <h1 className="text-xl text-slate-500">No phone number</h1>
                )}
                <h1 className="text-xl text-slate-500">{data?.address}</h1>
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
