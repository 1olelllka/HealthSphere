import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import { useForm } from "react-hook-form";
import { Form, FormControl, FormField } from "../ui/form";
import { Input } from "../ui/input";
import { Button } from "../ui/button";
import { useDispatch, useSelector } from "react-redux";
import { AppDispatch, RootState } from "@/redux/store";
import { useEffect } from "react";
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
import { getAllPatients } from "@/redux/action/patientActions";
import { ForbiddenPage } from "@/pages/ForbiddenPage";
import { UnauthorizedPage } from "@/pages/UnauthorizedPage";
import { LoadingPage } from "@/pages/LoadingPage";

const schema = z.object({
  params: z.string().optional(),
});
export const PatientList = () => {
  const patients = useSelector((state: RootState) => state.patient);
  const dispatch = useDispatch<AppDispatch>();
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const page = parseInt(searchParams.get("page") || "0", 10);

  useEffect(() => {
    const getPatients = async () => {
      dispatch(
        getAllPatients({ params: searchParams.get("search") ?? "", page: page })
      );
    };
    getPatients();
  }, [dispatch, page, searchParams]);

  const form = useForm<z.infer<typeof schema>>({
    resolver: zodResolver(schema),
    defaultValues: { params: "" },
  });

  const onSubmit = async (values: z.infer<typeof schema>) => {
    dispatch(getAllPatients({ params: values.params ?? "", page: page }));
    navigate(`?search=${values.params}`, { replace: true });
  };

  const updatePage = (newPage: number) => {
    const params = new URLSearchParams(window.location.search);
    params.set("page", newPage.toString());
    navigate(`?${params.toString()}`, { replace: true });
  };
  return (
    <>
      {patients.error && patients.error.status === 403 ? (
        <ForbiddenPage />
      ) : patients.error && patients.error.status === 401 ? (
        <UnauthorizedPage message={patients.error.message} />
      ) : patients.loading ? (
        <LoadingPage />
      ) : (
        <div className="flex flex-col pt-28 justify-center items-center">
          <div className="container">
            <h1 className="text-5xl font-semibold">Patient Search</h1>
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
              {patients.content.map((patient) => (
                <Card
                  key={patient.id}
                  className="hover:bg-slate-400 transition-all duration-300 ease-in-out cursor-pointer"
                  onClick={() => navigate(`/patients/${patient.id}`)}
                >
                  <CardHeader>
                    <CardTitle className="font-medium">
                      {patient.firstName} {patient.lastName}
                    </CardTitle>
                    <CardDescription className="flex flex-row gap-2">
                      {patient.email}
                    </CardDescription>
                  </CardHeader>
                </Card>
              ))}
            </div>
            <Pagination className="pt-10">
              <PaginationContent>
                {!patients.first && (
                  <PaginationItem>
                    <PaginationPrevious
                      className="cursor-pointer"
                      onClick={() => updatePage(patients.number - 1)}
                    />
                  </PaginationItem>
                )}

                {[patients.number - 1, patients.number, patients.number + 1]
                  .filter((item) => item >= 0 && item < patients.totalPages)
                  .map((item) => (
                    <PaginationItem key={item}>
                      <PaginationLink
                        className="cursor-pointer"
                        isActive={patients.number === item}
                        onClick={() => updatePage(item)}
                      >
                        {item + 1}
                      </PaginationLink>
                    </PaginationItem>
                  ))}

                {!patients.last && (
                  <PaginationItem>
                    <PaginationNext
                      className="cursor-pointer"
                      onClick={() => {
                        const nextPage = patients.number + 1;
                        if (nextPage < patients.totalPages) {
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
      )}
    </>
  );
};
