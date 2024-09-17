import {
  Dialog,
  DialogContent,
  DialogFooter,
  DialogTitle,
} from "../../ui/dialog";
import { Button } from "../../ui/button";
import { useEffect, useState } from "react";
import { SERVER_API } from "@/redux/api/utils";
import { AxiosError } from "axios";

interface AppointmentState {
  id: number;
  patient: {
    id: number;
    firstName: string;
    lastName: string;
  };
  doctor: {
    id: number;
    firstName: string;
    lastName: string;
  };
  appointmentDate: string;
  endDate: string;
  allDay: boolean;
  status: string;
  reason: string;
  createdAt: Date;
  updatedAt: Date;
}

export const TimeSlotDialog = (props: {
  open: boolean;
  id: number;
  onClose: () => void;
}) => {
  const [appointment, setAppointment] = useState<AppointmentState>();
  useEffect(() => {
    const getSpecificAppointment = async (id: number) => {
      try {
        const response = await SERVER_API.get(`/patients/appointments/${id}`);
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

  return (
    <>
      <Dialog open={props.open}>
        <DialogContent>
          <DialogTitle>Appointment #{props.id} Information</DialogTitle>
          <h1>
            Appointment Date:{" "}
            {new Date(
              appointment?.appointmentDate as string
            ).toLocaleDateString()}{" "}
            at{" "}
            {new Date(appointment?.appointmentDate as string)
              .toLocaleTimeString()
              .substring(0, 5)}
          </h1>
          <h1>
            Doctor: {appointment?.doctor?.firstName}{" "}
            {appointment?.doctor?.lastName}
          </h1>
          <h1>
            Patient: {appointment?.patient?.firstName}{" "}
            {appointment?.patient?.lastName}
          </h1>
          <h1>Reason: {appointment?.reason}</h1>
          <h1
            className={
              appointment?.status === "SCHEDULED"
                ? "text-yellow-500"
                : appointment?.status === "COMPLETED"
                ? "text-green-500"
                : "text-red-500"
            }
          >
            Status: {appointment?.status}
          </h1>
          <DialogFooter>
            <Button variant={"default"} onClick={props.onClose}>
              Close{" "}
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </>
  );
};
