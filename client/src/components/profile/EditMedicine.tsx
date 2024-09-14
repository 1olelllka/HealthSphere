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
import { useDispatch } from "react-redux";
import { AppDispatch } from "@/redux/store";
import {
  deleteMedicineFromPrescription,
  updateMedicineForPrescription,
} from "@/redux/action/medicineActions";
import { Medicine } from "@/redux/reducers/medicineReducer";

const schema = z.object({
  id: z.number(),
  medicineName: z
    .string()
    .min(1, { message: "Medicine name must not be empty." }),
  dosage: z.string().min(1, { message: "Dosage must not be empty." }),
  instructions: z.string().optional(),
});

export const EditMedicine = (props: { medicine: Medicine }) => {
  const dispatch = useDispatch<AppDispatch>();

  const form = useForm<z.infer<typeof schema>>({
    resolver: zodResolver(schema),
    defaultValues: {
      id: props.medicine.id,
      medicineName: props.medicine.medicineName,
      dosage: props.medicine.dosage,
      instructions: props.medicine.instructions as string,
    },
  });

  const onSubmit = async (values: z.infer<typeof schema>) => {
    dispatch(updateMedicineForPrescription(values as Medicine));
  };

  const removeMedicine = async (id: number) => {
    dispatch(deleteMedicineFromPrescription(id));
    form.reset();
  };

  return (
    <div className="mt-2.5 space-x-2">
      <Dialog>
        <DialogTrigger asChild>
          <Button variant={"default"}>Edit</Button>
        </DialogTrigger>
        <DialogContent className="w-[450px]">
          <DialogHeader>
            <DialogTitle>Edit Medicine</DialogTitle>
            <DialogDescription>
              Make changes to medicine here.
            </DialogDescription>
            <Form {...form}>
              <form onSubmit={form.handleSubmit(onSubmit)}>
                <div className="grid gap-8 grid-cols-2">
                  <div className="grid gap-4 py-4 col-span-2">
                    <FormField
                      control={form.control}
                      name="medicineName"
                      render={({ field }) => (
                        <FormItem>
                          <div className="grid grid-cols-4 items-center gap-4">
                            <FormLabel className="text-right">
                              Medicine Name
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
                      name="dosage"
                      render={({ field }) => (
                        <FormItem>
                          <div className="grid grid-cols-4 items-center gap-4">
                            <FormLabel className="text-right">Dosage</FormLabel>
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
                      name="instructions"
                      render={({ field }) => (
                        <FormItem>
                          <div className="grid grid-cols-4 items-center gap-4">
                            <FormLabel className="text-right">
                              Instructions
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
                    <Button variant={"default"} type="submit">
                      Edit
                    </Button>
                  </DialogClose>
                </DialogFooter>
              </form>
            </Form>
          </DialogHeader>
        </DialogContent>
      </Dialog>
      <Button
        variant={"destructive"}
        onClick={() => removeMedicine(props.medicine.id)}
      >
        Delete
      </Button>
    </div>
  );
};
