import { useEffect, useState } from "react";
import {
  Group,
  Modal,
  NumberInput,
  Stack,
  Table,
  Textarea,
  Title,
} from "@mantine/core";
import { useDisclosure } from "@mantine/hooks";
import { useForm } from "@mantine/form";
import { notifications } from "@mantine/notifications";
import { useAuthContext } from "~/context/AuthContext";
import { api } from "~/api/fetch";
import { EmptyState } from "~/components/EmptyState";
import { RequestTypeBadge } from "~/components/StatusBadge";
import {
  CancelButton,
  PrimaryButton,
  TableActionButton,
} from "~/components/Button";
import type { OfferInputDto, RequestOutputDto } from "~/types/dtos";

export default function Requests() {
  const { user } = useAuthContext();
  const id = user!.id;
  const [requests, setRequests] = useState<RequestOutputDto[]>([]);
  const [loading, setLoading] = useState(true);
  const [target, setTarget] = useState<RequestOutputDto | null>(null);
  const [opened, { open, close }] = useDisclosure(false);

  const form = useForm<OfferInputDto>({
    initialValues: { description: "", costs: 0 },
    validate: {
      description: (v) => (v.trim() ? null : "Beschreibung erforderlich"),
    },
  });

  async function load() {
    setLoading(true);
    const { value } = await api.get<RequestOutputDto[]>(
      `/storage-owners/${id}/incoming-requests`,
    );
    setRequests(value ?? []);
    setLoading(false);
  }

  useEffect(() => {
    load();
  }, [id]);

  function openOffer(req: RequestOutputDto) {
    setTarget(req);
    form.setValues({ description: "", costs: 0 });
    open();
  }

  async function submit(values: OfferInputDto) {
    if (!target) return;
    const { error } = await api.post(
      `/storage-owners/${id}/requests/${target.id}/offers`,
      values,
    );
    if (error) {
      notifications.show({ color: "red", message: error.message });
      return;
    }
    notifications.show({ color: "green", message: "Angebot gesendet." });
    close();
    load();
  }

  return (
    <Stack>
      <Title order={2}>Eingehende Anfragen</Title>

      {!loading && requests.length === 0 ? (
        <EmptyState
          title="Keine Anfragen"
          description="Sobald ein Einlagerer eine Stellplatzanfrage stellt, erscheint sie hier."
        />
      ) : (
        <Table.ScrollContainer minWidth={520}>
          <Table verticalSpacing="sm">
            <Table.Thead>
              <Table.Tr>
                <Table.Th>Beschreibung</Table.Th>
                <Table.Th>Typ</Table.Th>
                <Table.Th />
              </Table.Tr>
            </Table.Thead>
            <Table.Tbody>
              {requests.map((r) => (
                <Table.Tr key={r.id}>
                  <Table.Td>{r.description}</Table.Td>
                  <Table.Td>
                    <RequestTypeBadge type={r.type} />
                  </Table.Td>
                  <Table.Td>
                    <Group justify="flex-end">
                      <TableActionButton onClick={() => openOffer(r)}>
                        Angebot erstellen
                      </TableActionButton>
                    </Group>
                  </Table.Td>
                </Table.Tr>
              ))}
            </Table.Tbody>
          </Table>
        </Table.ScrollContainer>
      )}

      <Modal opened={opened} onClose={close} title="Angebot erstellen">
        <form onSubmit={form.onSubmit(submit)}>
          <Stack>
            <Textarea
              label="Beschreibung"
              placeholder="Konditionen des Angebots"
              autosize
              minRows={3}
              {...form.getInputProps("description")}
            />
            <NumberInput
              label="Kosten (€)"
              min={0}
              decimalScale={2}
              {...form.getInputProps("costs")}
            />
            <Group justify="flex-end">
              <CancelButton onClick={close}>Abbrechen</CancelButton>
              <PrimaryButton type="submit">Angebot senden</PrimaryButton>
            </Group>
          </Stack>
        </form>
      </Modal>
    </Stack>
  );
}
