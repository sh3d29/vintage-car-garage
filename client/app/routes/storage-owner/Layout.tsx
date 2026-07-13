import AppLayout, { type NavItem } from "~/components/AppLayout";

const links: NavItem[] = [
  { to: "/storage-owner/dashboard", label: "Dashboard" },
  { to: "/storage-owner/stored-cars", label: "Lager" },
  { to: "/storage-owner/services", label: "Serviceportfolio" },
  { to: "/storage-owner/requests", label: "Stellplatzanfragen" },
  { to: "/storage-owner/offers", label: "Stellplatzangebote" },
  { to: "/storage-owner/appointments", label: "Kalender" },
];

export default function StorageOwnerLayout() {
  return <AppLayout links={links} requiredRole="storage-owner" />;
}
