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
import { addMedicineToPrescription } from "@/redux/action/medicineActions";
import { Medicine } from "@/redux/reducers/medicineReducer";

const schema = z.object({
  id: z.number().optional(),
  medicineName: z
    .string()
    .min(1, { message: "Medicine name must not be empty." }),
  dosage: z.string().min(1, { message: "Dosage must not be empty." }),
  instructions: z.string().optional(),
});

export const CreateMedicine = (props: { id: number }) => {
  const dispatch = useDispatch<AppDispatch>();

  const form = useForm<z.infer<typeof schema>>({
    resolver: zodResolver(schema),
    defaultValues: {
      medicineName: "",
      dosage: "",
      instructions: "",
    },
  });

  const onSubmit = async (values: z.infer<typeof schema>) => {
    values.id = props.id;
    console.log(values);
    dispatch(addMedicineToPrescription(values as Medicine));
  };

  return (
    <>
      <Dialog>
        <DialogTrigger asChild>
          <Button className="mt-2.5" variant={"default"}>
            Add Medicine
          </Button>
        </DialogTrigger>
        <DialogContent className="w-[450px]">
          <DialogHeader>
            <DialogTitle>Add Medicine</DialogTitle>
            <DialogDescription>
              Add a new medicine to your prescription.
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
                          <div className="flex justify-center ml-8">
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
                          <div className="flex justify-center mr-4">
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
                      Add
                    </Button>
                  </DialogClose>
                </DialogFooter>
              </form>
            </Form>
          </DialogHeader>
        </DialogContent>
      </Dialog>
    </>
  );
};
