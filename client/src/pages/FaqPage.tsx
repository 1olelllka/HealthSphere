import {
  Accordion,
  AccordionContent,
  AccordionItem,
  AccordionTrigger,
} from "@/components/ui/accordion";

export const FaqPage = () => {
  return (
    <>
      <div className="flex flex-col pt-16 justify-center items-center">
        <div className="container">
          <h1 className="text-6xl">FAQs</h1>
          <h1 className="text-3xl pt-10">General Questions</h1>
          <Accordion type="single" collapsible className="mx-auto mt-10 w-4/5">
            <AccordionItem value="item-0">
              <AccordionTrigger className="text-2xl">
                What is HealthSphere?
              </AccordionTrigger>
              <AccordionContent className="text-lg">
                HealthSphere is a web-based application that allows patients to
                make appointments to specific doctors, view their medical
                records, which may include prescriptions and further details.
                Doctors can manage specific medical records, prescriptions, book
                an appointment for specific patients. This application is
                open-source and is not intended to be used in a real-world
                environment for personal medical records.
              </AccordionContent>
            </AccordionItem>
            <AccordionItem value="item-1">
              <AccordionTrigger className="text-2xl">
                How to start using HealthSphere?
              </AccordionTrigger>
              <AccordionContent className="text-lg">
                If you are new to HealthSphere, you should fill in the register
                form, which you can find by clicking on navigation bar on the
                left. After successful registration, you will be redirected to
                your newly created profile.
              </AccordionContent>
            </AccordionItem>
            <AccordionItem value="item-2">
              <AccordionTrigger className="text-2xl">
                What features are available on HealthSphere?
              </AccordionTrigger>
              <AccordionContent className="text-lg">
                As a patient, you may make appointments to specific doctors,
                view your medical records, which may include prescriptions and
                further details. Doctors can manage specific medical records,
                prescriptions, book an appointment for specific patients.
              </AccordionContent>
            </AccordionItem>
            <AccordionItem value="item-3">
              <AccordionTrigger className="text-2xl">
                Is HealthSphere free to use?
              </AccordionTrigger>
              <AccordionContent className="text-lg">
                Yes, completely free to use. We do not charge any fees for using
                our services. However, doctors may charge a fee for their
                services.
              </AccordionContent>
            </AccordionItem>
            <AccordionItem value="item-4">
              <AccordionTrigger className="text-2xl">
                How can I view my upcoming or past appointments?
              </AccordionTrigger>
              <AccordionContent className="text-lg">
                Go to the "Appointments" section, in the sidebar. You can view,
                edit and cancel your appointments.
              </AccordionContent>
            </AccordionItem>
            <AccordionItem value="item-5">
              <AccordionTrigger className="text-2xl">
                How to edit/delete my profile?
              </AccordionTrigger>
              <AccordionContent className="text-lg">
                To edit your profile, navigate to your profile page and click
                the three-dots icon in the top right corner. Select "Edit
                Profile" to make changes to your profile. To delete your
                profile, select "Delete Profile". Please be aware that this
                action is permanent and cannot be undone. Additionally, all of
                your upcoming appointments and personal information will be
                deleted.
              </AccordionContent>
            </AccordionItem>
            <AccordionItem value="item-6">
              <AccordionTrigger className="text-2xl">
                Is my data secure?
              </AccordionTrigger>
              <AccordionContent className="text-lg">
                Your data is secure with us. We use the latest encryption
                methods to protect your personal information and health records
                from unauthorized access. We also have strict policies in place
                to ensure that only authorized personnel have access to your
                data.
              </AccordionContent>
            </AccordionItem>
            <AccordionItem value="item-7">
              <AccordionTrigger className="text-2xl">
                What to do if I encounter a technical error?
              </AccordionTrigger>
              <AccordionContent className="text-lg">
                In the event that you encounter a technical issue while using
                our website or application, please do not hesitate to reach out
                to our dedicated support team at{" "}
                <a
                  className="text-blue-500"
                  href="mailto:support@healthsphere.com"
                >
                  support@healthsphere.com
                </a>
                . When submitting your query, please provide as much information
                as possible about the issue you are experiencing, including any
                error messages you may have encountered, the steps you took
                leading up to the issue, and any other relevant details. We are
                committed to ensuring that our platform is reliable and
                efficient, and your feedback is invaluable in helping us to
                achieve this goal.
              </AccordionContent>
            </AccordionItem>
          </Accordion>
          <h1 className="text-3xl pt-10">Patient-Specific Questions</h1>
          <Accordion type="single" collapsible className="mx-auto mt-10 w-4/5">
            <AccordionItem value="item-1">
              <AccordionTrigger className="text-2xl">
                How to book an appointment?
              </AccordionTrigger>
              <AccordionContent className="text-lg">
                To book an appointment, navigate to the "Doctors" page and
                select the doctor you wish to be examinated by. Scroll down to
                the "Book Appointment" section, where you will find a calendar
                displaying available time slots. Click on a specific time slot
                and confirm your appointment. Please note that time slots marked
                in red are unavailable.
              </AccordionContent>
            </AccordionItem>
            <AccordionItem value="item-2">
              <AccordionTrigger className="text-2xl">
                Who creates my medical records?
              </AccordionTrigger>
              <AccordionContent className="text-lg">
                Your medical records are created, edited and deleted by your
                doctor, who is the only authorized person to do so. You can view
                your medical records on your profile page.
              </AccordionContent>
            </AccordionItem>
            <AccordionItem value="item-3">
              <AccordionTrigger className="text-2xl">
                Who may view my profile information?
              </AccordionTrigger>
              <AccordionContent className="text-lg">
                Only authorized doctors have access to your profile information,
                which is used solely for the purpose of providing you with
                medical care and services. We take the confidentiality and
                privacy of your personal information seriously and do not share
                it with other patients or third parties without your explicit
                consent.
              </AccordionContent>
            </AccordionItem>
          </Accordion>
          <h1 className="text-3xl pt-10">Doctor-Specific Questions</h1>
          <Accordion type="single" collapsible className="mx-auto mt-10 w-4/5">
            <AccordionItem value="item-1">
              <AccordionTrigger className="text-2xl">
                How to book an appointment for patient?
              </AccordionTrigger>
              <AccordionContent className="text-lg">
                To book an appointment for a patient, navigate to the "Patients"
                page and select the patient you wish to examine. Then, scroll
                down to the "Book Appointment" section, where you will see a
                calendar with available appointments. Click on the desired time
                slot and confirm the appointment. Please note that time slots
                marked in red are unavailable.
              </AccordionContent>
            </AccordionItem>
            <AccordionItem value="item-2">
              <AccordionTrigger className="text-2xl">
                How to create a medical record?
              </AccordionTrigger>
              <AccordionContent className="text-lg">
                To create a medical record, navigate to the "Appointments" page
                and select the specific appointment for which you wish to create
                a medical record. Below the appointment details, you should see
                a "Create a Medical Record" button. Click on this button to be
                redirected to a medical record page, where you can click on
                "Edit Medical Record" to enter the relevant data in the medical
                record. Please note that it is not possible to create medical
                records two times for the same appointment. You are allowed to
                view, edit and delete the medical records.
              </AccordionContent>
            </AccordionItem>
            <AccordionItem value="item-3">
              <AccordionTrigger className="text-2xl">
                Who may view my profile information?
              </AccordionTrigger>
              <AccordionContent className="text-lg">
                Your profile information is publicly accessible, but only
                authorized patients are able to make an appointment with you.
              </AccordionContent>
            </AccordionItem>
          </Accordion>
        </div>
      </div>
    </>
  );
};
