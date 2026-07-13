import {
  index,
  layout,
  prefix,
  route,
  type RouteConfig,
} from "@react-router/dev/routes";

export default [
  index("routes/Login.tsx"),
  route("register", "routes/Register.tsx"),

  layout("routes/car-owner/Layout.tsx", [
    ...prefix("car-owner", [
      route("dashboard", "routes/car-owner/Dashboard.tsx"),
      route("search", "routes/car-owner/Search.tsx"),
      route("requests", "routes/car-owner/Requests.tsx"),
      route("offers", "routes/car-owner/Offers.tsx"),
      route("cars", "routes/car-owner/Cars.tsx"),
      route("appointments", "routes/car-owner/Appointments.tsx"),
    ]),
  ]),

  layout("routes/storage-owner/Layout.tsx", [
    ...prefix("storage-owner", [
      route("dashboard", "routes/storage-owner/Dashboard.tsx"),
      route("services", "routes/storage-owner/Services.tsx"),
      route("requests", "routes/storage-owner/Requests.tsx"),
      route("offers", "routes/storage-owner/Offers.tsx"),
      route("stored-cars", "routes/storage-owner/StoredCars.tsx"),
      route("appointments", "routes/storage-owner/Appointments.tsx"),
    ]),
  ]),
] satisfies RouteConfig;
