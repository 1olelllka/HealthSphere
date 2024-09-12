import { setDetailedRecord } from "@/redux/action/recordActions";
import { AppDispatch, RootState } from "@/redux/store";
import { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { useParams } from "react-router-dom";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "../ui/card";
import {
  HoverCard,
  HoverCardContent,
  HoverCardTrigger,
} from "@/components/ui/hover-card";
import { SERVER_API } from "@/redux/api/utils";
import { InfoIcon } from "lucide-react";

type Medicine = {
  id: number;
  medicineName: string;
  dosage: string;
  instructions: string | null;
};

export const MedicalRecordDetail = () => {
  const id = useParams().id;
  const record = useSelector((state: RootState) => state.record);
  const dispatch = useDispatch<AppDispatch>();

  useEffect(() => {
    const getDetailedRecord = async (id: number) => {
      dispatch(setDetailedRecord(id));
    };

    getDetailedRecord(parseInt(id as string));
  }, [dispatch, id]);

  const data = record.content[0];
  const [medicine, setMedicine] = useState<Medicine[]>([]);
  useEffect(() => {
    const getMedicineToPrescription = async (id: number) => {
      try {
        const response = await SERVER_API.get(`/prescriptions/${id}/medicine`);
        if (response.status === 200) {
          setMedicine(response.data);
        }
      } catch (err) {
        console.log(err);
      }
    };

    getMedicineToPrescription(data.prescription?.id as number);
  }, [data.prescription?.id]);

  console.log(data);
  console.log(medicine);
  return (
    <>
      <div className="flex flex-col pt-28 justify-center items-center">
        <div className="container">
          <h1 className="text-3xl">Medical record #{id}</h1>
          <Card className="mt-10">
            <div className="grid grid-cols-2">
              <div className="col-span-1">
                <CardHeader>
                  <CardTitle className="text-3xl">{data.diagnosis}</CardTitle>
                  <CardDescription>
                    Record Date: {data.recordDate}
                  </CardDescription>
                  <CardDescription>
                    Last Updated:{" "}
                    {data.updatedAt &&
                      new Date(data.updatedAt).toISOString().substring(0, 10)}
                  </CardDescription>
                </CardHeader>
                <CardContent>
                  <h1 className="text-xl font-bold">
                    Patient: {data.patient?.firstName} {data.patient?.lastName}
                  </h1>
                  <h1 className="text-xl font-bold">
                    Examined by: Dr. {data.doctor?.firstName}{" "}
                    {data.doctor?.lastName}
                  </h1>
                  {data.treatment && (
                    <h1 className="text-xl">Conclusion: {data?.treatment}</h1>
                  )}
                </CardContent>
              </div>
              {data.prescription && (
                <div className="col-span-1">
                  <CardHeader>
                    <CardTitle className="text-3xl">Prescription</CardTitle>
                    <CardDescription>
                      Issued at:{" "}
                      {data.prescription.issuedDate &&
                        new Date(data.prescription?.issuedDate)
                          .toISOString()
                          .substring(0, 10)}
                    </CardDescription>
                  </CardHeader>
                  <CardContent>
                    {medicine.map((item) => (
                      <div className="flex flex-row w-[300px] bg-slate-200 mt-2 rounded-lg">
                        <h1 className="text-xl font-medium pl-3 py-2">
                          {item.medicineName} - {item.dosage}
                        </h1>
                        {item.instructions && (
                          <HoverCard>
                            <HoverCardTrigger asChild>
                              <InfoIcon className="w-5 h-5 ml-4 mt-[12px] text-slate-500" />
                            </HoverCardTrigger>
                            <HoverCardContent>
                              <p>{item.instructions}</p>
                            </HoverCardContent>
                          </HoverCard>
                        )}
                      </div>
                    ))}
                  </CardContent>
                </div>
              )}
            </div>
          </Card>
        </div>
      </div>
    </>
  );
};
