import { BsInstagram, BsGithub, BsLinkedin } from "react-icons/bs";

export const Social = () => {
  return (
    <>
      <div className="flex flex-row justify-start space-x-5 pt-8">
        <BsInstagram
          className="text-3xl text-white cursor-pointer hover:opacity-80"
          onClick={() =>
            window.open("https://www.instagram.com/1olelllka/", "_blank")
          }
        />
        <BsGithub
          className="text-3xl text-white cursor-pointer hover:opacity-80"
          onClick={() => window.open("https://github.com/1olelllka", "_blank")}
        />
        <BsLinkedin
          className="text-3xl text-white cursor-pointer hover:opacity-80"
          onClick={() =>
            window.open(
              "https://www.linkedin.com/in/oleh-sichko-5a5b6231a",
              "_blank"
            )
          }
        />
      </div>
    </>
  );
};
