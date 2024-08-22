import { Link } from "react-router-dom";
import "../../index.css";
import { Button } from "../ui/button";
import { Accordion, AccordionItem } from "../ui/accordion";
import { AccordionContent, AccordionTrigger } from "@radix-ui/react-accordion";
import { Label } from "../ui/label";

export default function Home() {
  return (
    <>
      <div className="Page h-full px-10 w-full">
        <div className="navbar">
          <h1 className="text-4xl text-center pt-10 font-bold">
            Welcome to the HealthSphere
          </h1>
          <h4 className="text-center pt-5">
            "HealthSphere is a place where you can track your health, schedule
            appointments, and more."
          </h4>
        </div>
        <div className="flex flex-row justify-center mt-10">
          <div className="pr-5">
            <Link to={"/login"}>
              <Button variant="outline" className="min-w-40 max-w-52">
                Log In
              </Button>
            </Link>
          </div>
          <div className="pl-5">
            <Link to={"/register"}>
              <Button variant="default" className="min-w-40 max-w-52">
                Sign Up
              </Button>
            </Link>
            <p className="text-red-500 text-sm">*For Patients Only</p>
          </div>
        </div>
        <div className="why">
          <h2 className="text-3xl pt-10 font-bold pb-3">
            Why Choose HealthSphere?
          </h2>
          <Label className="text-base">
            Choosing HealthSphere Health Management System means embracing a
            comprehensive, secure, and user-friendly solution for managing your
            health. Our platform simplifies your health journey by offering
            centralized access to medical records, appointments, and
            prescriptions, all while ensuring your data is protected with
            advanced security measures. With 24/7 access on any device,
            personalized care options, and easy appointment scheduling, we
            empower you to take control of your health anytime, anywhere.
            Trusted by healthcare professionals and designed to support both
            individuals and families, HealthSphere offers a seamless and modern
            approach to health management.
          </Label>
        </div>
        <div className="FAQ">
          {/* <div className="FAQ">  TODO: Create normal FAQ here */}
          <h2 className="text-3xl pt-10 font-bold">
            Frequently Asked Questions
          </h2>
          <h3 className="text-2xl pt-10">General Questions</h3>
          <Accordion type="single" collapsible className="pt-5">
            <AccordionItem value="item-1">
              <AccordionTrigger className="text-xl">
                What is HealthSphere HMS?
              </AccordionTrigger>
              <AccordionContent className="text-base">
                HealthSphere Health Management System is a comprehensive digital
                platform designed to help individuals and healthcare providers
                manage health data, appointments, prescriptions, and wellness
                programs. It offers tools for tracking vital signs, managing
                medical records, and connecting with healthcare professionals.
              </AccordionContent>
            </AccordionItem>
            <AccordionItem value="item-2">
              <AccordionTrigger className="text-xl">
                Who can use this platform?
              </AccordionTrigger>
              <AccordionContent className="text-base">
                The platform is designed for patients, healthcare providers
                (doctors, nurses, specialists), and administrators. Whether
                you're managing your personal health or providing care for
                others, the system is tailored to meet your needs.
              </AccordionContent>
            </AccordionItem>
            <AccordionItem value="item-3">
              <AccordionTrigger className="text-xl">
                How do I create an account?
              </AccordionTrigger>
              <AccordionContent className="text-base">
                You can create an account by clicking on the "Sign Up" button on
                the homepage. Once you have an account, you can log in as a
                patient. If you're a doctor, you need to contact the
                administrator to create an account. The contact email is:
                6gKoA@example.com
              </AccordionContent>
            </AccordionItem>
          </Accordion>
          <h3 className="text-2xl pt-10">Patient Specific Questions</h3>
          <Accordion type="single" collapsible className="pt-5">
            <AccordionItem value="item-1">
              <AccordionTrigger className="text-xl">
                What is HealthSphere HMS?
              </AccordionTrigger>
              <AccordionContent className="text-base">
                HealthSphere Health Management System is a comprehensive digital
                platform designed to help individuals and healthcare providers
                manage health data, appointments, prescriptions, and wellness
                programs. It offers tools for tracking vital signs, managing
                medical records, and connecting with healthcare professionals.
              </AccordionContent>
            </AccordionItem>
            <AccordionItem value="item-2">
              <AccordionTrigger className="text-xl">
                Who can use this platform?
              </AccordionTrigger>
              <AccordionContent className="text-base">
                The platform is designed for patients, healthcare providers
                (doctors, nurses, specialists), and administrators. Whether
                you're managing your personal health or providing care for
                others, the system is tailored to meet your needs.
              </AccordionContent>
            </AccordionItem>
            <AccordionItem value="item-3">
              <AccordionTrigger className="text-xl">
                How do I create an account?
              </AccordionTrigger>
              <AccordionContent className="text-base">
                You can create an account by clicking on the "Sign Up" button on
                the homepage. Once you have an account, you can log in as a
                patient. If you're a doctor, you need to contact the
                administrator to create an account. The contact email is:
                6gKoA@example.com
              </AccordionContent>
            </AccordionItem>
          </Accordion>
          <h3 className="text-2xl pt-10">Doctor Specific Questions</h3>
          <Accordion type="single" collapsible className="pt-5">
            <AccordionItem value="item-1">
              <AccordionTrigger className="text-xl">
                What is HealthSphere HMS?
              </AccordionTrigger>
              <AccordionContent className="text-base">
                HealthSphere Health Management System is a comprehensive digital
                platform designed to help individuals and healthcare providers
                manage health data, appointments, prescriptions, and wellness
                programs. It offers tools for tracking vital signs, managing
                medical records, and connecting with healthcare professionals.
              </AccordionContent>
            </AccordionItem>
            <AccordionItem value="item-2">
              <AccordionTrigger className="text-xl">
                Who can use this platform?
              </AccordionTrigger>
              <AccordionContent className="text-base">
                The platform is designed for patients, healthcare providers
                (doctors, nurses, specialists), and administrators. Whether
                you're managing your personal health or providing care for
                others, the system is tailored to meet your needs.
              </AccordionContent>
            </AccordionItem>
            <AccordionItem value="item-3">
              <AccordionTrigger className="text-xl">
                How do I create an account?
              </AccordionTrigger>
              <AccordionContent className="text-base">
                You can create an account by clicking on the "Sign Up" button on
                the homepage. Once you have an account, you can log in as a
                patient. If you're a doctor, you need to contact the
                administrator to create an account. The contact email is:
                6gKoA@example.com
              </AccordionContent>
            </AccordionItem>
          </Accordion>
        </div>
        <div className="copy">
          <p className="text-md text-center pt-10 font-bold">
            Copyright &copy; 2024 HealthSphere
          </p>
        </div>
      </div>
    </>
  );
}
