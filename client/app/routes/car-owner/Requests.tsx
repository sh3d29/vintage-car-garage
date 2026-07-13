import { useEffect, useState } from "react";
import { Group, Stack, Table, Title } from "@mantine/core";
import { DeleteButton } from "~/components/Button";
import { modals } from "@mantine/modals";
import { notifications } from "@mantine/notifications";
import { useAuthContext } from "~/context/AuthContext";
import { api } from "~/api/fetch";
import { EmptyState } from "~/components/EmptyState";
import { RequestTypeBadge } from "~/components/StatusBadge";
import type { RequestOutputDto } from "~/types/dtos";

export default function Requests() {
  const { user } = useAuthContext();
  const id = user!.id;
  const [requests, setRequests] = useState<RequestOutputDto[]>([]);
  const [loading, setLoading] = useState(true);

  async function load() {
    setLoading(true);
    const { value } = await api.get<RequestOutputDto[]>(
      `/car-owners/${id}/requests`,
    );
    setRequests(value ?? []);
    setLoading(false);
  }

  useEffect(() => {
    load();
  }, [id]);

  function confirmDelete(req: RequestOutputDto) {
    modals.openConfirmModal({
      title: "Anfrage zurückziehen",
      children: "Diese Stellplatzanfrage wirklich löschen?",
      labels: { confirm: "Löschen", cancel: "Abbrechen" },
      confirmProps: { color: "red" },
      onConfirm: async () => {
        const { error } = await api.delete(
          `/car-owners/${id}/requests/${req.id}`,
        );
        if (error) {
          notifications.show({ color: "red", message: error.message });
          return;
        }
        notifications.show({ color: "green", message: "Anfrage gelöscht." });
        load();
      },
    });
  }

  return (
    <Stack>
      <Title order={2}>Meine Anfragen</Title>

      {!loading && requests.length === 0 ? (
        <EmptyState
          title="Keine Anfragen"
          description="Suche einen Stellplatz und stelle eine Anfrage an einen Lagerhalter."
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
                      <DeleteButton onClick={() => confirmDelete(r)}>
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
    </Stack>
  );
}
