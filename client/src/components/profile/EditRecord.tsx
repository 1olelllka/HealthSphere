import { Button } from "../ui/button";
import {
  Dialog,
  DialogClose,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "../ui/dialog";
import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import { useForm } from "react-hook-form";
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "../ui/form";
import { Input } from "../ui/input";
import { useDispatch, useSelector } from "react-redux";
import { AppDispatch, RootState } from "@/redux/store";
import { updateMedicalRecord } from "@/redux/action/recordActions";
import { useEffect } from "react";

const schema = z.object({
  id: z.number(),
  diagnosis: z.string().min(1, { message: "Diagnosis must not be empty." }),
  treatment: z.string().optional(),
  recordDate: z.string().optional(),
});

export const EditRecord = () => {
  const dispatch = useDispatch<AppDispatch>();
  const data = useSelector((state: RootState) => state.record.content[0]);

  const form = useForm<z.infer<typeof schema>>({
    resolver: zodResolver(schema),
    defaultValues: {
      id: data.id,
      diagnosis: data.diagnosis,
      treatment: (data.treatment as string) || "",
      recordDate: data.recordDate as string,
    },
  });

  useEffect(() => {
    form.reset({
      id: data.id,
      diagnosis: data.diagnosis,
      treatment: (data.treatment as string) || "",
      recordDate: data.recordDate as string,
    });
  }, [data.diagnosis, data.treatment, data.recordDate, data.id, form]);

  const onSubmit = async (values: z.infer<typeof schema>) => {
    dispatch(updateMedicalRecord(values));
  };

  return (
    <div className="mt-2.5">
      <Dialog>
        <DialogTrigger asChild>
          <Button variant={"outline"}>Edit Record</Button>
        </DialogTrigger>
        <DialogContent className="w-[450px]">
          <DialogHeader>
            <DialogTitle>Edit Record</DialogTitle>
            <DialogDescription>
              Make changes to medicine here.
            </DialogDescription>
            <Form {...form}>
              <form onSubmit={form.handleSubmit(onSubmit)}>
                <div className="grid gap-8 grid-cols-2">
                  <div className="grid gap-4 py-4 col-span-2">
                    <FormField
                      control={form.control}
                      name="diagnosis"
                      render={({ field }) => (
                        <FormItem>
                          <div className="grid grid-cols-4 items-center gap-4">
                            <FormLabel className="text-right">
                              Diagnosis
                            </FormLabel>
                            <FormControl>
                              <Input {...field} className="col-span-3" />
                            </FormControl>
                          </div>
                          <div className="flex justify-center mr-8">
                            <FormMessage />
                          </div>
                        </FormItem>
                      )}
                    />
                    <FormField
                      control={form.control}
                      name="treatment"
                      render={({ field }) => (
                        <FormItem>
                          <div className="grid grid-cols-4 items-center gap-4">
                            <FormLabel className="text-right">
                              Treatment
                            </FormLabel>
                            <FormControl>
                              <Input {...field} className="col-span-3" />
                            </FormControl>
                          </div>
                          <div className="flex justify-center mr-8">
                            <FormMessage />
                          </div>
                        </FormItem>
                      )}
                    />
                  </div>
                </div>
                <DialogFooter>
                  <DialogClose asChild>
                    <Button variant={"destructive"}>Close</Button>
                  </DialogClose>
                  <DialogClose asChild>
                    <Button
                      variant={"default"}
                      type="submit"
                      onClick={() => {
                        if (!form.formState.errors) {
                          form.reset();
                        }
                      }}
                    >
                      Edit
                    </Button>
                  </DialogClose>
                </DialogFooter>
              </form>
            </Form>
          </DialogHeader>
        </DialogContent>
      </Dialog>
    </div>
  );
};
