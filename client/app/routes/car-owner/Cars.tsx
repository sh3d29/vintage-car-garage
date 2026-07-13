import { useEffect, useState } from "react";
import {
  Card,
  Drawer,
  Group,
  Modal,
  Select,
  Stack,
  Table,
  Text,
  TextInput,
  Title,
} from "@mantine/core";
import { useDisclosure } from "@mantine/hooks";
import { useForm } from "@mantine/form";
import { modals } from "@mantine/modals";
import { notifications } from "@mantine/notifications";
import { useAuthContext } from "~/context/AuthContext";
import { api } from "~/api/fetch";
import { EmptyState } from "~/components/EmptyState";
import {
  BookedBadge,
  MaintenanceBadge,
  ReadyToDriveBadge,
  StoredBadge,
} from "~/components/StatusBadge";
import {
  CancelButton,
  ConfirmButton,
  DeleteButton,
  PrimaryButton,
  TableActionButton,
} from "~/components/Button";
import type {
  CarCategory,
  CarInputDto,
  CarOutputDto,
  ServiceOutputDto,
} from "~/types/dtos";

const CATEGORY_LABELS: Record<CarCategory, string> = {
  YOUNGTIMER: "Youngtimer",
  OLDTIMER: "Oldtimer",
};

const CATEGORY_OPTIONS = (
  Object.keys(CATEGORY_LABELS) as CarCategory[]
).map((value) => ({ value, label: CATEGORY_LABELS[value] }));

