import { useEffect, useState } from "react";
import { Link } from "react-router";
import { Card, Group, SimpleGrid, Stack, Text, Title } from "@mantine/core";
import { useAuthContext } from "~/context/AuthContext";
import { api } from "~/api/fetch";
import type {
  AppointmentOutputDto,
  CarOutputDto,
  OfferOutputDto,
  RequestOutputDto,
} from "~/types/dtos";
import Search from "./Search";

export interface CardItem {
  label: string;
  value: number;
  to: string;
}

export default function Dashboard() {
  const { user } = useAuthContext();
  const id = user!.id;
  const firstName = user && "firstName" in user ? user.firstName : "";
  const [counts, setCounts] = useState({
    cars: 0,
    requests: 0,
    offers: 0,
    appointments: 0,
  });

  useEffect(() => {
    (async () => {
      const [cars, requests, offers, appointments] = await Promise.all([
        api.get<CarOutputDto[]>(`/car-owners/${id}/cars`),
        api.get<RequestOutputDto[]>(`/car-owners/${id}/requests`),
        api.get<OfferOutputDto[]>(`/car-owners/${id}/offers`),
        api.get<AppointmentOutputDto[]>(`/car-owners/${id}/appointments`),
      ]);
      setCounts({
        cars: cars.value?.length ?? 0,
        requests: requests.value?.length ?? 0,
        offers: offers.value?.length ?? 0,
        appointments: appointments.value?.length ?? 0,
      });
    })();
  }, [id]);

  const cards: CardItem[] = [
    { label: "Fahrzeuge", value: counts.cars, to: "/car-owner/cars" },
    { label: "Anfragen", value: counts.requests, to: "/car-owner/requests" },
    { label: "Angebote", value: counts.offers, to: "/car-owner/offers" },
    {
      label: "Termine",
      value: counts.appointments,
      to: "/car-owner/appointments",
    },
  ];

  return (
    <Stack gap={"xl"}>
      <Title order={2}>Hallo, {firstName ?? ""}!</Title>
      <SimpleGrid cols={{ base: 2, sm: 4 }}>
        {cards.map((card) => (
          <Card
            key={card.label}
            withBorder
            component={Link}
            to={card.to}
            padding="lg"
          >
            <Text size="2rem" fw={700}>
              {card.value}
            </Text>
            <Text c="dimmed" size="sm">
              {card.label}
            </Text>
          </Card>
        ))}
      </SimpleGrid>
      <Search />
    </Stack>
  );
}
