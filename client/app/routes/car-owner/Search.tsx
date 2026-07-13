import { useState } from "react";
import {
  Badge,
  Card,
  Drawer,
  Group,
  Modal,
  SimpleGrid,
  Stack,
  Text,
  Textarea,
  TextInput,
  Title,
} from "@mantine/core";
import { useDisclosure } from "@mantine/hooks";
import { notifications } from "@mantine/notifications";
import { useAuthContext } from "~/context/AuthContext";
import { api } from "~/api/fetch";
import { EmptyState } from "~/components/EmptyState";
import {
  CancelButton,
  PrimaryButton,
  TableActionButton,
} from "~/components/Button";
import type {
  ServiceOutputDto,
  StorageOwnerSearchResDto,
} from "~/types/dtos";

export default function Search() {
  const { user } = useAuthContext();
  const id = user!.id;

  const [city, setCity] = useState("");
  const [results, setResults] = useState<StorageOwnerSearchResDto[]>([]);
  const [searched, setSearched] = useState(false);

  const [reqOpened, reqHandlers] = useDisclosure(false);
  const [target, setTarget] = useState<StorageOwnerSearchResDto | null>(null);
  const [description, setDescription] = useState("");

  const [portfolioOpened, portfolioHandlers] = useDisclosure(false);
  const [portfolioOwner, setPortfolioOwner] =
    useState<StorageOwnerSearchResDto | null>(null);
  const [portfolio, setPortfolio] = useState<ServiceOutputDto[]>([]);

  async function search() {
    const { value } = await api.get<StorageOwnerSearchResDto[]>(
      `/car-owners/search?city=${encodeURIComponent(city)}`,
    );
    setResults(value ?? []);
    setSearched(true);
  }

  function openRequest(so: StorageOwnerSearchResDto) {
    setTarget(so);
    setDescription("");
    reqHandlers.open();
  }

  function openPortfolio(so: StorageOwnerSearchResDto) {
    setPortfolioOwner(so);
    setPortfolio(so.services ?? []);
    portfolioHandlers.open();
  }

  async function submitRequest() {
    if (!target) return;
    const { error } = await api.post(`/car-owners/${id}/requests`, {
      storageOwnerId: target.id,
      description,
    });
    if (error) {
      notifications.show({ color: "red", message: error.message });
      return;
    }
    notifications.show({ color: "green", message: "Anfrage gesendet." });
    reqHandlers.close();
  }

  return (
    <Stack gap={"xl"} justify="stretch">
      <Group align="flex-end" justify="center">
        <TextInput
          placeholder="z.B. Berlin"
          value={city}
          onChange={(e) => setCity(e.currentTarget.value)}
          onKeyDown={(e) => e.key === "Enter" && search()}
          w={260}
        />
        <PrimaryButton onClick={search}>Lager suchen</PrimaryButton>
      </Group>

      {searched && results.length === 0 ? (
        <EmptyState
          title="Keine Lagerhalter gefunden"
          description="Für diese Stadt gibt es aktuell keine Treffer."
        />
      ) : (
        <SimpleGrid cols={{ base: 1, sm: 2 }}>
          {results.map((so) => (
            <Card key={so.id} withBorder padding="lg" h="100%">
              <Stack gap="xs" h="100%" justify="space-between">
                <Stack gap="xs">
                  <Group justify="space-between">
                    <Text fw={700}>{so.companyName}</Text>
                    <Badge variant="light">
                      {so.storage?.capacity ?? 0} Plätze
                    </Badge>
                  </Group>
                  <Text size="sm" c="dimmed">
                    {so.location.street} {so.location.houseNumber},{" "}
                    {so.location.postalCode} {so.location.city}
                  </Text>
                  <Text size="sm">Kosten: {so.costs} €</Text>
                  {so.storage?.conditions?.length > 0 && (
                    <Text size="sm">
                      Bedingungen: {so.storage.conditions.join(", ")}
                    </Text>
                  )}
                  {so.carManufacturers.length > 0 && (
                    <Group gap={4}>
                      {so.carManufacturers.map((m) => (
                        <Badge key={m} variant="dot" size="sm">
                          {m}
                        </Badge>
                      ))}
                    </Group>
                  )}
                </Stack>
                <Group justify="flex-end">
                  <TableActionButton onClick={() => openPortfolio(so)}>
                    Serviceportfolio
                  </TableActionButton>
                  <PrimaryButton
                    size="compact-sm"
                    onClick={() => openRequest(so)}
                  >
                    Anfrage stellen
                  </PrimaryButton>
                </Group>
              </Stack>
            </Card>
          ))}
        </SimpleGrid>
      )}

      <Modal
        opened={reqOpened}
        onClose={reqHandlers.close}
        title={`Anfrage an ${target?.companyName ?? ""}`}
      >
        <Stack>
          <Textarea
            label="Beschreibung"
            placeholder="Beschreibe dein Anliegen (Fahrzeug, Zeitraum, …)"
            minRows={4}
            autosize
            value={description}
            onChange={(e) => setDescription(e.currentTarget.value)}
          />
          <Group justify="flex-end">
            <CancelButton onClick={reqHandlers.close}>Abbrechen</CancelButton>
            <PrimaryButton onClick={submitRequest}>
              Anfrage senden
            </PrimaryButton>
          </Group>
        </Stack>
      </Modal>

      <Drawer
        opened={portfolioOpened}
        onClose={portfolioHandlers.close}
        position="right"
        title={`Serviceportfolio – ${portfolioOwner?.companyName ?? ""}`}
        size="lg"
      >
        <Stack>
          {portfolio.length === 0 ? (
            <Text c="dimmed" size="sm">
              Dieser Lagerhalter bietet noch keine Services an.
            </Text>
          ) : (
            portfolio.map((s) => (
              <Card key={s.id} withBorder padding="sm">
                <div>
                  <Group gap="xs">
                    <Text fw={600}>{s.title}</Text>
                    <Badge variant="light" size="sm">
                      {s.isOptional ? "Optional" : "Inklusive"}
                    </Badge>
                  </Group>
                  <Text size="sm" c="dimmed">
                    {s.description}
                  </Text>
                  <Text size="sm">{s.price} €</Text>
                </div>
              </Card>
            ))
          )}
        </Stack>
      </Drawer>
    </Stack>
  );
}
