import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import { useForm } from "react-hook-form";
import { Form, FormControl, FormField, FormItem } from "../ui/form";
import { Input } from "../ui/input";
import { Button } from "../ui/button";
import { useDispatch, useSelector } from "react-redux";
import { AppDispatch, RootState } from "@/redux/store";
import { useEffect } from "react";
import { searchRecord, setRecord } from "@/redux/action/recordActions";
import {
  Card,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Popover } from "@radix-ui/react-popover";
import { PopoverContent, PopoverTrigger } from "../ui/popover";
import { cn } from "@/lib/utils";
import { CalendarIcon } from "lucide-react";
import { Calendar } from "../ui/calendar";
import { useNavigate, useSearchParams } from "react-router-dom";
import {
  Pagination,
  PaginationContent,
  PaginationItem,
  PaginationLink,
  PaginationNext,
  PaginationPrevious,
} from "@/components/ui/pagination";
import { LoadingPage } from "@/pages/LoadingPage";

const schema = z.object({
  diagnosis: z.string().optional(),
  from: z.date().optional(),
  to: z.date().optional(),
});

export const MedicalRecords = (props: { id: number }) => {
  const id = props.id;
  const navigate = useNavigate();
  const records = useSelector((state: RootState) => state.record);
  const dispatch = useDispatch<AppDispatch>();
  const [searchParams] = useSearchParams();
  const page = parseInt(searchParams.get("page") || "0", 10);

  const form = useForm<z.infer<typeof schema>>({
    resolver: zodResolver(schema),
    defaultValues: {},
  });

  useEffect(() => {
    const getAllRecords = async (id: number) => {
      if (
        !searchParams.get("diagnosis") &&
        !searchParams.get("from") &&
        !searchParams.get("to")
      ) {
        dispatch(setRecord({ id, page: page }));
      } else {
        dispatch(
          searchRecord({
            id,
            diagnosis: searchParams.get("diagnosis") ?? undefined,
            from: searchParams.get("from")
              ? new Date(searchParams.get("from")!)
              : undefined,
            to: searchParams.get("to")
              ? new Date(searchParams.get("to")!)
              : undefined,
            page: page,
          })
        );
      }
    };
    getAllRecords(id);
  }, [dispatch, id, page, searchParams]);

  const onSubmit = async (values: {
    diagnosis?: string | undefined;
    from?: Date | undefined;
    to?: Date | undefined;
  }) => {
    console.log(values);
    const params = new URLSearchParams(window.location.search);

    if (values.diagnosis) {
      params.set("diagnosis", values.diagnosis);
    } else {
      params.delete("diagnosis");
    }
    if (values.from) {
      params.set("from", values.from.toISOString());
    } else {
      params.delete("from");
    }
    if (values.to) {
      params.set("to", values.to.toISOString());
    } else {
      params.delete("to");
    }
    window.history.replaceState(
      {},
      "",
      `${window.location.pathname}?${params.toString()}`
    );
    dispatch(searchRecord({ id: id, page: 0, ...values }));
  };

  const updatePage = (newPage: number) => {
    const params = new URLSearchParams(window.location.search);
    params.set("page", newPage.toString());
    navigate(`?${params.toString()}`, { replace: true });
  };

  return (
    <>
      {records.loading ? (
        <LoadingPage />
      ) : (
        <div className="pt-10">
          <h1 className="text-3xl">Medical History</h1>
          <div className="pt-4">
            <Form {...form}>
              <form onSubmit={form.handleSubmit(onSubmit)} action="GET">
                <div className="grid grid-cols-1 gap-2">
                  <div className="cols-span-1 w-1/3">
                    <FormField
                      control={form.control}
                      name="diagnosis"
                      render={({ field }) => (
                        <FormItem>
                          <FormControl>
                            <Input
                              placeholder="Search by diagnosis..."
                              {...field}
                            />
                          </FormControl>
                        </FormItem>
                      )}
                    />
                  </div>
                  <div className="grid grid-cols-2 w-1/3 gap-2">
                    <div className="cols-span-1">
                      <FormField
                        control={form.control}
                        name="from"
                        render={({ field }) => (
                          <FormItem>
                            <Popover>
                              <PopoverTrigger asChild>
                                <Button
                                  variant={"outline"}
                                  className={cn(
                                    "w-full text-left font-normal",
                                    !field.value && "text-muted-foreground"
                                  )}
                                >
                                  {field.value ? (
                                    field.value.toDateString()
                                  ) : (
                                    <span>From</span>
                                  )}
                                  <CalendarIcon className="ml-2 h-4 w-4 opacity-50" />
                                </Button>
                              </PopoverTrigger>
                              <PopoverContent
                                className="w-auto p-0"
                                align="start"
                              >
                                <Calendar
                                  mode="single"
                                  selected={field.value}
                                  onSelect={field.onChange}
                                  disabled={(date) =>
                                    date > new Date() ||
                                    date < new Date("1900-01-01")
                                  }
                                  initialFocus
                                />
                              </PopoverContent>
                            </Popover>
                          </FormItem>
                        )}
                      />
                    </div>
                    <div className="cols-span-1">
                      <FormField
                        control={form.control}
                        name="to"
                        render={({ field }) => (
                          <FormItem>
                            <Popover>
                              <PopoverTrigger asChild>
                                <Button
                                  variant={"outline"}
                                  className={cn(
                                    "w-full pl-3 text-left font-normal",
                                    !field.value && "text-muted-foreground"
                                  )}
                                >
                                  {field.value ? (
                                    field.value.toDateString()
                                  ) : (
                                    <span>To</span>
                                  )}
                                  <CalendarIcon className="ml-2 h-4 w-4 opacity-50" />
                                </Button>
                              </PopoverTrigger>
                              <PopoverContent
                                className="w-auto p-0"
                                align="start"
                              >
                                <Calendar
                                  mode="single"
                                  selected={field.value}
                                  onSelect={field.onChange}
                                  disabled={(date) =>
                                    date > new Date() ||
                                    date < new Date("1900-01-01")
                                  }
                                  initialFocus
                                />
                              </PopoverContent>
                            </Popover>
                          </FormItem>
                        )}
                      />
                    </div>
                  </div>
                </div>
                <Button variant="outline" type="submit" className="mt-4">
                  Search
                </Button>
              </form>
            </Form>
            <div className="pt-4 grid grid-cols-4 gap-4">
              {records.content.map((record) => (
                <Card
                  key={record.id}
                  className="hover:bg-slate-200 cursor-pointer transition-all duration-300"
                  onClick={() => navigate(`/medical-records/${record.id}`)}
                >
                  <CardHeader>
                    <CardTitle>{record.diagnosis}</CardTitle>
                    <CardDescription>{record.recordDate}</CardDescription>
                  </CardHeader>
                </Card>
              ))}
            </div>
            <Pagination className="pt-10">
              <PaginationContent>
                {!records.first && (
                  <PaginationItem>
                    <PaginationPrevious
                      className="cursor-pointer"
                      onClick={() => updatePage(records.number - 1)}
                    />
                  </PaginationItem>
                )}

                {[records.number - 1, records.number, records.number + 1]
                  .filter((p) => p >= 1 && p <= records.totalPages)
                  .map((p) => (
                    <PaginationItem key={p}>
                      <PaginationLink
                        className="cursor-pointer"
                        isActive={records.number === p - 1}
                        onClick={() => updatePage(p - 1)}
                      >
                        {p}
                      </PaginationLink>
                    </PaginationItem>
                  ))}

                {!records.last && (
                  <PaginationItem>
                    <PaginationNext
                      className="cursor-pointer"
                      onClick={() => {
                        const nextPage = records.number + 1;
                        if (nextPage < records.totalPages) {
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
