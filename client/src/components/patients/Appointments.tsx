import { Calendar, momentLocalizer } from "react-big-calendar";
import moment from "moment";
import "react-big-calendar/lib/css/react-big-calendar.css";
import { useDispatch, useSelector } from "react-redux";
import { AppDispatch, RootState } from "@/redux/store";
import { useEffect, useState } from "react";
import {
  createAppointment,
  setAppointmentsForPatient,
} from "@/redux/action/appointmentActions";
import "react-datepicker/dist/react-datepicker.css";
import {
  Dialog,
  DialogContent,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "../ui/dialog";
import { Button } from "../ui/button";
import { z } from "zod";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { Form, FormControl, FormField, FormItem, FormLabel } from "../ui/form";
import { Input } from "../ui/input";
import { Label } from "../ui/label";
import { Alert, AlertDescription, AlertTitle } from "@/components/ui/alert";
import { useParams } from "react-router-dom";
import { UnauthorizedPage } from "@/pages/UnauthorizedPage";

const localizer = momentLocalizer(moment);

const schema = z.object({
  patientId: z.number(),
  appointmentDate: z.date().optional(),
  status: z.string().default("SCHEDULED"),
  reason: z.string().optional(),
});

export const Appointments = () => {
  const id = parseInt(useParams().id as string, 10);
  const dispatch = useDispatch<AppDispatch>();
  const data = useSelector((state: RootState) => state.appointment);
  const [openDialog, setOpenDialog] = useState<boolean>(false);
  const [selectedDate, setSelectedDate] = useState<Date | undefined>(undefined);
  const [dateRange, setDateRange] = useState<
    [Date | undefined, Date | undefined]
  >([new Date(), new Date(Date.now() + 1000 * 60 * 60 * 24 * 5)]);
  const form = useForm<z.infer<typeof schema>>({
    resolver: zodResolver(schema),
    defaultValues: {
      appointmentDate: selectedDate ?? new Date(),
      patientId: id,
      status: "SCHEDULED",
      reason: "",
    },
  });

  const onSubmit = async (values: z.infer<typeof schema>) => {
    values.appointmentDate = selectedDate;
    dispatch(
      createAppointment({
        ...values,
        appointmentDate: values.appointmentDate?.toISOString(),
      })
    );
    setInterval(() => {
      window.location.reload();
    }, 2000);
  };

  const appointments = data.content.map((item) => ({
    ...item,
    endDate: new Date(item.endDate),
    appointmentDate: new Date(item.appointmentDate),
  }));

  useEffect(() => {
    const getAppointmentsForPatient = async (patientId: number) => {
      dispatch(
        setAppointmentsForPatient({
          patientId: patientId,
          from: dateRange[0],
          to: dateRange[1],
        })
      );
    };
    getAppointmentsForPatient(id);
  }, [dispatch, id, dateRange]);

  return (
    <>
      {data.error && data.error.status === 401 ? (
        <UnauthorizedPage message={data.error.message} />
      ) : (
        <div className="mt-10 bg-slate-50 p-10 rounded-3xl drop-shadow-lg">
          <h1 className="text-4xl">Appointments</h1>
          {data.error && data.error.status === 400 && (
            <Alert className="w-1/3 mx-auto mt-10" variant={"destructive"}>
              <AlertTitle>Error</AlertTitle>
              <AlertDescription>{data.error.message}</AlertDescription>
            </Alert>
          )}
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
                slotPropGetter={(
                  date: Date
                ): { style: React.CSSProperties } => {
                  const today = moment().startOf("day");
                  if (moment(date).isBefore(today)) {
                    return {
                      style: {
                        backgroundColor: "#f0f0f0",
                        opacity: 0.5,
                        transition: "background-color 0.3s, opacity 0.3s",
                      },
                    };
                  }
                  return { style: {} };
                }}
                onRangeChange={(range: Date[] | { start: Date; end: Date }) => {
                  if (Array.isArray(range)) {
                    setDateRange([
                      new Date(
                        Math.max(range[0].getTime(), new Date().getTime())
                      ),
                      new Date(
                        range[4].getTime() +
                          23 * 60 * 60 * 1000 +
                          59 * 60 * 1000
                      ),
                    ]);
                  }
                }}
                eventPropGetter={() => {
                  const style: React.CSSProperties = {
                    backgroundColor: "#eb5763",
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
                onSelectSlot={(slotInfo) => {
                  if (moment(slotInfo.start).isBefore(moment())) {
                    alert("You cannot book an appointment in the past");
                    return;
                  }
                  setOpenDialog(true);
                  setSelectedDate(slotInfo.start);
                }}
              />
            </div>
          )}
          <Dialog open={openDialog}>
            <DialogContent className="w-[455px]">
              <DialogHeader>
                <DialogTitle>Create an appointment</DialogTitle>
              </DialogHeader>
              <Form {...form}>
                <form>
                  <div className="grid gap-8 grid-cols-3">
                    <div className="grid gap-4 py-4 col-span-2">
                      <FormField
                        control={form.control}
                        name="appointmentDate"
                        render={() => (
                          <FormItem>
                            <div className="grid grid-cols-4 items-center gap-4">
                              <FormLabel className="text-right">Date</FormLabel>
                              <Label className="col-span-3 border-2 border-slate-300 rounded-lg p-2 font-normal w-[335px]">
                                {selectedDate
                                  ? `${selectedDate.getFullYear()}/${String(
                                      selectedDate.getMonth() + 1
                                    ).padStart(2, "0")}/${String(
                                      selectedDate.getDate()
                                    ).padStart(2, "0")} ${String(
                                      selectedDate.getHours()
                                    ).padStart(2, "0")}:${String(
                                      selectedDate.getMinutes()
                                    ).padStart(2, "0")}`
                                  : ""}
                              </Label>
                            </div>
                          </FormItem>
                        )}
                      />
                      <FormField
                        control={form.control}
                        name="reason"
                        render={({ field }) => (
                          <FormItem>
                            <div className="grid grid-cols-4 items-center gap-4">
                              <FormLabel className="text-right">
                                Reason
                              </FormLabel>
                              <FormControl>
                                <Input
                                  {...field}
                                  placeholder="Briefly describe the reason (Optional)"
                                  className="col-span-3 w-[335px]"
                                />
                              </FormControl>
                            </div>
                          </FormItem>
                        )}
                      />
                    </div>
                  </div>
                  <DialogFooter>
                    <Button
                      variant={"outline"}
                      type="button"
                      onClick={() => {
                        setOpenDialog(false);
                      }}
                    >
                      Cancel
                    </Button>
                    <Button
                      variant={"default"}
                      type="button"
                      onClick={() => {
                        onSubmit(form.getValues());
                        setOpenDialog(false);
                      }}
                    >
                      Create
                    </Button>
                  </DialogFooter>
                </form>
              </Form>
            </DialogContent>
          </Dialog>
        </div>
      )}
    </>
  );
};
