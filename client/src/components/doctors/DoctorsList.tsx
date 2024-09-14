import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import { useForm } from "react-hook-form";
import { Form, FormControl, FormField } from "../ui/form";
import { Input } from "../ui/input";
import { Button } from "../ui/button";
import { useDispatch, useSelector } from "react-redux";
import { AppDispatch, RootState } from "@/redux/store";
import { useEffect } from "react";
import { getAllDoctors } from "@/redux/action/doctorActions";
import { Card, CardDescription, CardHeader, CardTitle } from "../ui/card";
import {
  Pagination,
  PaginationContent,
  PaginationItem,
  PaginationLink,
  PaginationNext,
  PaginationPrevious,
} from "@/components/ui/pagination";
import { useNavigate, useSearchParams } from "react-router-dom";
import { Alert, AlertDescription, AlertTitle } from "../ui/alert";

const schema = z.object({
  params: z.string().optional(),
});
export const DoctorsList = () => {
  const doctors = useSelector((state: RootState) => state.doctor);
  const dispatch = useDispatch<AppDispatch>();
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const page = parseInt(searchParams.get("page") || "0", 10);

  useEffect(() => {
    const getDoctors = async () => {
      dispatch(
        getAllDoctors({ params: searchParams.get("search") ?? "", page: page })
      );
    };
    getDoctors();
  }, [dispatch, page, searchParams]);

  const form = useForm<z.infer<typeof schema>>({
    resolver: zodResolver(schema),
    defaultValues: { params: "" },
  });

  console.log(doctors);

  const onSubmit = async (values: z.infer<typeof schema>) => {
    dispatch(getAllDoctors({ params: values.params ?? "", page: page }));
    navigate(`?search=${values.params}`, { replace: true });
  };

  const updatePage = (newPage: number) => {
    const params = new URLSearchParams(window.location.search);
    params.set("page", newPage.toString());
    navigate(`?${params.toString()}`, { replace: true });
  };

  return (
    <>
      <div className="flex flex-col pt-28 justify-center items-center">
        <div className="container">
          {doctors.error && (
            <Alert className="w-1/3 mx-auto" variant="destructive">
              <AlertTitle>Error</AlertTitle>
              <AlertDescription>{doctors.error.message}</AlertDescription>
            </Alert>
          )}
          <h1 className="text-5xl font-semibold">Search</h1>
          <Form {...form}>
            <form onSubmit={form.handleSubmit(onSubmit)}>
              <div className="flex flex-row grid grid-cols-3 gap-4 w-3/4 pt-2">
                <div className="cols-span-2">
                  <FormField
                    control={form.control}
                    name="params"
                    render={({ field }) => (
                      <div>
                        <FormControl>
                          <Input {...field} />
                        </FormControl>
                      </div>
                    )}
                  />
                </div>
                <div className="cols-span-1">
                  <Button
                    variant={"ghost"}
                    className="bg-slate-200 [box-shadow:1px_1px_2px_var(--tw-shadow-color)] shadow-slate-400 font-semibold hover:bg-[#040D12] hover:text-white"
                  >
                    Search
                  </Button>
                </div>
              </div>
            </form>
          </Form>
          <div className="grid grid-cols-4 gap-4 pt-10">
            {doctors.content.map((doctor) => (
              <Card
                className="hover:bg-slate-400 transition-all duration-300 ease-in-out cursor-pointer"
                onClick={() => navigate(`/doctors/${doctor.id}`)}
              >
                <CardHeader>
                  <CardTitle className="font-medium">
                    {doctor.firstName} {doctor.lastName}
                  </CardTitle>
                  <CardDescription className="flex flex-row gap-2">
                    {doctor.specializations &&
                      doctor.specializations.map((item) => (
                        <div className="bg-slate-100 rounded-md p-1 ">
                          <h1
                            key={item.specializationName}
                            className="text-md text-slate-700"
                          >
                            {item.specializationName}{" "}
                          </h1>
                        </div>
                      ))}
                  </CardDescription>
                  {doctor.experienceYears && (
                    <CardDescription>
                      {doctor.experienceYears} years of practical experience
                    </CardDescription>
                  )}
                  <CardDescription>{doctor.clinicAddress}</CardDescription>
                </CardHeader>
              </Card>
            ))}
          </div>
          <Pagination className="pt-10">
            <PaginationContent>
              {!doctors.first && (
                <PaginationItem>
                  <PaginationPrevious
                    className="cursor-pointer"
                    onClick={() => updatePage(doctors.number - 1)}
                  />
                </PaginationItem>
              )}

              {[doctors.number - 1, doctors.number, doctors.number + 1]
                .filter((item) => item >= 0 && item < doctors.totalPages)
                .map((item) => (
                  <PaginationItem key={item}>
                    <PaginationLink
                      className="cursor-pointer"
                      isActive={doctors.number === item}
                      onClick={() => updatePage(item)}
                    >
                      {item + 1}
                    </PaginationLink>
                  </PaginationItem>
                ))}

              {!doctors.last && (
                <PaginationItem>
                  <PaginationNext
                    className="cursor-pointer"
                    onClick={() => {
                      const nextPage = doctors.number + 1;
                      if (nextPage < doctors.totalPages) {
                        updatePage(nextPage);
                      }
                    }}
                  />
                </PaginationItem>
              )}
            </PaginationContent>
          </Pagination>
        </div>
      </div>
    </>
  );
};
