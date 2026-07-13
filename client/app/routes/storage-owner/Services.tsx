import { useEffect, useState } from "react";
import {
  Group,
  Modal,
  NumberInput,
  Stack,
  Switch,
  Table,
  Textarea,
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
import { ServiceKindBadge } from "~/components/StatusBadge";
import {
  CancelButton,
  DeleteButton,
  PrimaryButton,
  TableActionButton,
} from "~/components/Button";
import type { ServiceInputDto, ServiceOutputDto } from "~/types/dtos";

export default function Services() {
  const { user } = useAuthContext();
  const id = user!.id;
  const [services, setServices] = useState<ServiceOutputDto[]>([]);
  const [loading, setLoading] = useState(true);
  const [editing, setEditing] = useState<ServiceOutputDto | null>(null);
  const [opened, { open, close }] = useDisclosure(false);

  const form = useForm<ServiceInputDto>({
    initialValues: { title: "", description: "", price: 0, isOptional: false },
    validate: { title: (v) => (v.trim() ? null : "Titel erforderlich") },
  });

  async function load() {
    setLoading(true);
    const { value } = await api.get<ServiceOutputDto[]>(
      `/storage-owners/${id}/services`,
    );
    setServices(value ?? []);
    setLoading(false);
  }

  useEffect(() => {
    load();
  }, [id]);

  function openCreate() {
    setEditing(null);
    form.setValues({ title: "", description: "", price: 0, isOptional: false });
    open();
  }

  function openEdit(s: ServiceOutputDto) {
    setEditing(s);
    form.setValues({
      title: s.title,
      description: s.description,
      price: s.price,
      isOptional: s.isOptional,
    });
    open();
  }

  async function submit(values: ServiceInputDto) {
    const res = editing
      ? await api.patch<ServiceOutputDto>(
          `/storage-owners/${id}/services/${editing.id}`,
          values,
        )
      : await api.post<ServiceOutputDto>(
          `/storage-owners/${id}/services`,
          values,
        );
    if (res.error) {
      notifications.show({ color: "red", message: res.error.message });
      return;
    }
    notifications.show({
      color: "green",
      message: editing ? "Service aktualisiert." : "Service angelegt.",
    });
    close();
    load();
  }

  function confirmDelete(s: ServiceOutputDto) {
    modals.openConfirmModal({
      title: "Service löschen",
      children: `Service „${s.title}" wirklich löschen?`,
      labels: { confirm: "Löschen", cancel: "Abbrechen" },
      confirmProps: { color: "red" },
      onConfirm: async () => {
        const { error } = await api.delete(
          `/storage-owners/${id}/services/${s.id}`,
        );
        if (error) {
          notifications.show({ color: "red", message: error.message });
          return;
        }
        notifications.show({ color: "green", message: "Service gelöscht." });
        load();
      },
    });
  }

  return (
    <Stack>
      <Group justify="space-between">
        <Title order={2}>Serviceportfolio</Title>
        <PrimaryButton onClick={openCreate}>Service anlegen</PrimaryButton>
      </Group>

      {!loading && services.length === 0 ? (
        <EmptyState
          title="Noch keine Services"
          description="Lege Services an, die du anbietest. Optionale Services können Einlagerer zu ihren Fahrzeugen buchen."
          action={
            <PrimaryButton onClick={openCreate}>Service anlegen</PrimaryButton>
          }
        />
      ) : (
        <Table.ScrollContainer minWidth={640}>
          <Table verticalSpacing="sm">
            <Table.Thead>
              <Table.Tr>
                <Table.Th>Titel</Table.Th>
                <Table.Th>Beschreibung</Table.Th>
                <Table.Th>Preis</Table.Th>
                <Table.Th />
              </Table.Tr>
            </Table.Thead>
            <Table.Tbody>
              {services.map((s) => (
                <Table.Tr key={s.id}>
                  <Table.Td fw={600}>{s.title}</Table.Td>
                  <Table.Td>{s.description}</Table.Td>
                  <Table.Td>{s.price} €</Table.Td>
                  <Table.Td>
                    <ServiceKindBadge optional={s.isOptional} />
                  </Table.Td>
                  <Table.Td>
                    <Group gap="xs" justify="flex-end">
                      <TableActionButton onClick={() => openEdit(s)}>
                        Bearbeiten
                      </TableActionButton>
                      <DeleteButton onClick={() => confirmDelete(s)}>
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
        title={editing ? "Service bearbeiten" : "Service anlegen"}
      >
        <form onSubmit={form.onSubmit(submit)}>
          <Stack>
            <TextInput
              label="Titel"
              required
              {...form.getInputProps("title")}
            />
            <Textarea
              label="Beschreibung"
              autosize
              minRows={2}
              {...form.getInputProps("description")}
            />
            <NumberInput
              label="Preis (€)"
              min={0}
              decimalScale={2}
              {...form.getInputProps("price")}
            />
            <Switch
              label="Optionaler Service (buchbar durch Einlagerer)"
              {...form.getInputProps("isOptional", { type: "checkbox" })}
            />
            <Group justify="flex-end">
              <CancelButton onClick={close}>Abbrechen</CancelButton>
              <PrimaryButton type="submit">Speichern</PrimaryButton>
            </Group>
          </Stack>
        </form>
      </Modal>
    </Stack>
  );
}
