import AppLayout, { type NavItem } from "~/components/AppLayout";

const links: NavItem[] = [
  { to: "/car-owner/dashboard", label: "Dashboard" },
  { to: "/car-owner/cars", label: "Fahrzeuge" },
  { to: "/car-owner/requests", label: "Stellplatzanfragen" },
  { to: "/car-owner/offers", label: "Stellplatzangebote" },
  { to: "/car-owner/appointments", label: "Termine" },
];

export default function CarOwnerLayout() {
  return <AppLayout links={links} requiredRole="car-owner" />;
}
