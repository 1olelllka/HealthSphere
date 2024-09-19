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
import { Popover, PopoverContent, PopoverTrigger } from "../ui/popover";
import { MdOutlineBloodtype } from "react-icons/md";
import { Command, CommandGroup, CommandItem, CommandList } from "../ui/command";
import { cn } from "@/lib/utils";

const schema = z.object({
  id: z.number(),
  firstName: z.string().min(1, { message: "First name is required" }),
  lastName: z.string().min(1, { message: "Last name is required" }),
  address: z.string().min(1, { message: "Address is required" }),
  phoneNumber: z.string().min(1, { message: "Phone number is required" }),
  dateOfBirth: z.string().min(1, { message: "Date of birth is required" }),
  allergies: z.string().optional(),
  bloodType: z.string().optional(),
});

export const EditPatient = (props: { open: boolean; onClose: () => void }) => {
  const data = useSelector((state: RootState) => state.profile.data);
  const dispatch = useDispatch<AppDispatch>();

  const bloodTypes = [
    {
      id: 1,
      name: "O+",
    },
    {
      id: 2,
      name: "O-",
    },
    {
      id: 3,
      name: "A+",
    },
    {
      id: 4,
      name: "A-",
    },
    {
      id: 5,
      name: "B+",
    },
    {
      id: 6,
      name: "B-",
    },
    {
      id: 7,
      name: "AB+",
    },
    {
      id: 8,
      name: "AB-",
    },
  ];

  const form = useForm<z.infer<typeof schema>>({
    resolver: zodResolver(schema),
    defaultValues: {
      id: data.id,
      firstName: data.firstName,
      lastName: data.lastName,
      address: data.address,
      phoneNumber: data.phoneNumber,
      dateOfBirth: new Date(data.dateOfBirth).toISOString().substring(0, 10),
      allergies: data.allergies || "",
      bloodType: data.bloodType || "",
    },
  });

  const onSubmit = async (values: z.infer<typeof schema>) => {
    switch (values.bloodType) {
      case "O+":
        values.bloodType = "OPlus";
        break;
      case "O-":
        values.bloodType = "OMinus";
        break;
      case "A+":
        values.bloodType = "APlus";
        break;
      case "A-":
        values.bloodType = "AMinus";
        break;
      case "B+":
        values.bloodType = "BPlus";
        break;
      case "B-":
        values.bloodType = "BMinus";
        break;
      case "AB+":
        values.bloodType = "ABPlus";
        break;
      case "AB-":
        values.bloodType = "ABMinus";
        break;
      default: {
        values.bloodType = "";
      }
    }
    dispatch(patchPatientProfile(values));
    props.onClose();
  };

  return (
    <Dialog open={props.open}>
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
                name="allergies"
                render={({ field }) => (
                  <FormItem>
                    <div className="grid grid-cols-4 items-center gap-4">
                      <FormLabel>Allergies</FormLabel>
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
                name="bloodType"
                render={({ field }) => (
                  <FormItem>
                    <div className="grid grid-cols-4 items-center gap-4">
                      <FormLabel>Blood Type</FormLabel>
                      <FormControl>
                        <Popover>
                          <PopoverTrigger asChild>
                            <Button
                              variant="outline"
                              className={cn(
                                "w-full p-3 text-left font-normal",
                                !field.value && "text-muted-foreground"
                              )}
                            >
                              {field.value ? (
                                field.value
                              ) : (
                                <MdOutlineBloodtype className="h-4 w-4" />
                              )}
                            </Button>
                          </PopoverTrigger>
                          <PopoverContent className="w-[200px] p-0">
                            <Command>
                              <CommandList>
                                <CommandGroup>
                                  {bloodTypes.map((item) => (
                                    <CommandItem
                                      className={`flex justify-start p-2 ${
                                        field.value == item.name
                                          ? "bg-slate-200"
                                          : ""
                                      }`}
                                      key={item.id}
                                      value={item.name}
                                      onSelect={(currentValue) => {
                                        field.onChange(currentValue);
                                      }}
                                    >
                                      {item.name}
                                    </CommandItem>
                                  ))}
                                </CommandGroup>
                              </CommandList>
                            </Command>
                          </PopoverContent>
                        </Popover>
                      </FormControl>
                    </div>
                    <FormMessage />
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
              <Button
                onClick={props.onClose}
                type="button"
                variant="destructive"
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
