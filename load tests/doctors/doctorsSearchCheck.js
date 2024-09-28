import http from "k6/http";
import { check, sleep } from "k6";

export const options = {
  thresholds: {
    http_req_failed: [{ threshold: "rate<0.01", abortOnFail: true }],
    http_req_duration: ["p(95)<2500"],
  },
  scenarios: {
    constant_request_rate: {
      executor: "constant-arrival-rate",
      rate: 100,
      preAllocatedVUs: 100,
      timeUnit: "1s",
      duration: "1m",
    },
    average_load: {
      executor: "ramping-vus",
      stages: [
        { duration: "10s", target: 20 },
        { duration: "50s", target: 20 },
        { duration: "50s", target: 40 },
        { duration: "50s", target: 60 },
        { duration: "50s", target: 80 },
        { duration: "50s", target: 100 },
        { duration: "50s", target: 120 },
        { duration: "50s", target: 140 },
      ],
    },
    stress_test: {
      executor: "ramping-vus",
      stages: [
        { duration: "10s", target: 200 },
        { duration: "50s", target: 1000 },
        { duration: "10s", target: 70 },
      ],
    },
  },
};

export default function () {
  // get all doctors
  const response = http.get(`http://localhost:8000/api/v1/doctors`);
  check(response, { "doctors fetch successful": (r) => r.status === 200 });

  // get doctors by search
  const searchResponse = http.get(
    `http://localhost:8000/api/v1/doctors?search=berry`
  );
  check(searchResponse, { "search status is 200": (r) => r.status === 200 });
  sleep(1);
}
