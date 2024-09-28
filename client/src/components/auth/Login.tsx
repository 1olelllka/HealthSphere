import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import { useForm } from "react-hook-form";
import { useState } from "react";
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "../ui/form";
import { Input } from "@/components/ui/input";
import { Button } from "../ui/button";
import { SERVER_API } from "@/redux/api/utils";
import { useNavigate } from "react-router-dom";
import { Alert, AlertDescription, AlertTitle } from "@/components/ui/alert";
import { ScrollToTop } from "../general/ScrollToTop";

const schema = z.object({
  email: z.string().email({ message: "Email is invalid" }),
  password: z.string().min(1, { message: "Password is required" }),
});

export const Login = () => {
  const form = useForm<z.infer<typeof schema>>({
    resolver: zodResolver(schema),
    defaultValues: {
      email: "",
      password: "",
    },
  });

  const [error, setError] = useState<boolean>(false);

  const navigate = useNavigate();

  const onSubmit = async (values: z.infer<typeof schema>) => {
    try {
      await SERVER_API.post("/login", values, {
        headers: {
          "Content-Type": "application/json",
        },
        withCredentials: true,
      });
      navigate("/profile");
    } catch (err) {
      setError(true);
      console.log(err);
    }
  };

  return (
    <>
      <ScrollToTop />
      <div className="w-full flex justify-center">
        <div className="container">
          <Alert
            className={`w-1/3 mx-auto transition-all transform duration-300 ${
              error ? "translate-y-0 opacity-100" : "translate-y-full opacity-0"
            }`}
            variant={"destructive"}
          >
            <AlertTitle>Error</AlertTitle>
            <AlertDescription>
              Invalid email or password. Please try again.
            </AlertDescription>
          </Alert>
          <h1
            className={`text-5xl font-semibold text-center transition-all ${
              error && "pt-10"
            }`}
          >
            Login
          </h1>
          <div className="flex flex-col w-1/3 mx-auto">
            <Form {...form}>
              <form onSubmit={form.handleSubmit(onSubmit)}>
                <FormField
                  control={form.control}
                  name="email"
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel>Email</FormLabel>
                      <FormControl>
                        <Input placeholder="Enter your email" {...field} />
                      </FormControl>
                      <FormMessage />
                    </FormItem>
                  )}
                />
                <FormField
                  control={form.control}
                  name="password"
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel>Password</FormLabel>
                      <FormControl>
                        <Input
                          type="password"
                          placeholder="Enter your password"
                          {...field}
                        />
                      </FormControl>
                      <FormMessage />
                    </FormItem>
                  )}
                />
                <div className="pt-4">
                  <Button
                    type="submit"
                    variant={"ghost"}
                    className="flex justify-start hover:bg-[#040D12] hover:text-white drop-shadow-lg [box-shadow:1px_1px_2px_var(--tw-shadow-color)] shadow-slate-400 font-semibold"
                  >
                    Submit
                  </Button>
                </div>
              </form>
            </Form>
          </div>
        </div>
      </div>
    </>
  );
};
