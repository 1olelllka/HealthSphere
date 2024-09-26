import {
  deleteMedicalRecord,
  deletePrescriptionForRecord,
  setDetailedRecord,
} from "@/redux/action/recordActions";
import { AppDispatch, RootState } from "@/redux/store";
import { useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import { useNavigate, useParams } from "react-router-dom";
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
import { ArrowLeft, InfoIcon } from "lucide-react";
import { ForbiddenPage } from "@/pages/ForbiddenPage";
import { EditMedicine } from "./EditMedicine";
import { setMedicineForPrescription } from "@/redux/action/medicineActions";
import { Alert, AlertDescription, AlertTitle } from "../ui/alert";
import { EditRecord } from "./EditRecord";
import { CreatePrescription } from "./CreatePrescription";
import { CreateMedicine } from "./CreateMedicine";
import { Button } from "../ui/button";
import {
  Dialog,
  DialogClose,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "../ui/dialog";
import { UnauthorizedPage } from "@/pages/UnauthorizedPage";

export const MedicalRecordDetail = () => {
  const id = useParams().id;
  const record = useSelector((state: RootState) => state.record);
  const dispatch = useDispatch<AppDispatch>();
  const medicine_data = useSelector((state: RootState) => state.medicine);
  const medicine = medicine_data.data;
  const profile = useSelector((state: RootState) => state.profile.data);
  const navigate = useNavigate();

  useEffect(() => {
    const getDetailedRecord = async (id: number) => {
      dispatch(setDetailedRecord(id));
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
      ) : record.error && record.error.status === 401 ? (
        <UnauthorizedPage message={record.error.message} />
      ) : (
        <div
          className={`flex flex-col pt-20 justify-center items-center ${
            profile.user.role === "ROLE_PATIENT" && "pb-40"
          }`}
        >
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
            <div className="flex flex-row gap-4">
              <div className="bg-slate-50 p-1 rounded-lg hover:bg-slate-200 transition-color duration-300">
                <ArrowLeft onClick={() => navigate(-1)} size={32} />
              </div>
              <h1 className="text-3xl mt-1">Medical record</h1>
            </div>
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
              {profile.user.role === "ROLE_DOCTOR" && (
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
                  {data.prescription && (
                    <div className="mt-2.5">
                      <Dialog>
                        <DialogTrigger asChild>
                          <Button variant={"destructive"}>
                            Delete Prescription
                          </Button>
                        </DialogTrigger>
                        <DialogContent>
                          <DialogHeader>
                            <DialogTitle>
                              Are you sure you want to delete this prescription?
                            </DialogTitle>
                          </DialogHeader>
                          <DialogFooter>
                            <DialogClose asChild>
                              <Button variant={"outline"}>Cancel</Button>
                            </DialogClose>
                            <Button
                              variant={"destructive"}
                              onClick={() => {
                                dispatch(
                                  deletePrescriptionForRecord({
                                    prescriptionId: data.prescription
                                      ?.id as number,
                                    medicalRecordId: data.id,
                                  })
                                );
                              }}
                            >
                              Yes
                            </Button>
                          </DialogFooter>
                        </DialogContent>
                      </Dialog>
                    </div>
                  )}
                </div>
              )}
            </Card>
            {profile.user.role === "ROLE_DOCTOR" && (
              <>
                <h1 className="text-3xl text-red-500 pt-10">Danger Zone</h1>
                <div className="mt-3">
                  <Dialog>
                    <DialogTrigger>
                      <Button variant={"destructive"}>Delete Record</Button>
                    </DialogTrigger>
                    <DialogContent>
                      <DialogHeader>
                        <DialogTitle>
                          Are you sure you want to delete this record?
                        </DialogTitle>
                        <DialogDescription>
                          Please note, this action cannot be undone and all data
                          will be lost.
                        </DialogDescription>
                      </DialogHeader>
                      <DialogFooter>
                        <DialogClose asChild>
                          <Button variant={"default"}>Close</Button>
                        </DialogClose>
                        <Button
                          variant={"destructive"}
                          onClick={() => {
                            dispatch(deleteMedicalRecord(data.id));
                            navigate(-1);
                          }}
                        >
                          Delete
                        </Button>
                      </DialogFooter>
                    </DialogContent>
                  </Dialog>
                </div>
              </>
            )}
          </div>
        </div>
      )}
    </>
  );
};
