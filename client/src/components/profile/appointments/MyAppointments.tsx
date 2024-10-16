import "react-big-calendar/lib/css/react-big-calendar.css";
import { useDispatch, useSelector } from "react-redux";
import { AppDispatch, RootState } from "@/redux/store";
import { useEffect, useState } from "react";
import {
  setAppointmentsForDoctor,
  setAppointmentsForPatient,
} from "@/redux/action/appointmentActions";
import { TimeSlotSheet } from "./TimeSlotSheet";
import { AppointmentState } from "@/redux/reducers/appointmentsReducer";
import "react-datepicker/dist/react-datepicker.css";
import { DeleteAppointmentDialog } from "./DeleteAppointmentDialog";
import { PatchAppointmentDialog } from "./PatchAppointmentDialog";
import { useNavigate, useSearchParams } from "react-router-dom";
import { ArrowLeft } from "lucide-react";
import { UnauthorizedPage } from "@/pages/UnauthorizedPage";
import { LoadingPage } from "@/pages/LoadingPage";
import { Alert, AlertDescription, AlertTitle } from "@/components/ui/alert";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import {
  Pagination,
  PaginationContent,
  PaginationItem,
  PaginationNext,
  PaginationPrevious,
} from "@/components/ui/pagination";
import { Button } from "@/components/ui/button";
import { ForbiddenPage } from "@/pages/ForbiddenPage";

export const MyAppointments = () => {
  const dispatch = useDispatch<AppDispatch>();
  const profile = useSelector((state: RootState) => state.profile);
  const data = useSelector((state: RootState) => state.appointment);
  const [dialogOpen, setDialogOpen] = useState<boolean>(false);
  const [selectedId, setSelectedId] = useState<number>(0);
  const [selected, setSelected] = useState<AppointmentState>();
  const [patchOpen, setPatchOpen] = useState<boolean>(false);
  const [deleteOpen, setDeleteOpen] = useState<boolean>(false);
  const [searchParams] = useSearchParams();
  const page = parseInt(searchParams.get("page") || "0", 10);
  const navigate = useNavigate();

  const closeDialog = () => {
    setDialogOpen(false);
  };
  const appointments = data.content.map((item) => ({
    ...item,
    endDate: new Date(item.endDate),
    appointmentDate: new Date(item.appointmentDate),
  }));

  const updatePage = (newPage: number) => {
    const params = new URLSearchParams(window.location.search);
    params.set("page", newPage.toString());
    navigate(`?${params.toString()}`, { replace: true });
  };

  useEffect(() => {
    const getAppointmentsForPatient = async (profileId: number) => {
      if (profile.data.user.role === "ROLE_PATIENT") {
        dispatch(
          setAppointmentsForPatient({ patientId: profileId, page: page })
        );
      } else if (profile.data.user.role === "ROLE_DOCTOR") {
        dispatch(
          setAppointmentsForDoctor({
            doctorId: profileId,
            from: undefined,
            to: undefined,
            page: page,
          })
        );
      }
    };
    console.log(profile);
    getAppointmentsForPatient(profile.data.id);
  }, [dispatch, profile.data.id, profile.data.user.role, profile, page]);
  console.log(data.error?.status);

  return (
    <>
      {data.error && data.error.status === 401 ? (
        <UnauthorizedPage message={data.error.message} />
      ) : profile.error && profile.error.status === 403 ? (
        <ForbiddenPage />
      ) : data.loading ? (
        <LoadingPage />
      ) : (
        <div className="flex justify-center z-20">
          <div className="container">
            {data.success && data.success.length > 0 && (
              <Alert className="w-1/3 mx-auto mt-10">
                <AlertTitle>Success</AlertTitle>
                <AlertDescription>{data.success}</AlertDescription>
              </Alert>
            )}
            {data.error && data.error.status === 400 && (
              <Alert className="w-1/3 mx-auto mt-10" variant={"destructive"}>
                <AlertTitle>Error</AlertTitle>
                <AlertDescription>{data.error.message}</AlertDescription>
              </Alert>
            )}
            <div className="mt-10 p-10 rounded-3xl drop-shadow-lg">
              <div className="flex flex-row gap-4">
                <div className="bg-slate-200 p-1 rounded-lg hover:bg-slate-400 transition-color duration-300">
                  <ArrowLeft onClick={() => navigate(-1)} size={32} />
                </div>
                <h1 className="text-4xl">Appointments</h1>
              </div>
              {appointments && (
                <div className="pt-10">
                  <Table>
                    <TableHeader>
                      <TableRow>
                        <TableHead className="w-[100px]">ID</TableHead>
                        <TableHead>Doctor</TableHead>
                        <TableHead>Status</TableHead>
                        <TableHead>Date</TableHead>
                        <TableHead>Duration</TableHead>
                        <TableHead className="w-[150px]"></TableHead>
                      </TableRow>
                    </TableHeader>
                    <TableBody>
                      {appointments.map((item) => (
                        <>
                          <TableRow
                            className="bg-slate-100 hover:bg-slate-50"
                            key={item.id}
                          >
                            <TableCell className="font-medium">
                              {item.id}
                            </TableCell>
                            <TableCell>
                              {" "}
                              Dr. {item?.doctor?.firstName}{" "}
                              {item?.doctor?.lastName}
                            </TableCell>
                            <TableCell
                              className={`font-bold ${
                                item?.status === "SCHEDULED"
                                  ? "text-yellow-500"
                                  : item?.status === "COMPLETED"
                                  ? "text-green-500"
                                  : "text-red-500"
                              }`}
                            >
                              {item.status}
                            </TableCell>
                            <TableCell>
                              {new Date(item?.appointmentDate).toLocaleString()}
                            </TableCell>
                            <TableCell>30 min</TableCell>
                            <TableCell className="flex flex-row gap-2 items-center">
                              <Button
                                className="px-5 my-0 py-0 h-7 bg-slate-500 hover:bg-slate-400 transition-color duration-300"
                                variant={"default"}
                                onClick={() => {
                                  setSelectedId(item.id);
                                  setDialogOpen(true);
                                }}
                              >
                                View
                              </Button>
                              <Button
                                className="px-5 my-0 py-0 h-7 transition-color duration-300"
                                variant={"outline"}
                                onClick={() => {
                                  setSelected(item);
                                  setPatchOpen(true);
                                }}
                              >
                                Edit
                              </Button>
                              <Button
                                className="px-2 my-0 py-0 h-7 transition-color duration-300"
                                variant={"destructive"}
                                onClick={() => {
                                  setSelectedId(item.id);
                                  setDeleteOpen(true);
                                }}
                              >
                                Delete
                              </Button>
                            </TableCell>
                          </TableRow>
                        </>
                      ))}
                    </TableBody>
                  </Table>
                  <Pagination className="pt-5 flex justify-end">
                    <PaginationContent>
                      {!data.first && (
                        <PaginationItem>
                          <PaginationPrevious
                            className="cursor-pointer"
                            onClick={() => updatePage(data.number - 1)}
                          />
                        </PaginationItem>
                      )}
                      {!data.last && (
                        <PaginationItem>
                          <PaginationNext
                            className="cursor-pointer"
                            onClick={() => {
                              const nextPage = data.number + 1;
                              if (nextPage < data.totalPages) {
                                updatePage(nextPage);
                              }
                            }}
                          />
                        </PaginationItem>
                      )}
                    </PaginationContent>
                  </Pagination>

                  <TimeSlotSheet
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
      )}
    </>
  );
};
