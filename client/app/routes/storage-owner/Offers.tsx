import { useEffect, useState } from "react";
import { Group, Stack, Table, Title } from "@mantine/core";
import { DeleteButton } from "~/components/Button";
import { modals } from "@mantine/modals";
import { notifications } from "@mantine/notifications";
import { useAuthContext } from "~/context/AuthContext";
import { api } from "~/api/fetch";
import { EmptyState } from "~/components/EmptyState";
import { OfferStatusBadge } from "~/components/StatusBadge";
import type { OfferOutputDto } from "~/types/dtos";

export default function Offers() {
  const { user } = useAuthContext();
  const id = user!.id;
  const [offers, setOffers] = useState<OfferOutputDto[]>([]);
  const [loading, setLoading] = useState(true);

  async function load() {
    setLoading(true);
    const { value } = await api.get<OfferOutputDto[]>(
      `/storage-owners/${id}/offers`,
    );
    setOffers(value ?? []);
    setLoading(false);
  }

  useEffect(() => {
    load();
  }, [id]);

  function confirmDelete(offer: OfferOutputDto) {
    modals.openConfirmModal({
      title: "Angebot zurückziehen",
      children: "Dieses Angebot wirklich zurückziehen?",
      labels: { confirm: "Zurückziehen", cancel: "Abbrechen" },
      confirmProps: { color: "red" },
      onConfirm: async () => {
        const { error } = await api.delete(
          `/storage-owners/${id}/offers/${offer.id}`,
        );
        if (error) {
          notifications.show({ color: "red", message: error.message });
          return;
        }
        notifications.show({
          color: "green",
          message: "Angebot zurückgezogen.",
        });
        load();
      },
    });
  }

  return (
    <Stack>
      <Title order={2}>Meine Angebote</Title>

      {!loading && offers.length === 0 ? (
        <EmptyState
          title="Keine Angebote"
          description="Erstelle Angebote auf eingehende Anfragen."
        />
      ) : (
        <Table.ScrollContainer minWidth={600}>
          <Table verticalSpacing="sm">
            <Table.Thead>
              <Table.Tr>
                <Table.Th>Beschreibung</Table.Th>
                <Table.Th>Kosten</Table.Th>
                <Table.Th>Status</Table.Th>
                <Table.Th />
              </Table.Tr>
            </Table.Thead>
            <Table.Tbody>
              {offers.map((o) => (
                <Table.Tr key={o.id}>
                  <Table.Td>{o.description}</Table.Td>
                  <Table.Td>{o.costs} €</Table.Td>
                  <Table.Td>
                    <OfferStatusBadge accepted={o.accepted} />
                  </Table.Td>
                  <Table.Td>
                    <Group justify="flex-end">
                      <DeleteButton onClick={() => confirmDelete(o)}>
                        Zurückziehen
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
