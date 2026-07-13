import { useState } from "react";
import { Link, useNavigate } from "react-router";
import {
  Anchor,
  Container,
  NumberInput,
  Paper,
  PasswordInput,
  SegmentedControl,
  SimpleGrid,
  Stack,
  TagsInput,
  Text,
  TextInput,
  Title,
} from "@mantine/core";
import { TimeInput } from "@mantine/dates";
import { useForm } from "@mantine/form";
import { notifications } from "@mantine/notifications";
import { api } from "~/api/fetch";
import { PrimaryButton } from "~/components/Button";
import type {
  CarOwnerInputDto,
  StorageOwnerInputDto,
  UserRole,
} from "~/types/dtos";
import PublicRoute from "~/components/PublicRoute";

export default function Register() {
  const [role, setRole] = useState<UserRole>("car-owner");

  return (
    <PublicRoute>
      <Container size={role === "car-owner" ? 460 : 640} my={40}>
        <Title ta="center" fw={900}>
          Account erstellen
        </Title>
        <Text c="dimmed" size="sm" ta="center" mt={5}>
          Schon registriert?{" "}
          <Anchor size="sm" component={Link} to="/">
            Zum Login
          </Anchor>
        </Text>

        <Paper withBorder shadow="md" p={30} mt={30} radius="md">
          <SegmentedControl
            fullWidth
            mb="lg"
            value={role}
            onChange={(v) => setRole(v as UserRole)}
            data={[
              { label: "Einlagerer", value: "car-owner" },
              { label: "Lagerhalter", value: "storage-owner" },
            ]}
          />

          {role === "car-owner" ? <CarOwnerForm /> : <StorageOwnerForm />}
        </Paper>
      </Container>
    </PublicRoute>
  );
}

type CarOwnerFormValues = CarOwnerInputDto & { confirmPassword: string };

function CarOwnerForm() {
  const navigate = useNavigate();
  const [submitting, setSubmitting] = useState(false);

  const form = useForm<CarOwnerFormValues>({
    initialValues: {
      email: "",
      password: "",
      confirmPassword: "",
      firstName: "",
      lastName: "",
    },
    validate: {
      email: (v) => (/^\S+@\S+$/.test(v) ? null : "Ungültige E-Mail"),
      password: (v) => ((v ?? "").length >= 6 ? null : "Mind. 6 Zeichen"),
      confirmPassword: (v, values) =>
        v !== values.password ? "Passwörter stimmen nicht überein" : null,
      firstName: (v) => (v ? null : "Vorname erforderlich"),
      lastName: (v) => (v ? null : "Nachname erforderlich"),
    },
  });

  async function submit(values: CarOwnerFormValues) {
    setSubmitting(true);
    const payload: CarOwnerInputDto = {
      email: values.email,
      password: values.password,
      firstName: values.firstName,
      lastName: values.lastName,
    };
    const { error } = await api.post("/car-owners/sign-up", payload);
    setSubmitting(false);
    if (error) {
      notifications.show({ color: "red", message: error.message });
      return;
    }
    notifications.show({
      color: "green",
      message: "Registrierung erfolgreich. Bitte anmelden.",
    });
    navigate("/");
  }

  return (
    <form onSubmit={form.onSubmit(submit)}>
      <Stack>
        <SimpleGrid cols={{ base: 1, sm: 2 }}>
          <TextInput
            label="Vorname"
            required
            {...form.getInputProps("firstName")}
          />
          <TextInput
            label="Nachname"
            required
            {...form.getInputProps("lastName")}
          />
        </SimpleGrid>
        <TextInput label="E-Mail" required {...form.getInputProps("email")} />
        <PasswordInput
          label="Passwort"
          required
          {...form.getInputProps("password")}
        />
        <PasswordInput
          label="Passwort wiederholen"
          required
          {...form.getInputProps("confirmPassword")}
        />
        <PrimaryButton fullWidth type="submit" mt="sm" loading={submitting}>
          Registrieren
        </PrimaryButton>
      </Stack>
    </form>
  );
}

type StorageOwnerFormValues = StorageOwnerInputDto & {
  confirmPassword: string;
};

