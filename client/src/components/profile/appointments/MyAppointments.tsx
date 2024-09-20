import { Calendar, momentLocalizer } from "react-big-calendar";
import moment from "moment";
import "react-big-calendar/lib/css/react-big-calendar.css";
import { useDispatch, useSelector } from "react-redux";
import { AppDispatch, RootState } from "@/redux/store";
import { useEffect, useState } from "react";
import {
  setAppointmentsForDoctor,
  setAppointmentsForPatient,
} from "@/redux/action/appointmentActions";
import { TimeSlotDialog } from "./TimeSlotDialog";
import { AppointmentState } from "@/redux/reducers/appointmentsReducer";
import {
  ContextMenu,
  ContextMenuContent,
  ContextMenuItem,
  ContextMenuTrigger,
} from "../../ui/context-menu";
import "react-datepicker/dist/react-datepicker.css";
import { DeleteAppointmentDialog } from "./DeleteAppointmentDialog";
import { PatchAppointmentDialog } from "./PatchAppointmentDialog";
import { useNavigate } from "react-router-dom";
import { ArrowLeft } from "lucide-react";

const localizer = momentLocalizer(moment);

export const MyAppointments = () => {
  const dispatch = useDispatch<AppDispatch>();
  const profile = useSelector((state: RootState) => state.profile.data);
  const data = useSelector((state: RootState) => state.appointment);
  const [dialogOpen, setDialogOpen] = useState<boolean>(false);
  const [selectedId, setSelectedId] = useState<number>(0);
  const [selected, setSelected] = useState<AppointmentState>();
  const [patchOpen, setPatchOpen] = useState<boolean>(false);
  const [deleteOpen, setDeleteOpen] = useState<boolean>(false);
  const navigate = useNavigate();

  const closeDialog = () => {
    setDialogOpen(false);
  };
  const appointments = data.data.map((item) => ({
    ...item,
    endDate: new Date(item.endDate),
    appointmentDate: new Date(item.appointmentDate),
  }));

  useEffect(() => {
    const getAppointmentsForPatient = async (profileId: number) => {
      if (profile.user.role === "ROLE_PATIENT") {
        dispatch(setAppointmentsForPatient(profileId));
      } else if (profile.user.role === "ROLE_DOCTOR") {
        dispatch(setAppointmentsForDoctor(profileId));
      }
    };
    console.log(profile);
    getAppointmentsForPatient(profile.id);
  }, [
    dispatch,
    profile.id,
    profile.user.role,
    dialogOpen,
    patchOpen,
    deleteOpen,
    profile,
  ]);

  return (
    <div className="flex justify-center">
      <div className="container">
        <div className="mt-10 bg-slate-50 p-10 rounded-3xl drop-shadow-lg">
          <div className="flex flex-row gap-4">
            <div className="bg-slate-200 p-1 rounded-lg hover:bg-slate-400 transition-color duration-300">
              <ArrowLeft onClick={() => navigate(-1)} size={32} />
            </div>
            <h1 className="text-4xl">My Schedule</h1>
          </div>
          {appointments && (
            <div className="pt-10 flex justify-center">
              <Calendar
                localizer={localizer}
                events={appointments}
                startAccessor="appointmentDate"
                defaultView="work_week"
                views={{ work_week: true }}
                endAccessor="endDate"
                selectable={true}
                onSelectEvent={(event) => {
                  setDialogOpen(true);
                  setSelectedId(event.id);
                }}
                eventPropGetter={() => {
                  const style: React.CSSProperties = {
                    backgroundColor: "#5C8374",
                    borderRadius: "5px",
                    color: "white",
                    border: "1px solid #183D3D",
                  };
                  return {
                    style: style,
                  };
                }}
                step={30}
                min={new Date(1970, 1, 1, 9, 0, 0)}
                max={new Date(1970, 1, 1, 18, 0, 0)}
                style={{ width: "80%", height: 600, textAlign: "center" }}
                components={{
                  // ignore the error here
                  eventWrapper: ({ event, children }) => (
                    <ContextMenu>
                      <ContextMenuTrigger asChild>
                        <div>{children}</div>
                      </ContextMenuTrigger>
                      <ContextMenuContent>
                        <ContextMenuItem
                          onClick={() => {
                            setSelected(event);
                            setPatchOpen(true);
                          }}
                        >
                          Edit Appointment
                        </ContextMenuItem>
                        <ContextMenuItem
                          onClick={() => {
                            setSelectedId(event.id);
                            setDeleteOpen(true);
                          }}
                        >
                          Delete Appointment
                        </ContextMenuItem>
                      </ContextMenuContent>
                    </ContextMenu>
                  ),
                }}
              />
              <TimeSlotDialog
                open={dialogOpen}
                id={selectedId as number}
                onClose={closeDialog}
              />

              <PatchAppointmentDialog
                patchOpen={patchOpen}
                onClose={() => setPatchOpen(false)}
                selected={selected}
              />

              <DeleteAppointmentDialog
                deleteOpen={deleteOpen}
                onClose={() => {
                  setDeleteOpen(false);
                }}
                selectedId={selectedId as number}
              />
            </div>
          )}
        </div>
      </div>
    </div>
  );
};
