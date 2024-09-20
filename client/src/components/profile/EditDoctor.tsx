import { AppDispatch, RootState } from "@/redux/store";
import { Button } from "../ui/button";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogFooter,
  DialogTitle,
  DialogDescription,
} from "../ui/dialog";
import { Input } from "../ui/input";
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form";

import { useDispatch, useSelector } from "react-redux";
import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import { useForm } from "react-hook-form";
import { patchDoctorProfile } from "@/redux/action/profileActions";
import { EditSpecializations } from "./EditSpecializations";
import { useState } from "react";
import { Specialization } from "@/redux/reducers/specializationReducer";

const schema = z.object({
  id: z.number(),
  firstName: z.string().min(1, { message: "First name is required" }),
  lastName: z.string().min(1, { message: "Last name is required" }),
  clinicAddress: z.string().min(1, { message: "Address is required" }),
  phoneNumber: z.string().min(1, { message: "Phone number is required" }),
  experienceYears: z.string(),
  licenseNumber: z.string().min(1, { message: "License number is requried" }),
  specializations: z
    .array(
      z.object({
        id: z.number(),
        specializationName: z.string(),
      })
    )
    .optional()
    .nullable(),
});

export const EditDoctor = (props: { open: boolean; onClose: () => void }) => {
  const data = useSelector((state: RootState) => state.profile.data);
  const dispatch = useDispatch<AppDispatch>();

  const [selectedSpecializations, setSelectedSpecializations] = useState<
    Specialization[]
  >(data.specializations || []);

  const form = useForm<z.infer<typeof schema>>({
    resolver: zodResolver(schema),
    defaultValues: {
      id: data.id,
      firstName: data.firstName,
      lastName: data.lastName,
      clinicAddress: data.clinicAddress,
      phoneNumber: data.phoneNumber,
      licenseNumber: data.licenseNumber,
      experienceYears: data.experienceYears?.toString(),
      specializations: data.specializations,
    },
  });

  const onSubmit = async (values: z.infer<typeof schema>) => {
    const updatedValues = {
      ...values,
      specializations: selectedSpecializations,
      experienceYears: parseInt(values.experienceYears as string),
    };
    dispatch(patchDoctorProfile(updatedValues));
    props.onClose();
  };

  return (
    <Dialog open={props.open}>
      <DialogContent className="sm:max-w-[700px] max-h-[700px]">
        <DialogHeader>
          <DialogTitle>Edit profile</DialogTitle>
          <DialogDescription>
            Make changes to your profile here. Click save when you're done.
          </DialogDescription>
        </DialogHeader>
        <Form {...form}>
          <form onSubmit={form.handleSubmit(onSubmit)}>
            <div className="grid gap-8 grid-cols-3">
              <div className="grid gap-4 py-4 col-span-2">
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
                  name="clinicAddress"
                  render={({ field }) => (
                    <FormItem>
                      <div className="grid grid-cols-4 items-center gap-4">
                        <FormLabel>Clinic Address</FormLabel>
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
                        <FormLabel>Phone numb.</FormLabel>
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
                  name="licenseNumber"
                  render={({ field }) => (
                    <FormItem>
                      <div className="grid grid-cols-4 items-center gap-4">
                        <FormLabel>License numb.</FormLabel>
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
                  name="experienceYears"
                  render={({ field }) => (
                    <FormItem>
                      <div className="grid grid-cols-4 items-center gap-4">
                        <FormLabel>Experience yrs</FormLabel>
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
              <div className="py-4">
                <EditSpecializations
                  selectedSpecializations={selectedSpecializations}
                  setSelectedSpecializations={setSelectedSpecializations}
                />
              </div>
            </div>
            <DialogFooter>
              <Button
                type="button"
                variant="destructive"
                onClick={() => props.onClose()}
              >
                Close
              </Button>
              <Button type="submit">Save changes</Button>
            </DialogFooter>
          </form>
        </Form>
      </DialogContent>
    </Dialog>
  );
};
