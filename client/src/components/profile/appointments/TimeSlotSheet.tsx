import { Button } from "../../ui/button";
import { useEffect, useState } from "react";
import { SERVER_API } from "@/redux/api/utils";
import { AxiosError } from "axios";
import { useNavigate } from "react-router-dom";
import { useSelector } from "react-redux";
import { RootState } from "@/redux/store";
import {
  Sheet,
  SheetContent,
  SheetHeader,
  SheetTitle,
} from "@/components/ui/sheet";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Specialization } from "@/redux/reducers/specializationReducer";

interface AppointmentState {
  id: number;
  patient: {
    id: number;
    firstName: string;
    lastName: string;
    user: {
      email: string;
    };
  };
  doctor: {
    id: number;
    firstName: string;
    lastName: string;
    user: {
      email: string;
    };
    specializations: Specialization[];
  };
  appointmentDate: string;
  endDate: string;
  allDay: boolean;
  status: string;
  reason: string;
  createdAt: Date;
  updatedAt: Date;
}

export const TimeSlotSheet = (props: {
  open: boolean;
  id: number;
  onClose: () => void;
}) => {
  const [appointment, setAppointment] = useState<AppointmentState>();
  const profile = useSelector((state: RootState) => state.profile.data);
  const navigate = useNavigate();
  useEffect(() => {
    const getSpecificAppointment = async (id: number) => {
      try {
        const response = await SERVER_API.get(`/appointments/${id}`);
        if (response.status === 200) {
          setAppointment(response.data);
        }
      } catch (err) {
        const axiosErr = err as AxiosError;
        if (axiosErr.response?.status == 403) {
          console.log("You don't have permission to access this page.");
        } else if (axiosErr.response?.status == 404) {
          console.log("Appointment not found.");
        }
      }
    };
    getSpecificAppointment(props.id);
  }, [props.id]);

  const createMedicalRecord = async (values: {
    patient: { id: number };
    diagnosis: string;
    recordDate: string;
  }) => {
    try {
      const response = await SERVER_API.post(
        "/patient/medical-records",
        values
      );
      if (response.status === 201) {
        navigate(`/medical-records/${response.data.id}`);
      }
    } catch (err) {
      const axiosErr = err as AxiosError;
      if (axiosErr.response?.status == 403) {
        console.log("You don't have permission to access this page.");
      }
      console.log(err);
    }
  };

  return (
    <>
      <Sheet open={props.open} onOpenChange={props.onClose}>
        <SheetContent>
          <SheetHeader>
            <SheetTitle className="text-3xl">Appointment Details</SheetTitle>
          </SheetHeader>
          <Card className="mt-5 border-0 bg-primary drop-shadow-lg">
            <CardContent className="p-0">
              <CardHeader className="p-3">
                <CardTitle className="text-xl font-normal">
                  Information
                </CardTitle>
              </CardHeader>
              <div className="space-y-2 pb-3">
                <div className="flex flex-row justify-between">
                  <h1 className="text-sm pl-3 font-light">Appointment ID:</h1>
                  <h1 className="text-sm pr-5 font-light">{appointment?.id}</h1>
                </div>
                <div className="flex flex-row justify-between">
                  <h1 className="text-sm pl-3 font-light">Patient:</h1>
                  {profile.user.role === "ROLE_DOCTOR" ? (
                    <h1
                      className="text-sm pr-5 font-light underline hover:cursor-pointer hover:text-blue-500 transition-color"
                      onClick={() =>
                        navigate(`/patients/${appointment?.patient.id}`)
                      }
                    >
                      {appointment?.patient?.firstName}{" "}
                      {appointment?.patient?.lastName}
                    </h1>
                  ) : (
                    <h1 className="text-sm pr-5 font-light">
                      {appointment?.patient?.firstName}{" "}
                      {appointment?.patient?.lastName}
                    </h1>
                  )}
                </div>
                <div className="flex flex-row justify-between">
                  <h1 className="text-sm pl-3 font-light">Patient Email:</h1>
                  <h1 className="text-sm pr-5 font-light">
                    {appointment?.patient?.user.email}
                  </h1>
                </div>
                <div className="flex flex-row justify-between">
                  <h1 className="text-sm pl-3 font-light">Date:</h1>
                  <h1 className="text-sm pr-5 font-light">
                    {new Date(
                      appointment?.appointmentDate as string
                    ).toLocaleString()}
                  </h1>
                </div>
                <div className="flex flex-row justify-between">
                  <h1 className="text-sm pl-3 font-light">Duration:</h1>
                  <h1 className="text-sm pr-5 font-light">30 minutes</h1>
                </div>
                <div className="flex flex-row justify-between">
                  <h1 className="text-sm pl-3 font-light">Reason:</h1>
                  <h1 className="text-sm pr-5 font-light">
                    {appointment?.reason || "No reason provided"}
                  </h1>
                </div>
                <div className="flex flex-row justify-between">
                  <h1 className="text-sm pl-3 font-light">Status:</h1>
                  <h1
                    className={`text-sm pr-5 font-bold ${
                      appointment?.status === "SCHEDULED"
                        ? "text-yellow-500"
                        : appointment?.status === "COMPLETED"
                        ? "text-green-500"
                        : "text-red-500"
                    }`}
                  >
                    {appointment?.status}
                  </h1>
                </div>
              </div>
            </CardContent>
          </Card>
          <Card className="mt-5 border-0 bg-primary drop-shadow-lg">
            <CardContent className="p-0">
              <CardHeader className="p-3">
                <CardTitle className="text-xl font-normal">Doctor</CardTitle>
              </CardHeader>
              <div className="space-y-2 pb-3">
                <div className="flex flex-row justify-between">
                  <h1 className="text-sm pl-3 font-light">Doctor</h1>
                  {profile.user.role === "ROLE_PATIENT" ? (
                    <h1
                      className="text-sm pr-5 font-light underline hover:cursor-pointer hover:text-blue-500 transition-color"
                      onClick={() =>
                        navigate(`/doctors/${appointment?.doctor.id}`)
                      }
                    >
                      Dr. {appointment?.doctor?.firstName}{" "}
                      {appointment?.doctor?.lastName}
                    </h1>
                  ) : (
                    <h1 className="text-sm pr-5 font-light">
                      Dr. {appointment?.doctor?.firstName}{" "}
                      {appointment?.doctor?.lastName}
                    </h1>
                  )}
                </div>
                <div className="flex flex-row justify-between">
                  <h1 className="text-sm pl-3 font-light">Doctor Email:</h1>
                  <h1 className="text-sm pr-5 font-light">
                    {appointment?.doctor?.user.email}
                  </h1>
                </div>
                <div className="flex flex-row justify-between">
                  <h1 className="text-sm pl-3 font-light">Specializations:</h1>
                  <h1 className="text-sm pr-5 font-light">
                    {appointment?.doctor?.specializations?.map(
                      (spec) => spec.specializationName + " | "
                    )}
                  </h1>
                </div>
              </div>
            </CardContent>
          </Card>
          {profile.user.role === "ROLE_DOCTOR" && (
            <Button
              variant={"default"}
              className="mt-5"
              onClick={() => {
                createMedicalRecord({
                  patient: { id: appointment?.patient?.id as number },
                  diagnosis: "Edit Diagnosis in Edit Medical Record",
                  recordDate: new Date().toISOString().substring(0, 10),
                });
              }}
            >
              Create the Medical Record
            </Button>
          )}
        </SheetContent>
      </Sheet>
    </>
  );
};
