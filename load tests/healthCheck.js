import http from "k6/http";
import { sleep, check } from "k6";

export const options = {
  thresholds: {
    http_req_failed: [{ threshold: "rate<0.01", abortOnFail: true }],
    http_req_duration: ["p(95)<100"],
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
        { duration: "50s", target: 300 },
        { duration: "10s", target: 70 },
      ],
    },
  },
};

export default function () {
  const url = "http://localhost:8000/actuator/health";

  const res = http.get(url);
  check(res, { "status was 200": (r) => r.status === 200 });
  sleep(1);
}
