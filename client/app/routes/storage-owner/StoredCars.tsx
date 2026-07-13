import { useEffect, useState } from "react";
import { Group, Modal, Stack, Switch, Table, Title } from "@mantine/core";
import { useDisclosure } from "@mantine/hooks";
import { notifications } from "@mantine/notifications";
import { useAuthContext } from "~/context/AuthContext";
import { api } from "~/api/fetch";
import { EmptyState } from "~/components/EmptyState";
import { MaintenanceBadge, ReadyToDriveBadge } from "~/components/StatusBadge";
import {
  CancelButton,
  PrimaryButton,
  TableActionButton,
} from "~/components/Button";
import type { CarOutputDto, CarStatusDto } from "~/types/dtos";

export default function StoredCars() {
  const { user } = useAuthContext();
  const id = user!.id;
  const [cars, setCars] = useState<CarOutputDto[]>([]);
  const [loading, setLoading] = useState(true);
  const [editing, setEditing] = useState<CarOutputDto | null>(null);
  const [status, setStatus] = useState<CarStatusDto>({
    readyToDrive: true,
    isMaintained: false,
  });
  const [opened, { open, close }] = useDisclosure(false);

  async function load() {
    setLoading(true);
    const { value } = await api.get<CarOutputDto[]>(
      `/storage-owners/${id}/stored-cars`,
    );
    setCars(value ?? []);
    setLoading(false);
  }

  useEffect(() => {
    load();
  }, [id]);

  function openStatus(car: CarOutputDto) {
    setEditing(car);
    setStatus({
      readyToDrive: car.readyToDrive,
      isMaintained: car.isMaintained,
    });
    open();
  }

  async function save() {
    if (!editing) return;
    const { error } = await api.patch(
      `/storage-owners/${id}/stored-cars/${editing.id}/status`,
      status,
    );
    if (error) {
      notifications.show({ color: "red", message: error.message });
      return;
    }
    notifications.show({ color: "green", message: "Status aktualisiert." });
    close();
    load();
  }

  return (
    <Stack>
      <Title order={2}>Eingelagerte Fahrzeuge</Title>

      {!loading && cars.length === 0 ? (
        <EmptyState
          title="Keine eingelagerten Fahrzeuge"
          description="Sobald ein Einlagerungstermin gebucht wird, erscheint das Fahrzeug hier."
        />
      ) : (
        <Table.ScrollContainer minWidth={560}>
          <Table verticalSpacing="sm">
            <Table.Thead>
              <Table.Tr>
                <Table.Th>Kennzeichen</Table.Th>
                <Table.Th>Fahrbereitschaft</Table.Th>
                <Table.Th>Wartung</Table.Th>
                <Table.Th />
              </Table.Tr>
            </Table.Thead>
            <Table.Tbody>
              {cars.map((car) => (
                <Table.Tr key={car.id}>
                  <Table.Td fw={600}>{car.licensePlate}</Table.Td>
                  <Table.Td>
                    <ReadyToDriveBadge ready={car.readyToDrive} />
                  </Table.Td>
                  <Table.Td>
                    <MaintenanceBadge maintained={car.isMaintained} />
                  </Table.Td>
                  <Table.Td>
                    <Group justify="flex-end">
                      <TableActionButton onClick={() => openStatus(car)}>
                        Status ändern
                      </TableActionButton>
                    </Group>
                  </Table.Td>
                </Table.Tr>
              ))}
            </Table.Tbody>
          </Table>
        </Table.ScrollContainer>
      )}

      <Modal
        opened={opened}
        onClose={close}
        title={`Status – ${editing?.licensePlate ?? ""}`}
      >
        <Stack>
          <Switch
            label="Fahrbereit"
            checked={status.readyToDrive}
            onChange={(e) => {
              const checked = e.currentTarget.checked;
              setStatus((s) => ({ ...s, readyToDrive: checked }));
            }}
          />
          <Switch
            label="Gewartet"
            checked={status.isMaintained}
            onChange={(e) => {
              const checked = e.currentTarget.checked;
              setStatus((s) => ({ ...s, isMaintained: checked }));
            }}
          />
          <Group justify="flex-end">
            <CancelButton onClick={close}>Abbrechen</CancelButton>
            <PrimaryButton onClick={save}>Speichern</PrimaryButton>
          </Group>
        </Stack>
      </Modal>
    </Stack>
  );
}