export default function Cars() {
  const { user } = useAuthContext();
  const id = user!.id;
  const [cars, setCars] = useState<CarOutputDto[]>([]);
  const [loading, setLoading] = useState(true);
  const [editing, setEditing] = useState<CarOutputDto | null>(null);
  const [opened, { open, close }] = useDisclosure(false);

  const [svcOpened, svcHandlers] = useDisclosure(false);
  const [activeCar, setActiveCar] = useState<CarOutputDto | null>(null);
  const [services, setServices] = useState<ServiceOutputDto[]>([]);

  const form = useForm<CarInputDto>({
    initialValues: { licensePlate: "", category: null, manufacturer: "" },
    validate: {
      licensePlate: (v) => (v.trim() ? null : "Kennzeichen erforderlich"),
      manufacturer: (v) => (v.trim() ? null : "Hersteller erforderlich"),
      category: (v) => (v ? null : "Fahrzeugkategorie erforderlich"),
    },
  });

  async function load() {
    setLoading(true);
    const { value } = await api.get<CarOutputDto[]>(`/car-owners/${id}/cars`);
    const next = value ?? [];
    setCars(next);
    setLoading(false);
    return next;
  }

  useEffect(() => {
    load();
  }, [id]);

  function openCreate() {
    setEditing(null);
    form.setValues({ licensePlate: "", category: null, manufacturer: "" });
    open();
  }

  function openEdit(car: CarOutputDto) {
    setEditing(car);
    form.setValues({
      licensePlate: car.licensePlate,
      category: car.category,
      manufacturer: car.manufacturer ?? "",
    });
    open();
  }

  async function submit(values: CarInputDto) {
    const res = editing
      ? await api.patch<CarOutputDto>(
          `/car-owners/${id}/cars/${editing.id}`,
          values,
        )
      : await api.post<CarOutputDto>(`/car-owners/${id}/cars`, values);
    if (res.error) {
      notifications.show({ color: "red", message: res.error.message });
      return;
    }
    notifications.show({
      color: "green",
      message: editing ? "Fahrzeug aktualisiert." : "Fahrzeug registriert.",
    });
    close();
    load();
  }

  function confirmDelete(car: CarOutputDto) {
    modals.openConfirmModal({
      title: "Fahrzeug löschen",
      children: `Fahrzeug ${car.licensePlate} wirklich löschen?`,
      labels: { confirm: "Löschen", cancel: "Abbrechen" },
      confirmProps: { color: "red" },
      onConfirm: async () => {
        const { error } = await api.delete(`/car-owners/${id}/cars/${car.id}`);
        if (error) {
          notifications.show({ color: "red", message: error.message });
          return;
        }
        notifications.show({ color: "green", message: "Fahrzeug gelöscht." });
        load();
      },
    });
  }

  async function openServices(car: CarOutputDto) {
    if (!car.currentStorageOwnerId) {
      notifications.show({
        color: "red",
        message: "Fahrzeug ist nicht eingelagert.",
      });
      return;
    }
    const { value, error } = await api.get<ServiceOutputDto[]>(
      `/car-owners/${id}/storage-owners/${car.currentStorageOwnerId}/optional-services`,
    );
    if (error) {
      notifications.show({ color: "red", message: error.message });
      return;
    }
    setActiveCar(car);
    setServices(value ?? []);
    svcHandlers.open();
  }

  async function bookService(serviceId: string) {
    if (!activeCar?.currentStorageOwnerId) return;
    const { error } = await api.post(
      `/car-owners/${id}/cars/${activeCar.id}/booked-services/${serviceId}`,
    );
    if (error) {
      notifications.show({ color: "red", message: error.message });
      return;
    }
    notifications.show({ color: "green", message: "Service gebucht." });
    const next = await load();
    setActiveCar(next.find((c) => c.id === activeCar.id) ?? null);
  }

  async function removeService(car: CarOutputDto, serviceId: string) {
    const { error } = await api.delete(
      `/car-owners/${id}/cars/${car.id}/booked-services/${serviceId}`,
    );
    if (error) {
      notifications.show({ color: "red", message: error.message });
      return;
    }
    notifications.show({ color: "green", message: "Service entfernt." });
    const next = await load();
    setActiveCar((prev) =>
      prev ? (next.find((c) => c.id === prev.id) ?? null) : prev,
    );
  }

  return (
    <Stack>
      <Group justify="space-between">
        <Title order={2}>Meine Fahrzeuge</Title>
        <PrimaryButton onClick={openCreate}>
          Fahrzeug registrieren
        </PrimaryButton>
      </Group>

      {!loading && cars.length === 0 ? (
        <EmptyState
          title="Noch keine Fahrzeuge"
          description="Registriere dein erstes Fahrzeug, um Stellplätze und Termine buchen zu können."
          action={
            <PrimaryButton onClick={openCreate}>
              Fahrzeug registrieren
            </PrimaryButton>
          }
        />
      ) : (
        <Table.ScrollContainer minWidth={640}>
          <Table verticalSpacing="sm">
            <Table.Thead>
              <Table.Tr>
                <Table.Th>Kennzeichen</Table.Th>
                <Table.Th>Status</Table.Th>
                <Table.Th>Eingelagert</Table.Th>
                <Table.Th>Optionale Lagerservices</Table.Th>
                <Table.Th />
              </Table.Tr>
            </Table.Thead>
            <Table.Tbody>
              {cars.map((car) => (
                <Table.Tr key={car.id}>
                  <Table.Td>
                    <Text fw={600}>{car.licensePlate}</Text>
                    {(car.manufacturer || car.category) && (
                      <Text size="xs" c="dimmed">
                        {[
                          car.manufacturer,
                          car.category && CATEGORY_LABELS[car.category],
                        ]
                          .filter(Boolean)
                          .join(" · ")}
                      </Text>
                    )}
                  </Table.Td>
                  <Table.Td>
                    <Group gap="xs">
                      <ReadyToDriveBadge ready={car.readyToDrive} />
                      <MaintenanceBadge maintained={car.isMaintained} />
                    </Group>
                  </Table.Td>
                  <Table.Td>
                    <StoredBadge stored={!!car.currentStorageOwnerId} />
                  </Table.Td>
                  <Table.Td>
                    <Group gap="xs">
                      <TableActionButton onClick={() => openServices(car)}>
                        Serviceportfolio
                      </TableActionButton>
                    </Group>
                  </Table.Td>
                  <Table.Td>
                    <Group gap="xs" justify="flex-end">
                      <TableActionButton onClick={() => openEdit(car)}>
                        Bearbeiten
                      </TableActionButton>
                      <DeleteButton onClick={() => confirmDelete(car)}>
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

      <Modal
        opened={opened}
        onClose={close}
        title={editing ? "Fahrzeug bearbeiten" : "Fahrzeug registrieren"}
      >
        <form onSubmit={form.onSubmit(submit)}>
          <Stack>
            <TextInput
              label="Kennzeichen"
              placeholder="z.B. B-VC 1955"
              required
              {...form.getInputProps("licensePlate")}
            />
            <TextInput
              label="Hersteller"
              placeholder="z.B. Mercedes-Benz"
              required
              {...form.getInputProps("manufacturer")}
            />
            <Select
              label="Fahrzeugkategorie"
              placeholder="Kategorie wählen"
              data={CATEGORY_OPTIONS}
              required
              {...form.getInputProps("category")}
            />
            <Group justify="flex-end">
              <CancelButton onClick={close}>Abbrechen</CancelButton>
              <PrimaryButton type="submit">Speichern</PrimaryButton>
            </Group>
          </Stack>
        </form>
      </Modal>

      <Drawer
        opened={svcOpened}
        onClose={svcHandlers.close}
        position="right"
        title={`Optionale Services – ${activeCar?.licensePlate ?? ""}`}
        size={"lg"}
      >
        <Stack>
          {services.length === 0 ? (
            <Text c="dimmed" size="sm">
              Keine optionalen Services verfügbar.
            </Text>
          ) : (
            services.map((s) => {
              const booked =
                activeCar?.bookedServiceIds.includes(s.id) ?? false;
              return (
                <Card key={s.id} withBorder padding="sm">
                  <Group justify="space-between" wrap="nowrap">
                    <div>
                      <Group gap="xs">
                        <Text fw={600}>{s.title}</Text>
                        {booked && <BookedBadge />}
                      </Group>
                      <Text size="sm" c="dimmed">
                        {s.description}
                      </Text>
                      <Text size="sm">{s.price} €</Text>
                    </div>
                    {booked ? (
                      <DeleteButton
                        onClick={() =>
                          activeCar && removeService(activeCar, s.id)
                        }
                      >
                        Entfernen
                      </DeleteButton>
                    ) : (
                      <ConfirmButton onClick={() => bookService(s.id)}>
                        Buchen
                      </ConfirmButton>
                    )}
                  </Group>
                </Card>
              );
            })
          )}
        </Stack>
      </Drawer>
    </Stack>
  );
}
