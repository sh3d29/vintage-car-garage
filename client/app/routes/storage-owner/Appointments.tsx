import { useEffect, useState } from "react";
import { Group, Modal, Select, Stack, Table, Title } from "@mantine/core";
import { DatePickerInput, TimeInput } from "@mantine/dates";
import { useDisclosure } from "@mantine/hooks";
import { modals } from "@mantine/modals";
import { notifications } from "@mantine/notifications";
import dayjs from "dayjs";
import { useAuthContext } from "~/context/AuthContext";
import { api } from "~/api/fetch";
import { EmptyState } from "~/components/EmptyState";
import {
  AppointmentStatusBadge,
  AppointmentTypeBadge,
} from "~/components/StatusBadge";
import {
  CancelButton,
  ConfirmButton,
  DeleteButton,
  PrimaryButton,
} from "~/components/Button";
import type { AppointmentOutputDto, AppointmentType } from "~/types/dtos";

export default function Appointments() {
  const { user } = useAuthContext();
  const id = user!.id;
  const [appointments, setAppointments] = useState<AppointmentOutputDto[]>([]);
  const [loading, setLoading] = useState(true);
  const [opened, { open, close }] = useDisclosure(false);

  const [type, setType] = useState<AppointmentType>("EINLAGERUNG");
  const [date, setDate] = useState<string | null>(null);
  const [time, setTime] = useState("");

  async function load() {
    setLoading(true);
    const { value } = await api.get<AppointmentOutputDto[]>(
      `/storage-owners/${id}/appointments`,
    );
    setAppointments(value ?? []);
    setLoading(false);
  }

  useEffect(() => {
    load();
  }, [id]);

  function openCreate() {
    setType("EINLAGERUNG");
    setDate(null);
    setTime("");
    open();
  }

  async function create() {
    if (!date || !time) {
      notifications.show({
        color: "red",
        message: "Bitte Datum und Uhrzeit wählen.",
      });
      return;
    }
    const { error } = await api.post(`/storage-owners/${id}/appointments`, {
      type,
      date: `${date}T${time}:00`,
      time: `${time}:00`,
    });
    if (error) {
      notifications.show({ color: "red", message: error.message });
      return;
    }
    notifications.show({ color: "green", message: "Termin angelegt." });
    close();
    load();
  }

  function confirmDelete(a: AppointmentOutputDto) {
    modals.openConfirmModal({
      title: "Termin löschen",
      children: "Diesen Terminslot wirklich löschen?",
      labels: { confirm: "Löschen", cancel: "Abbrechen" },
      confirmProps: { color: "red" },
      onConfirm: async () => {
        const { error } = await api.delete(
          `/storage-owners/${id}/appointments/${a.id}`,
        );
        if (error) {
          notifications.show({ color: "red", message: error.message });
          return;
        }
        notifications.show({ color: "green", message: "Termin gelöscht." });
        load();
      },
    });
  }

  function confirmComplete(a: AppointmentOutputDto) {
    const einlagerung = a.type === "EINLAGERUNG";
    modals.openConfirmModal({
      title: "Termin abschließen",
      children: einlagerung
        ? "Das Fahrzeug wird damit als eingelagert markiert."
        : "Das Fahrzeug wird damit als ausgelagert markiert.",
      labels: { confirm: "Abschließen", cancel: "Abbrechen" },
      confirmProps: { color: "green" },
      onConfirm: async () => {
        const { error } = await api.patch(
          `/storage-owners/${id}/appointments/${a.id}/complete`,
        );
        if (error) {
          notifications.show({ color: "red", message: error.message });
          return;
        }
        notifications.show({
          color: "green",
          message: "Termin abgeschlossen.",
        });
        load();
      },
    });
  }

  return (
    <Stack>
      <Group justify="space-between">
        <Title order={2}>Kalender</Title>
        <PrimaryButton onClick={openCreate}>Termin anlegen</PrimaryButton>
      </Group>

      {!loading && appointments.length === 0 ? (
        <EmptyState
          title="Keine Termine"
          description="Lege freie Ein- und Auslagerungstermine an, die Einlagerer buchen können."
          action={
            <PrimaryButton onClick={openCreate}>Termin anlegen</PrimaryButton>
          }
        />
      ) : (
        <Table.ScrollContainer minWidth={520}>
          <Table verticalSpacing="sm">
            <Table.Thead>
              <Table.Tr>
                <Table.Th>Typ</Table.Th>
                <Table.Th>Status</Table.Th>
                <Table.Th>Datum & Zeit</Table.Th>
                <Table.Th />
              </Table.Tr>
            </Table.Thead>
            <Table.Tbody>
              {appointments.map((a) => (
                <Table.Tr key={a.id}>
                  <Table.Td>
                    <AppointmentTypeBadge type={a.type} />
                  </Table.Td>
                  <Table.Td>
                    <AppointmentStatusBadge status={a.status} />
                  </Table.Td>
                  <Table.Td>
                    {dayjs(a.date).format("DD.MM.YYYY")} · {a.time.slice(0, 5)}{" "}
                    Uhr
                  </Table.Td>
                  <Table.Td>
                    <Group justify="flex-end">
                      {a.status === "BOOKED" && (
                        <ConfirmButton onClick={() => confirmComplete(a)}>
                          Abschließen
                        </ConfirmButton>
                      )}
                      <DeleteButton onClick={() => confirmDelete(a)}>
                        Löschen
                      </DeleteButton>
                    </Group>
                  </Table.Td>
                </Table.Tr>
              ))}
            </Table.Tbody>
          </Table>
        </Table.ScrollContainer>
      )}

      <Modal opened={opened} onClose={close} title="Termin anlegen">
        <Stack>
          <Select
            label="Typ"
            data={[
              { value: "EINLAGERUNG", label: "Einlagerung" },
              { value: "AUSLAGERUNG", label: "Auslagerung" },
            ]}
            value={type}
            onChange={(v) => setType((v as AppointmentType) ?? "EINLAGERUNG")}
          />
          <DatePickerInput
            label="Datum"
            placeholder="Datum wählen"
            value={date}
            onChange={setDate}
          />
          <TimeInput
            label="Uhrzeit"
            value={time}
            onChange={(e) => setTime(e.currentTarget.value)}
          />
          <Group justify="flex-end">
            <CancelButton onClick={close}>Abbrechen</CancelButton>
            <PrimaryButton onClick={create}>Anlegen</PrimaryButton>
          </Group>
        </Stack>
      </Modal>
    </Stack>
  );
}