function StorageOwnerForm() {
  const navigate = useNavigate();
  const [submitting, setSubmitting] = useState(false);

  const form = useForm<StorageOwnerFormValues>({
    initialValues: {
      email: "",
      password: "",
      confirmPassword: "",
      companyName: "",
      costs: 0,
      serviceStartTime: "",
      serviceEndTime: "",
      location: { street: "", houseNumber: "", postalCode: "", city: "" },
      storage: { conditions: [], capacity: 0 },
      carManufacturers: [],
    },
    validate: {
      email: (v) => (/^\S+@\S+$/.test(v) ? null : "Ungültige E-Mail"),
      password: (v) => ((v ?? "").length >= 6 ? null : "Mind. 6 Zeichen"),
      confirmPassword: (v, values) =>
        v !== values.password ? "Passwörter stimmen nicht überein" : null,
      companyName: (v) => (v ? null : "Firmenname erforderlich"),
      costs: (v) =>
        typeof v === "number" && v >= 0 ? null : "Kosten erforderlich",
      serviceStartTime: (v) => (v ? null : "Startzeit erforderlich"),
      serviceEndTime: (v, values) => {
        if (!v) return "Endzeit erforderlich";
        if (values.serviceStartTime && v <= values.serviceStartTime)
          return "Endzeit muss nach Startzeit liegen";
        return null;
      },
      location: {
        street: (v) => (v ? null : "Erforderlich"),
        houseNumber: (v) => (v ? null : "Erforderlich"),
        postalCode: (v) => (v ? null : "Erforderlich"),
        city: (v) => (v ? null : "Erforderlich"),
      },
      storage: {
        capacity: (v) =>
          typeof v === "number" && v > 0 ? null : "Kapazität erforderlich",
        conditions: (v) =>
          v && v.length > 0 ? null : "Mind. eine Lagerbedingung",
      },
    },
  });

  async function submit(values: StorageOwnerFormValues) {
    setSubmitting(true);
    const { confirmPassword: _confirmPassword, ...rest } = values;
    const payload: StorageOwnerInputDto = {
      ...rest,
      serviceStartTime: `${values.serviceStartTime}:00`,
      serviceEndTime: `${values.serviceEndTime}:00`,
    };
    const { error } = await api.post("/storage-owners/sign-up", payload);
    setSubmitting(false);
    if (error) {
      notifications.show({ color: "red", message: error.message });
      return;
    }
    notifications.show({
      color: "green",
      message: "Registrierung erfolgreich. Bitte anmelden.",
    });
    navigate("/");
  }

  return (
    <form onSubmit={form.onSubmit(submit)}>
      <Stack>
        <TextInput
          label="Firmenname"
          required
          {...form.getInputProps("companyName")}
        />
        <TextInput label="E-Mail" required {...form.getInputProps("email")} />
        <SimpleGrid cols={{ base: 1, sm: 2 }}>
          <PasswordInput
            label="Passwort"
            required
            {...form.getInputProps("password")}
          />
          <PasswordInput
            label="Passwort wiederholen"
            required
            {...form.getInputProps("confirmPassword")}
          />
        </SimpleGrid>

        <Text fw={600} size="sm" mt="xs">
          Standort
        </Text>
        <SimpleGrid cols={{ base: 1, sm: 2 }}>
          <TextInput
            label="Straße"
            required
            {...form.getInputProps("location.street")}
          />
          <TextInput
            label="Hausnummer"
            required
            {...form.getInputProps("location.houseNumber")}
          />
          <TextInput
            label="PLZ"
            required
            {...form.getInputProps("location.postalCode")}
          />
          <TextInput
            label="Stadt"
            required
            {...form.getInputProps("location.city")}
          />
        </SimpleGrid>

        <Text fw={600} size="sm" mt="xs">
          Lager & Konditionen
        </Text>
        <SimpleGrid cols={{ base: 1, sm: 2 }}>
          <NumberInput
            label="Stellplatz-Kapazität"
            withAsterisk
            min={0}
            {...form.getInputProps("storage.capacity")}
          />
          <NumberInput
            label="Kosten (€)"
            withAsterisk
            min={0}
            decimalScale={2}
            {...form.getInputProps("costs")}
          />
          <TimeInput
            label="Servicezeit von"
            withAsterisk
            {...form.getInputProps("serviceStartTime")}
          />
          <TimeInput
            label="Servicezeit bis"
            withAsterisk
            {...form.getInputProps("serviceEndTime")}
          />
        </SimpleGrid>
        <TagsInput
          label="Lagerbedingungen"
          withAsterisk
          placeholder="z.B. beheizt, trocken – Enter zum Hinzufügen"
          {...form.getInputProps("storage.conditions")}
        />
        <TagsInput
          label="Markenspezialisierung (optional)"
          placeholder="z.B. Porsche, Mercedes"
          {...form.getInputProps("carManufacturers")}
        />

        <PrimaryButton fullWidth type="submit" mt="sm" loading={submitting}>
          Registrieren
        </PrimaryButton>
      </Stack>
    </form>
  );
}
