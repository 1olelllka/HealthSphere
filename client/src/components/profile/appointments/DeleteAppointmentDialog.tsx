import { Button } from "@/components/ui/button";
import {
  Dialog,
  DialogContent,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "../../ui/dialog";
import { useDispatch } from "react-redux";
import { AppDispatch } from "@/redux/store";
import { deleteAppointment } from "@/redux/action/appointmentActions";

export const DeleteAppointmentDialog = (props: {
  deleteOpen: boolean;
  onClose: () => void;
  selectedId: number;
}) => {
  const dispatch = useDispatch<AppDispatch>();

  return (
    <Dialog open={props.deleteOpen}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>
            Are you sure you want to delete this appointment?
          </DialogTitle>
        </DialogHeader>
        <DialogFooter className="pt-10">
          <Button variant={"outline"} onClick={() => props.onClose()}>
            Close
          </Button>
          <Button
            variant={"destructive"}
            onClick={() => {
              dispatch(deleteAppointment(props.selectedId));
              props.onClose();
            }}
          >
            Yes
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
};