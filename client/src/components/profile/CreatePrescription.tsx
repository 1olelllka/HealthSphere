import { AppDispatch } from "@/redux/store";
import { Button } from "../ui/button";
import {
  Dialog,
  DialogTrigger,
  DialogContent,
  DialogHeader,
  DialogFooter,
  DialogTitle,
  DialogDescription,
  DialogClose,
} from "../ui/dialog";

import { useDispatch } from "react-redux";
import { createPrescriptionForRecord } from "@/redux/action/recordActions";

export const CreatePrescription = (props: {
  id: number;
  patientId: number;
  recordDate: string;
  diagnosis: string;
}) => {
  const dispatch = useDispatch<AppDispatch>();

  const onSumbit = (values: {
    id: number;
    patient: { id: number };
    diagnosis: string;
    recordDate: string;
  }) => {
    dispatch(createPrescriptionForRecord(values));
  };

  return (
    <div className="mt-2.5">
      <Dialog>
        <DialogTrigger asChild>
          <Button variant={"default"}>Create Prescription</Button>
        </DialogTrigger>
        <DialogContent className="sm:max-w-[500px] max-h-[700px]">
          <DialogHeader>
            <DialogTitle>Create Prescription</DialogTitle>
            <DialogDescription>
              By clicking the button, you can create a new prescription.
            </DialogDescription>
          </DialogHeader>
          <DialogFooter>
            <div className="flex flex-row grid grid-cols-2 gap-4 w-1/2 pt-4">
              <DialogClose asChild>
                <Button
                  variant={"destructive"}
                  onClick={() =>
                    onSumbit({
                      id: props.id,
                      patient: {
                        id: props.patientId,
                      },
                      diagnosis: props.diagnosis,
                      recordDate: props.recordDate,
                    })
                  }
                >
                  Create
                </Button>
              </DialogClose>
              <DialogClose asChild>
                <Button variant={"default"}>Cancel</Button>
              </DialogClose>
            </div>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
};
