import { AppDispatch, RootState } from "@/redux/store";
import { Button } from "../ui/button";
import {
  Dialog,
  DialogTrigger,
  DialogContent,
  DialogHeader,
  DialogFooter,
  DialogTitle,
  DialogDescription,
  DialogClose,
} from "../ui/dialog";
import { Input } from "../ui/input";
import {
  Form,
  FormControl,
  FormDescription,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form";

import { useDispatch, useSelector } from "react-redux";
import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import { useForm } from "react-hook-form";
import { patchPatientProfile } from "@/redux/action/profileActions";

const schema = z.object({
  id: z.number(),
  firstName: z.string().min(1, { message: "First name is required" }),
  lastName: z.string().min(1, { message: "Last name is required" }),
  address: z.string().min(1, { message: "Address is required" }),
  phoneNumber: z.string().min(1, { message: "Phone number is required" }),
  dateOfBirth: z.string().min(1, { message: "Date of birth is required" }),
});

export const EditPatient = () => {
  const data = useSelector((state: RootState) => state.profile.data);
  const dispatch = useDispatch<AppDispatch>();

  const form = useForm<z.infer<typeof schema>>({
    resolver: zodResolver(schema),
    defaultValues: {
      id: data.id,
      firstName: data.firstName,
      lastName: data.lastName,
      address: data.address,
      phoneNumber: data.phoneNumber,
      dateOfBirth: new Date(data.dateOfBirth).toISOString().substring(0, 10),
    },
  });

  const onSubmit = async (values: z.infer<typeof schema>) => {
    console.log(values);
    dispatch(patchPatientProfile(values));
    console.log(data);
  };

  return (
    <Dialog>
      <DialogTrigger asChild>
        <Button
          variant={"ghost"}
          className="[box-shadow:1px_1px_2px_var(--tw-shadow-color)] shadow-slate-400 font-semibold hover:bg-[#040D12] hover:text-white"
        >
          Edit data
        </Button>
      </DialogTrigger>
      <DialogContent className="sm:max-w-[425px]">
        <DialogHeader>
          <DialogTitle>Edit profile</DialogTitle>
          <DialogDescription>
            Make changes to your profile here. Click save when you're done.
          </DialogDescription>
        </DialogHeader>
        <Form {...form}>
          <form onSubmit={form.handleSubmit(onSubmit)}>
            <div className="grid gap-4 py-4">
              <FormField
                control={form.control}
                name="firstName"
                render={({ field }) => (
                  <FormItem>
                    <div className="grid grid-cols-4 items-center gap-4">
                      <FormLabel className="text-right">First Name</FormLabel>
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
                name="lastName"
                render={({ field }) => (
                  <FormItem>
                    <div className="grid grid-cols-4 items-center gap-4">
                      <FormLabel className="text-right">Last Name</FormLabel>
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
                name="address"
                render={({ field }) => (
                  <FormItem>
                    <div className="grid grid-cols-4 items-center gap-4">
                      <FormLabel>Address</FormLabel>
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
                name="phoneNumber"
                render={({ field }) => (
                  <FormItem>
                    <div className="grid grid-cols-4 items-center gap-4">
                      <FormLabel>Phone Number</FormLabel>
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
                name="dateOfBirth"
                render={({ field }) => (
                  <FormItem>
                    <div className="grid grid-cols-4 items-center gap-4">
                      <FormLabel className="text-right">
                        Date Of Birth
                      </FormLabel>
                      <FormControl>
                        <Input {...field} className="col-span-3" />
                      </FormControl>
                    </div>
                    <div className="flex flex-col justify-center space-y-2">
                      <FormMessage />
                      <FormDescription>Year-Month-Day Format</FormDescription>
                    </div>
                  </FormItem>
                )}
              />
            </div>
            <DialogFooter>
              <DialogClose asChild>
                <Button type="button" variant="destructive">
                  Close
                </Button>
              </DialogClose>
              <DialogClose asChild>
                <Button type="submit">Save changes</Button>
              </DialogClose>
            </DialogFooter>
          </form>
        </Form>
      </DialogContent>
    </Dialog>
  );
};
