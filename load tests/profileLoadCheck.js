import http from "k6/http";
import { sleep, check } from "k6";

export const options = {
  thresholds: {
    http_req_failed: [{ threshold: "rate<0.01", abortOnFail: true }],
    http_req_duration: ["p(95)<2500"],
  },
  scenarios: {
    // constant_request_rate: {
    //   executor: "constant-arrival-rate",
    //   rate: 100,
    //   preAllocatedVUs: 100,
    //   timeUnit: "1s",
    //   duration: "1m",
    // },
    // average_load: {
    //   executor: "ramping-vus",
    //   stages: [
    //     { duration: "10s", target: 20 },
    //     { duration: "50s", target: 20 },
    //     { duration: "50s", target: 40 },
    //     { duration: "50s", target: 60 },
    //     { duration: "50s", target: 80 },
    //     { duration: "50s", target: 100 },
    //     { duration: "50s", target: 120 },
    //     { duration: "50s", target: 140 },
    //   ],
    // },
    stress_test: {
      executor: "ramping-vus",
      stages: [
        { duration: "10s", target: 200 },
        { duration: "50s", target: 300 },
        { duration: "10s", target: 70 },
      ],
    },
  },
};

// const jwt = async () => {
//   return await fetch("http://localhost:8000/api/v1/login", {
//     method: "POST",
//     headers: {
//       "Content-Type": "application/json",
//     },
//     body: JSON.stringify({
//       email: "iDoctor@email.com",
//       password: "olehtop2282",
//     }),
//   })
//     .then((res) => res.json())
//     .then((data) => console.log(data));
// };
// console.log(jwt());

export default function () {
  const url = "http://localhost:8000/api/v1/doctors/me";
  const params = {
    headers: {
      "Content-Type": "application/json",
      Authorization:
        "Bearer eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJpRG9jdG9yQGVtYWlsLmNvbSIsImlhdCI6MTcyNzM2MjcwOSwiZXhwIjoxNzI3MzY2MzA5fQ.LyFr_y2qY0Knrow654-qzLa7MNUWnwFn9Ry23FsJ2vWDCN0-CgBLTfDUIpTLLc7h",
    },
  };

  const res = http.get(url, params);

  check(res, { "status was 200": (r) => r.status === 200 });
  sleep(1);
}
