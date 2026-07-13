import { Badge } from "@mantine/core";
import type {
  AppointmentStatus,
  AppointmentType,
  RequestType,
} from "~/types/dtos";

export function OfferStatusBadge({ accepted }: { accepted: boolean | null }) {
  if (accepted === true) {
    return (
      <Badge color="green" variant="light">
        Angenommen
      </Badge>
    );
  }
  if (accepted === false) {
    return (
      <Badge color="red" variant="light">
        Abgelehnt
      </Badge>
    );
  }
  return (
    <Badge color="gray" variant="light">
      Ausstehend
    </Badge>
  );
}

export function AppointmentTypeBadge({ type }: { type: AppointmentType }) {
  return (
    <Badge color={type === "EINLAGERUNG" ? "blue" : "grape"} variant="light">
      {type === "EINLAGERUNG" ? "Einlagerung" : "Auslagerung"}
    </Badge>
  );
}

const APPOINTMENT_STATUS: Record<
  AppointmentStatus,
  { color: string; label: string }
> = {
  AVAILABLE: { color: "green", label: "Frei" },
  BOOKED: { color: "blue", label: "Gebucht" },
  COMPLETED: { color: "grey", label: "Abgeschlossen" },
};

export function AppointmentStatusBadge({
  status,
}: {
  status: AppointmentStatus;
}) {
  const { color, label } = APPOINTMENT_STATUS[status];
  return (
    <Badge color={color} variant="light">
      {label}
    </Badge>
  );
}

export function ReadyToDriveBadge({ ready }: { ready: boolean }) {
  return (
    <Badge color={ready ? "green" : "grey"} variant="light">
      {ready ? "Fahrbereit" : "Nicht fahrbereit"}
    </Badge>
  );
}

export function MaintenanceBadge({ maintained }: { maintained: boolean }) {
  return (
    <Badge color={maintained ? "blue" : "grey"} variant="light">
      {maintained ? "Gewartet" : "Ungewartet"}
    </Badge>
  );
}

export function StoredBadge({ stored }: { stored: boolean }) {
  return (
    <Badge color={stored ? "teal" : "grey"} variant="light">
      {stored ? "Ja" : "Nein"}
    </Badge>
  );
}

export function ServiceKindBadge({ optional }: { optional: boolean }) {
  return (
    <Badge color={optional ? "orange" : "grey"} variant="light">
      {optional ? "Optional" : "Standard"}
    </Badge>
  );
}

export function RequestTypeBadge({ type }: { type: RequestType }) {
  return (
    <Badge color={"grey"} variant="light">
      {type === "STELLPLATZ" ? "Stellplatz" : ""}
    </Badge>
  );
}

export function BookedBadge() {
  return (
    <Badge color={"blue"} variant="light" size="sm">
      Gebucht
    </Badge>
  );
}
