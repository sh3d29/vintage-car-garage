import { useEffect, useState } from "react";
import {
  Card,
  Group,
  Modal,
  Radio,
  Select,
  Stack,
  Table,
  Text,
  Title,
} from "@mantine/core";
import { useDisclosure } from "@mantine/hooks";
import { modals } from "@mantine/modals";
import { notifications } from "@mantine/notifications";
import dayjs from "dayjs";
import { useAuthContext } from "~/context/AuthContext";
import { api } from "~/api/fetch";
import { EmptyState } from "~/components/EmptyState";
import { AppointmentTypeBadge } from "~/components/StatusBadge";
import { CancelButton, DeleteButton, PrimaryButton } from "~/components/Button";
import type {
  AppointmentOutputDto,
  CarOutputDto,
  OfferOutputDto,
} from "~/types/dtos";

function formatSlot(a: AppointmentOutputDto): string {
  return `${dayjs(a.date).format("DD.MM.YYYY")} · ${a.time.slice(0, 5)}`;
}

export default function Appointments() {
  const { user } = useAuthContext();
  const id = user!.id;
  const [appointments, setAppointments] = useState<AppointmentOutputDto[]>([]);
  const [loading, setLoading] = useState(true);
  const [cars, setCars] = useState<CarOutputDto[]>([]);
  const [opened, { open, close }] = useDisclosure(false);

  const [owners, setOwners] = useState<{ id: string; companyName: string }[]>(
    [],
  );
  const [ownerId, setOwnerId] = useState<string | null>(null);
  const [slots, setSlots] = useState<AppointmentOutputDto[]>([]);
  const [slotId, setSlotId] = useState<string | null>(null);
  const [carId, setCarId] = useState<string | null>(null);

  async function load() {
    setLoading(true);
    const [appointments, cars] = await Promise.all([
      api.get<AppointmentOutputDto[]>(`/car-owners/${id}/appointments`),
      api.get<CarOutputDto[]>(`/car-owners/${id}/cars`),
    ]);
    setAppointments(appointments.value ?? []);
    setCars(cars.value ?? []);
    setLoading(false);
  }

  useEffect(() => {
    load();
  }, [id]);

  async function openBooking() {
    setOwners([]);
    setOwnerId(null);
    setSlots([]);
    setSlotId(null);
    setCarId(cars[0]?.id ?? null);
    open();
    const { value } = await api.get<OfferOutputDto[]>(
      `/car-owners/${id}/offers`,
    );
    const bookable = new Map<string, string>();
    for (const o of value ?? []) {
      if (o.accepted === true)
        bookable.set(o.storageOwnerId, o.storageOwnerName);
    }
    for (const c of cars) {
      if (c.currentStorageOwnerId && c.currentStorageOwnerName)
        bookable.set(c.currentStorageOwnerId, c.currentStorageOwnerName);
    }
    setOwners(
      [...bookable].map(([oid, companyName]) => ({ id: oid, companyName })),
    );
  }

  async function selectOwner(value: string | null) {
    setOwnerId(value);
    setSlotId(null);
    if (!value) return;
    const { value: available } = await api.get<AppointmentOutputDto[]>(
      `/car-owners/${id}/storage-owners/${value}/available-appointments`,
    );
    setSlots(available ?? []);
  }

  async function book() {
    const slot = slots.find((s) => s.id === slotId);
    if (!ownerId || !slot || !carId) {
      notifications.show({
        color: "red",
        message: "Bitte Lagerhalter, Termin und Fahrzeug wählen.",
      });
      return;
    }
    const { error } = await api.post(
      `/car-owners/${id}/storage-owners/${ownerId}/appointments`,
      {
        carId,
        type: slot.type,
        date: slot.date,
        time: slot.time,
      },
    );
    if (error) {
      notifications.show({ color: "red", message: error.message });
      return;
    }
    notifications.show({ color: "green", message: "Termin gebucht." });
    close();
    load();
  }

  function confirmCancel(a: AppointmentOutputDto) {
    modals.openConfirmModal({
      title: "Termin stornieren",
      children: `Termin am ${formatSlot(a)} wirklich stornieren?`,
      labels: { confirm: "Stornieren", cancel: "Abbrechen" },
      confirmProps: { color: "red" },
      onConfirm: async () => {
        const { error } = await api.delete(
          `/car-owners/${id}/appointments/${a.id}`,
        );
        if (error) {
          notifications.show({ color: "red", message: error.message });
          return;
        }
        notifications.show({ color: "green", message: "Termin storniert." });
        load();
      },
    });
  }

  return (
    <Stack>
      <Group justify="space-between">
        <Title order={2}>Meine Termine</Title>
        <PrimaryButton onClick={openBooking} disabled={cars.length === 0}>
          Termin buchen
        </PrimaryButton>
      </Group>

      {!loading && appointments.length === 0 ? (
        <EmptyState
          title="Keine Termine"
          description={
            cars.length === 0
              ? "Registriere zuerst ein Fahrzeug, um Termine buchen zu können."
              : "Buche einen Ein- oder Auslagerungstermin bei einem Lagerhalter."
          }
        />
      ) : (
        <Table.ScrollContainer minWidth={520}>
          <Table verticalSpacing="sm">
            <Table.Thead>
              <Table.Tr>
                <Table.Th>Typ</Table.Th>
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
                  <Table.Td>{formatSlot(a)}</Table.Td>
                  <Table.Td>
                    <Group justify="flex-end">
                      <DeleteButton onClick={() => confirmCancel(a)}>
                        Stornieren
                      </DeleteButton>
                    </Group>
                  </Table.Td>
                </Table.Tr>
              ))}
            </Table.Tbody>
          </Table>
        </Table.ScrollContainer>
      )}

      <Modal opened={opened} onClose={close} title="Termin buchen" size="lg">
        <Stack>
          {owners.length === 0 ? (
            <Text c="dimmed" size="sm">
              Keine buchbaren Lagerhalter. Nimm zuerst ein Angebot an, um
              einzulagern – oder buche eine Auslagerung für ein bereits
              eingelagertes Fahrzeug.
            </Text>
          ) : (
            <Select
              label="Lagerhalter"
              placeholder="Auswählen"
              data={owners.map((o) => ({ value: o.id, label: o.companyName }))}
              value={ownerId}
              onChange={selectOwner}
            />
          )}

          {ownerId && (
            <>
              <Text fw={600} size="sm">
                Verfügbare Termine
              </Text>
              {slots.length === 0 ? (
                <Text c="dimmed" size="sm">
                  Keine verfügbaren Termine.
                </Text>
              ) : (
                <Radio.Group value={slotId} onChange={setSlotId}>
                  <Stack gap="xs">
                    {slots.map((s) => (
                      <Card key={s.id} withBorder padding="xs">
                        <Radio
                          value={s.id}
                          label={
                            <Group gap="xs">
                              <AppointmentTypeBadge type={s.type} />
                              <span>{formatSlot(s)}</span>
                            </Group>
                          }
                        />
                      </Card>
                    ))}
                  </Stack>
                </Radio.Group>
              )}
            </>
          )}

          <Select
            label="Fahrzeug"
            data={cars.map((c) => ({ value: c.id, label: c.licensePlate }))}
            value={carId}
            onChange={setCarId}
          />

          <Group justify="flex-end">
            <CancelButton onClick={close}>Abbrechen</CancelButton>
            <PrimaryButton onClick={book} disabled={!slotId}>
              Buchen
            </PrimaryButton>
          </Group>
        </Stack>
      </Modal>
    </Stack>
  );
}
