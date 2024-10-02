import { Input } from "@/components/ui/input";
import {
  Dialog,
  DialogContent,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "../../ui/dialog";
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
} from "../../ui/form";
import { Button } from "@/components/ui/button";
import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import { useForm } from "react-hook-form";
import DatePicker from "react-datepicker";
import { useDispatch } from "react-redux";
import { AppDispatch } from "@/redux/store";
import { patchAppointment } from "@/redux/action/appointmentActions";
import { AppointmentState } from "@/redux/reducers/appointmentsReducer";
import { useEffect } from "react";

const schema = z.object({
  appointmentDate: z.date().optional(),
  status: z.enum(["SCHEDULED", "COMPLETED", "CANCELED"]),
  reason: z
    .string()
    .min(1, { message: "Reason is required" })
    .max(1000, { message: "Reason is too long" }),
});

export const PatchAppointmentDialog = (props: {
  patchOpen: boolean;
  onClose: () => void;
  selected: AppointmentState | undefined;
}) => {
  const dispatch = useDispatch<AppDispatch>();

  const form = useForm<z.infer<typeof schema>>({
    resolver: zodResolver(schema),
    defaultValues: {
      appointmentDate: props.selected?.appointmentDate as Date,
      status: props.selected?.status,
      reason: props.selected?.reason,
    },
  });

  const patch = async (values: z.infer<typeof schema>) => {
    console.log(values);
    if (
      (props.selected?.appointmentDate as Date).toISOString() ===
      values.appointmentDate?.toISOString()
    ) {
      values.appointmentDate = undefined;
    }
    dispatch(patchAppointment({ id: props.selected?.id as number, ...values }));
    props.onClose();
    setInterval(() => {
      window.location.reload();
    }, 2000);
    scrollTo({ top: 0, behavior: "smooth" });
  };

  useEffect(() => {
    form.reset({
      appointmentDate: props.selected?.appointmentDate as Date,
      status: props.selected?.status,
      reason: props.selected?.reason,
    });
  }, [props.selected, form]);

  return (
    <Dialog open={props.patchOpen} onOpenChange={props.onClose}>
      <DialogContent className="w-[500px]">
        <DialogHeader>
          <DialogTitle>Edit Appointment</DialogTitle>
        </DialogHeader>
        <Form {...form}>
          <form onSubmit={form.handleSubmit(patch)}>
            <div className="grid gap-8 grid-cols-3">
              <div className="grid gap-4 py-4 col-span-2">
                <FormField
                  control={form.control}
                  name="appointmentDate"
                  render={({ field }) => (
                    <FormItem>
                      <div className="grid grid-cols-4 items-center gap-4">
                        <FormLabel className="text-right">Date</FormLabel>
                        <FormControl>
                          <DatePicker
                            selected={field.value}
                            onChange={field.onChange}
                            showTimeSelect
                            dateFormat="Pp"
                            className="col-span-3 rounded-lg border border-gray-300 bg-gray-50 p-2 text-sm text-gray-900"
                          />
                        </FormControl>
                      </div>
                    </FormItem>
                  )}
                />
                <FormField
                  control={form.control}
                  name="status"
                  render={({ field }) => (
                    <FormItem>
                      <div className="grid grid-cols-4 items-center gap-4">
                        <FormLabel className="text-right">Status</FormLabel>
                        <FormControl>
                          <select
                            className="rounded-lg border border-gray-300 bg-gray-50 p-2 w-[220px] text-sm text-gray-900"
                            {...field}
                          >
                            <option value="SCHEDULED">Scheduled</option>
                            <option value="COMPLETED">Completed</option>
                            <option value="CANCELED">Canceled</option>
                          </select>
                        </FormControl>
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
                        <FormLabel className="text-right">Reason</FormLabel>
                        <FormControl>
                          <Input {...field} className="col-span-3" />
                        </FormControl>
                      </div>
                    </FormItem>
                  )}
                />
              </div>
            </div>
          </form>
        </Form>
        <DialogFooter>
          <Button variant={"outline"} onClick={props.onClose}>
            Close
          </Button>
          <Button
            variant={"destructive"}
            type="button"
            onClick={() => {
              patch(form.getValues());
            }}
          >
            Edit
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
};
