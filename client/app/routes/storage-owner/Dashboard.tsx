import {
  Anchor,
  Card,
  Group,
  SimpleGrid,
  Stack,
  Text,
  Title,
} from "@mantine/core";
import { useEffect, useState } from "react";
import { Link } from "react-router";
import { api } from "~/api/fetch";
import { EmptyState } from "~/components/EmptyState";
import { ReadyToDriveBadge } from "~/components/StatusBadge";
import { useAuthContext } from "~/context/AuthContext";
import type {
  AppointmentOutputDto,
  CarOutputDto,
  OfferOutputDto,
  RequestOutputDto,
  ServiceOutputDto,
} from "~/types/dtos";

const PREVIEW_LIMIT = 5;

export default function Dashboard() {
  const { user } = useAuthContext();
  const id = user!.id;
  const companyName = user && "companyName" in user ? user.companyName : "";
  const [services, setServices] = useState<ServiceOutputDto[]>([]);
  const [storedCars, setStoredCars] = useState<CarOutputDto[]>([]);
  const [counts, setCounts] = useState({
    services: 0,
    requests: 0,
    offers: 0,
    storedCars: 0,
    appointments: 0,
  });

  useEffect(() => {
    (async () => {
      const [services, requests, offers, cars, appts] = await Promise.all([
        api.get<ServiceOutputDto[]>(`/storage-owners/${id}/services`),
        api.get<RequestOutputDto[]>(`/storage-owners/${id}/incoming-requests`),
        api.get<OfferOutputDto[]>(`/storage-owners/${id}/offers`),
        api.get<CarOutputDto[]>(`/storage-owners/${id}/stored-cars`),
        api.get<AppointmentOutputDto[]>(`/storage-owners/${id}/appointments`),
      ]);
      setServices(services.value ?? []);
      setStoredCars(cars.value ?? []);
      setCounts({
        services: services.value?.length ?? 0,
        storedCars: cars.value?.length ?? 0,
        requests: requests.value?.length ?? 0,
        offers: offers.value?.length ?? 0,
        appointments: appts.value?.length ?? 0,
      });
    })();
  }, [id]);

  const cards = [
    {
      label: "Lagerbestand",
      value: counts.storedCars,
      to: "/storage-owner/stored-cars",
    },
    {
      label: "Services",
      value: counts.services,
      to: "/storage-owner/services",
    },
    {
      label: "Anfragen",
      value: counts.requests,
      to: "/storage-owner/requests",
    },
    { label: "Angebote", value: counts.offers, to: "/storage-owner/offers" },
    {
      label: "Termine",
      value: counts.appointments,
      to: "/storage-owner/appointments",
    },
  ];

  return (
    <Stack gap="xl">
      <Title order={2}>{companyName || "Übersicht"}</Title>

      <SimpleGrid cols={{ base: 2, sm: 5 }}>
        {cards.map((t) => (
          <Card
            key={t.label}
            withBorder
            component={Link}
            to={t.to}
            padding="lg"
          >
            <Text size="2rem" fw={700}>
              {t.value}
            </Text>
            <Text c="dimmed" size="sm">
              {t.label}
            </Text>
          </Card>
        ))}
      </SimpleGrid>

      <SimpleGrid cols={{ base: 1, md: 2 }}>
        <Card withBorder padding="lg">
          <Group justify="space-between" mb="md">
            <Title order={4}>Eingelagerte Fahrzeuge</Title>
            <Anchor component={Link} to="/storage-owner/stored-cars" size="sm">
              Alle ansehen
            </Anchor>
          </Group>
          {storedCars.length === 0 ? (
            <EmptyState
              title="Keine eingelagerten Fahrzeuge"
              description="Sobald ein Einlagerungstermin gebucht wird, erscheint das Fahrzeug hier."
            />
          ) : (
            <Stack gap="sm">
              {storedCars.slice(0, PREVIEW_LIMIT).map((car) => (
                <Group key={car.id} justify="space-between" wrap="nowrap">
                  <Text fw={600}>{car.licensePlate}</Text>
                  <ReadyToDriveBadge ready={car.readyToDrive} />
                </Group>
              ))}
            </Stack>
          )}
        </Card>

        <Card withBorder padding="lg">
          <Group justify="space-between" mb="md">
            <Title order={4}>Aktuelle Services</Title>
            <Anchor component={Link} to="/storage-owner/services" size="sm">
              Alle ansehen
            </Anchor>
          </Group>
          {services.length === 0 ? (
            <EmptyState
              title="Noch keine Services"
              description="Lege Services an, die du anbietest."
            />
          ) : (
            <Stack gap="sm">
              {services.slice(0, PREVIEW_LIMIT).map((s) => (
                <Group key={s.id} justify="space-between" wrap="nowrap">
                  <Text fw={600} truncate>
                    {s.title}
                  </Text>
                  <Text c="dimmed" size="sm" style={{ whiteSpace: "nowrap" }}>
                    {s.price} €
                  </Text>
                </Group>
              ))}
            </Stack>
          )}
        </Card>
      </SimpleGrid>
    </Stack>
  );
}
