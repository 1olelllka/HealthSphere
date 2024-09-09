/** @type {import('tailwindcss').Config} */
export default {
  content: ["./src/**/*.{js,jsx,ts,tsx}"],
  theme: {
    extend: {
      fontFamily: {
        body: ["Roboto", "sans-serif"],
      },
      backgroundColor: {
        primary: "#eee",
      },
    },
  },
  plugins: [require("tailwindcss-animate")],
  darkMode: "class",
};
