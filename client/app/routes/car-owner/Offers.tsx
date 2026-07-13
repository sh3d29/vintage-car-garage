import { useEffect, useState } from "react";
import { Group, Stack, Table, Text, Title } from "@mantine/core";
import { ConfirmButton, DeleteButton } from "~/components/Button";
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
      `/car-owners/${id}/offers`,
    );
    setOffers(value ?? []);
    setLoading(false);
  }

  useEffect(() => {
    load();
  }, [id]);

  async function respond(offer: OfferOutputDto, accepted: boolean) {
    const { error } = await api.patch(`/car-owners/${id}/offers/${offer.id}`, {
      accepted,
    });
    if (error) {
      notifications.show({ color: "red", message: error.message });
      return;
    }
    notifications.show({
      color: "green",
      message: accepted ? "Angebot angenommen." : "Angebot abgelehnt.",
    });
    load();
  }

  return (
    <Stack>
      <Title order={2}>Erhaltene Angebote</Title>

      {!loading && offers.length === 0 ? (
        <EmptyState
          title="Keine Angebote"
          description="Sobald ein Lagerhalter auf deine Anfrage reagiert, erscheint sein Angebot hier."
        />
      ) : (
        <Table.ScrollContainer minWidth={640}>
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
                    {o.accepted === null ? (
                      <Group justify="flex-end" gap="xs">
                        <ConfirmButton onClick={() => respond(o, true)}>
                          Annehmen
                        </ConfirmButton>
                        <DeleteButton onClick={() => respond(o, false)}>
                          Ablehnen
                        </DeleteButton>
                      </Group>
                    ) : null}
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
