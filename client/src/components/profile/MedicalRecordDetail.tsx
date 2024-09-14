import { setDetailedRecord } from "@/redux/action/recordActions";
import { AppDispatch, RootState } from "@/redux/store";
import { useEffect } from "react";
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
import { InfoIcon } from "lucide-react";
import { ForbiddenPage } from "@/pages/ForbiddenPage";
import { EditMedicine } from "./EditMedicine";
import { setMedicineForPrescription } from "@/redux/action/medicineActions";
import { Alert, AlertDescription, AlertTitle } from "../ui/alert";
import { EditRecord } from "./EditRecord";
import { CreatePrescription } from "./CreatePrescription";
import { CreateMedicine } from "./CreateMedicine";

export const MedicalRecordDetail = () => {
  const id = useParams().id;
  const record = useSelector((state: RootState) => state.record);
  const dispatch = useDispatch<AppDispatch>();
  const medicine_data = useSelector((state: RootState) => state.medicine);
  const medicine = medicine_data.data;

  useEffect(() => {
    const getDetailedRecord = async (id: number) => {
      dispatch(setDetailedRecord(id));
      // TODO: when persisted state will be established, handle the frontend logic of access for doctors
    };

    getDetailedRecord(parseInt(id as string));
  }, [dispatch, id]);

  const data = record.content[0];
  useEffect(() => {
    const getMedicineToPrescription = async (id: number) => {
      dispatch(setMedicineForPrescription(id));
    };

    getMedicineToPrescription(data.prescription?.id as number);
  }, [dispatch, data.prescription?.id]);

  return (
    <>
      {record.error && record.error.status == 403 ? (
        <ForbiddenPage message={record.error.message} />
      ) : (
        <div className="flex flex-col pt-28 justify-center items-center">
          <div className="container">
            {medicine_data.error && medicine_data.error.status == 400 && (
              <Alert className="w-1/3 mx-auto" variant="destructive">
                <AlertTitle>Error</AlertTitle>
                <AlertDescription>
                  {medicine_data.error.message}
                </AlertDescription>
              </Alert>
            )}
            {medicine_data.error && medicine_data.error.status == 403 && (
              <ForbiddenPage message={medicine_data.error.message} />
            )}
            {record.error && (
              <Alert className="w-1/3 mx-auto" variant="destructive">
                <AlertTitle>Error</AlertTitle>
                <AlertDescription>
                  {record.error.message ||
                    "An error occurred. Please try again later."}
                </AlertDescription>
              </Alert>
            )}
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
                      Patient: {data.patient?.firstName}{" "}
                      {data.patient?.lastName}
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
                      {medicine &&
                        medicine.map((item) => (
                          <div className="flex flex-row gap-2">
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
                            <EditMedicine key={item.id} medicine={item} />
                          </div>
                        ))}
                      {medicine && medicine.length == 0 && (
                        <CreateMedicine id={data.prescription.id} />
                      )}
                    </CardContent>
                  </div>
                )}
              </div>
              <div className="grid grid-cols-2 w-[300px] pl-6 pb-4">
                <EditRecord />
                {!data.prescription && (
                  <CreatePrescription
                    id={data.id}
                    patientId={data.patient?.id}
                    recordDate={data.recordDate}
                    diagnosis={data.diagnosis}
                  />
                )}
              </div>
            </Card>
          </div>
        </div>
      )}
    </>
  );
};
