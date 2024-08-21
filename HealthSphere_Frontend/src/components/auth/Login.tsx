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
  password: z.string().min(1, { message: "Please enter your password" }),
});

export default function Login() {
  const form = useForm<z.infer<typeof formSchema>>({
    resolver: zodResolver(formSchema),
    defaultValues: {
      email: "",
      password: "",
    },
  });

  const onSubmit = (values: z.infer<typeof formSchema>) => {
    console.log(values);
    axiosInstance
      .post("http://localhost:8000/api/v1/login", values, {
        withCredentials: true,
      })
      .then(() => {
        alert("success"); // for now
      })
      .catch((err) => {
        console.log(err);
      });
  };

  return (
    <>
      <div className="PatientRegisterLogin pb-7 h-screen">
        <h1 className="text-4xl text-center pt-10 font-bold">
          Login to your account
        </h1>
        <div className="flex justify-center mt-12 mx-3">
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
                      Write your password. You may have received it either by
                      registration (patient) or by admin (staff).
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
