import { zodResolver } from "@hookform/resolvers/zod";
import { useForm } from "react-hook-form";
import { z } from "zod";
import {
  Form,
  FormControl,
  FormDescription,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form";
import { Input } from "../ui/input";
import { Button } from "../ui/button";
import "../../index.css";
import axiosInstance from "../api/axiosInstance";

const formSchema = z.object({
  email: z.string().email({ message: "Please enter a valid email address" }),
  password: z.string().min(8, {
    message:
      "Password must be at least 8 characters and include at least 1 number",
  }),
  firstName: z.string().min(1, { message: "Please enter your first name" }),
  lastName: z.string().min(1, { message: "Please enter your last name" }),
  licenseNumber: z
    .string()
    .min(1, { message: "Please enter your license number" }),
  phoneNumber: z.string().min(8, { message: "Please enter your phone number" }),
  clinicAddress: z
    .string()
    .min(1, { message: "Please enter your clinic address" }),
});

export default function DoctorRegister() {
  const form = useForm<z.infer<typeof formSchema>>({
    resolver: zodResolver(formSchema),
    defaultValues: {
      email: "",
      password: "",
      firstName: "",
      lastName: "",
      licenseNumber: "",
      phoneNumber: "",
      clinicAddress: "",
    },
  });

  const onSubmit = (values: z.infer<typeof formSchema>) => {
    console.log(values);
    axiosInstance
      .post("http://localhost:8000/api/v1/doctor-register", values)
      .then(() => {
        alert("success"); // for now
      })
      .catch((err) => {
        alert("You are not authorized to do this." + err);
      });
  };

  return (
    <>
      <div className="PatientRegisterLogin pb-7">
        <h1 className="text-4xl text-center pt-10 font-bold">
          Doctor Register
        </h1>
        <div className="flex justify-center mt-8 mx-3">
          <Form {...form}>
            <form onSubmit={form.handleSubmit(onSubmit)}>
              <FormField
                control={form.control}
                name="email"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel className="text-md">Email</FormLabel>
                    <FormControl>
                      <Input type="email" {...field} />
                    </FormControl>
                    <FormDescription className="text-xs text-slate-600">
                      Write your email.
                    </FormDescription>
                    <FormMessage />
                  </FormItem>
                )}
              />
              <FormField
                control={form.control}
                name="password"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel className="text-md">Password</FormLabel>
                    <FormControl>
                      <Input type="password" {...field} />
                    </FormControl>
                    <FormDescription className="text-xs text-slate-600">
                      Write password. The password should be at least 8
                      characters including at least 1 number.
                    </FormDescription>
                    <FormMessage />
                  </FormItem>
                )}
              />
              <FormField
                control={form.control}
                name="firstName"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel className="text-md">First Name</FormLabel>
                    <FormControl>
                      <Input type="string" {...field} />
                    </FormControl>
                    <FormDescription className="text-xs text-slate-600">
                      Write the first name. The first name should be at least 1
                      character.
                    </FormDescription>
                    <FormMessage />
                  </FormItem>
                )}
              />
              <FormField
                control={form.control}
                name="lastName"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel className="text-md">Last Name</FormLabel>
                    <FormControl>
                      <Input type="string" {...field} />
                    </FormControl>
                    <FormDescription className="text-xs text-slate-600">
                      Write the last name. The last name should be at least 1
                      character.
                    </FormDescription>
                    <FormMessage />
                  </FormItem>
                )}
              />
              <FormField
                control={form.control}
                name="licenseNumber"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel className="text-md">License Number</FormLabel>
                    <FormControl>
                      <Input type="string" {...field} />
                    </FormControl>
                    <FormDescription className="text-xs text-slate-600">
                      Write the license number.
                    </FormDescription>
                    <FormMessage />
                  </FormItem>
                )}
              />
              <FormField
                control={form.control}
                name="phoneNumber"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel className="text-md">Phone Number</FormLabel>
                    <FormControl>
                      <Input type="string" {...field} />
                    </FormControl>
                    <FormDescription className="text-xs text-slate-600">
                      Write the phone number (without country code). The number
                      should contain at least 8 numbers.
                    </FormDescription>
                    <FormMessage />
                  </FormItem>
                )}
              />
              <FormField
                control={form.control}
                name="clinicAddress"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel className="text-md">Clinic Address</FormLabel>
                    <FormControl>
                      <Input type="string" {...field} />
                    </FormControl>
                    <FormDescription className="text-xs text-slate-600">
                      Write the clinic address. The site admin will contact
                      city's registered clinic.
                    </FormDescription>
                    <FormMessage />
                  </FormItem>
                )}
              />
              <Button type="submit" className="mt-5">
                Submit
              </Button>
            </form>
          </Form>
        </div>
      </div>
    </>
  );
}
