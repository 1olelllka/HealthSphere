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
import { Popover } from "../ui/popover";
import { PopoverContent, PopoverTrigger } from "@radix-ui/react-popover";
import { EyeIcon, UserIcon } from "lucide-react";
import { cn } from "@/lib/utils";
import { Command, CommandGroup, CommandList } from "../ui/command";
import { CommandItem } from "cmdk";
import { ScrollToTop } from "../general/ScrollToTop";
import DatePicker from "react-datepicker";

const schema = z.object({
  email: z.string().email({ message: "Email is invalid" }),
  password: z
    .string()
    .min(8, { message: "Password should be at least 8 characters" }),
  firstName: z.string().min(1, { message: "First Name is required" }),
  lastName: z.string().min(1, { message: "Last Name is required" }),
  gender: z.enum(["Male", "Female"]),
  dateOfBirth: z.date({ required_error: "Date of Birth is required" }),
});

const genders = [
  {
    id: 1,
    name: "Male",
  },
  {
    id: 2,
    name: "Female",
  },
];

export const Register = () => {
  const form = useForm<z.infer<typeof schema>>({
    resolver: zodResolver(schema),
    defaultValues: {
      email: "",
      password: "",
      firstName: "",
      lastName: "",
      gender: undefined,
      dateOfBirth: undefined,
    },
  });

  const [error, setError] = useState<string>("");
  const [visible, setVisible] = useState<boolean>(false);

  const navigate = useNavigate();

  const onSubmit = async (values: z.infer<typeof schema>) => {
    values.gender = values.gender?.toUpperCase() as "Male" | "Female";
    console.log(values);
    try {
      await SERVER_API.post("/register/patient", values, {
        headers: {
          "Content-Type": "application/json",
        },
      });
      const credentials = {
        email: values.email,
        password: values.password,
      };
      await SERVER_API.post("/login", credentials, {
        headers: {
          "Content-Type": "application/json",
        },
        withCredentials: true,
      });
      navigate("/profile");
    } catch (err) {
      const error = err as { response: { data: { message: string } } };
      setError(error?.response?.data?.message);
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
            <AlertDescription>{error}</AlertDescription>
          </Alert>
          <h1
            className={`text-5xl font-semibold text-center transition-all ${
              error && "pt-10"
            }`}
          >
            Register
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
                        <div className="flex flex-row gap-2">
                          <Input
                            type={visible ? "" : "password"}
                            placeholder="Enter your password"
                            {...field}
                          />
                          <Button
                            type={"button"}
                            variant={"outline"}
                            onClick={() => setVisible(!visible)}
                          >
                            <EyeIcon />
                          </Button>
                        </div>
                      </FormControl>
                      <FormMessage />
                    </FormItem>
                  )}
                />
                <FormField
                  control={form.control}
                  name="firstName"
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel>First Name</FormLabel>
                      <FormControl>
                        <Input placeholder="Enter your first name" {...field} />
                      </FormControl>
                      <FormMessage />
                    </FormItem>
                  )}
                />
                <FormField
                  control={form.control}
                  name="lastName"
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel>Last Name</FormLabel>
                      <FormControl>
                        <Input placeholder="Enter your last name" {...field} />
                      </FormControl>
                      <FormMessage />
                    </FormItem>
                  )}
                />
                <FormField
                  control={form.control}
                  name="gender"
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel>Gender</FormLabel>
                      <FormControl>
                        <Popover>
                          <PopoverTrigger asChild>
                            <Button
                              variant="outline"
                              className={cn(
                                "w-full pl-3 text-left font-normal",
                                !field.value && "text-muted-foreground"
                              )}
                            >
                              {field.value ? (
                                field.value
                              ) : (
                                <h1>Select your gender</h1>
                              )}
                              <UserIcon className="ml-2 h-4 w-4" />
                            </Button>
                          </PopoverTrigger>
                          <PopoverContent className="w-[200px] p-0 z-50">
                            <Command>
                              <CommandList>
                                <CommandGroup>
                                  {genders.map((item) => (
                                    <CommandItem
                                      className={`flex cursor-pointer justify-start p-2 ${
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
                      <FormMessage />
                    </FormItem>
                  )}
                />
                <FormField
                  control={form.control}
                  name="dateOfBirth"
                  render={({ field }) => (
                    <FormItem className="flex flex-col mt-2">
                      <FormLabel>Date Of Birth</FormLabel>
                      <FormControl>
                        <DatePicker
                          selected={field.value}
                          onChange={field.onChange}
                          dateFormat={"dd/MM/yyyy"}
                          className="bg-white dark:bg-slate-800 border border-slate-200 rounded-md p-1.5 w-full"
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
