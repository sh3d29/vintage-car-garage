import { useEffect, useState } from "react";
import { useNavigate } from "react-router";
import {
  Group,
  NumberInput,
  PasswordInput,
  SimpleGrid,
  Stack,
  TagsInput,
  Text,
  TextInput,
} from "@mantine/core";
import { TimeInput } from "@mantine/dates";
import { useForm } from "@mantine/form";
import { notifications } from "@mantine/notifications";
import { useAuthContext } from "~/context/AuthContext";
import { api } from "~/api/fetch";
import { PrimaryButton } from "~/components/Button";
import type { StorageOwnerInputDto, StorageOwnerOutputDto } from "~/types/dtos";

export function StorageOwnerProfileForm({
  onSuccess,
}: {
  onSuccess: () => void;
}) {
  const { user, refresh, logout } = useAuthContext();
  const id = user!.id;
  const navigate = useNavigate();
  const [submitting, setSubmitting] = useState(false);

  const form = useForm({
    initialValues: {
      email: "",
      password: "",
      confirmPassword: "",
      companyName: "",
      costs: 0,
      serviceStartTime: "",
      serviceEndTime: "",
      location: { street: "", houseNumber: "", postalCode: "", city: "" },
      storage: { conditions: [] as string[], capacity: 0 },
      carManufacturers: [] as string[],
    },
    validate: {
      email: (v) => (/^\S+@\S+$/.test(v) ? null : "Ungültige E-Mail"),
      companyName: (v) => (v ? null : "Firmenname erforderlich"),
      confirmPassword: (v, values) =>
        values.password && v !== values.password
          ? "Passwörter stimmen nicht überein"
          : null,
      serviceEndTime: (v, values) =>
        v && values.serviceStartTime && v <= values.serviceStartTime
          ? "Endzeit muss nach Startzeit liegen"
          : null,
    },
  });

  useEffect(() => {
    (async () => {
      const { value } = await api.get<StorageOwnerOutputDto>(
        `/storage-owners/${id}`,
      );
      if (value) {
        form.setValues({
          email: value.email,
          password: "",
          confirmPassword: "",
          companyName: value.companyName,
          costs: value.costs,
          serviceStartTime: value.serviceStartTime
            ? value.serviceStartTime.slice(0, 5)
            : "",
          serviceEndTime: value.serviceEndTime
            ? value.serviceEndTime.slice(0, 5)
            : "",
          location: value.location,
          storage: value.storage,
          carManufacturers: value.carManufacturers ?? [],
        });
      }
    })();
  }, [id]);

  async function submit(values: typeof form.values) {
    setSubmitting(true);
    const passwordChanged = values.password.trim().length > 0;
    const payload: StorageOwnerInputDto = {
      email: values.email,
      companyName: values.companyName,
      costs: values.costs,
      serviceStartTime: values.serviceStartTime
        ? `${values.serviceStartTime}:00`
        : "08:00:00",
      serviceEndTime: values.serviceEndTime
        ? `${values.serviceEndTime}:00`
        : "18:00:00",
      location: values.location,
      storage: values.storage,
      carManufacturers: values.carManufacturers,
    };
    if (passwordChanged) payload.password = values.password;

    const { error } = await api.patch(`/storage-owners/${id}`, payload);
    setSubmitting(false);
    if (error) {
      notifications.show({ color: "red", message: error.message });
      return;
    }

    if (passwordChanged) {
      notifications.show({
        color: "green",
        message: "Profil aktualisiert. Bitte neu anmelden.",
      });
      await logout();
      navigate("/");
      return;
    }
    notifications.show({ color: "green", message: "Profil aktualisiert." });
    refresh();
    onSuccess();
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
            label="Neues Passwort"
            placeholder="Leer lassen, um es nicht zu ändern"
            {...form.getInputProps("password")}
          />
          <PasswordInput
            label="Neues Passwort wiederholen"
            placeholder="Leer lassen, um es nicht zu ändern"
            {...form.getInputProps("confirmPassword")}
          />
        </SimpleGrid>

        <Text fw={600} size="sm" mt="xs">
          Standort
        </Text>
        <SimpleGrid cols={{ base: 1, sm: 2 }}>
          <TextInput
            label="Straße"
            {...form.getInputProps("location.street")}
          />
          <TextInput
            label="Hausnummer"
            {...form.getInputProps("location.houseNumber")}
          />
          <TextInput
            label="PLZ"
            {...form.getInputProps("location.postalCode")}
          />
          <TextInput label="Stadt" {...form.getInputProps("location.city")} />
        </SimpleGrid>

        <Text fw={600} size="sm" mt="xs">
          Lager & Konditionen
        </Text>
        <SimpleGrid cols={{ base: 1, sm: 2 }}>
          <NumberInput
            label="Stellplatz-Kapazität"
            min={0}
            {...form.getInputProps("storage.capacity")}
          />
          <NumberInput
            label="Kosten (€)"
            min={0}
            decimalScale={2}
            {...form.getInputProps("costs")}
          />
          <TimeInput
            label="Servicezeit von"
            {...form.getInputProps("serviceStartTime")}
          />
          <TimeInput
            label="Servicezeit bis"
            {...form.getInputProps("serviceEndTime")}
          />
        </SimpleGrid>
        <TagsInput
          label="Lagerbedingungen"
          {...form.getInputProps("storage.conditions")}
        />
        <TagsInput
          label="Markenspezialisierung"
          {...form.getInputProps("carManufacturers")}
        />

        <Group justify="flex-end">
          <PrimaryButton type="submit" loading={submitting}>
            Speichern
          </PrimaryButton>
        </Group>
      </Stack>
    </form>
  );
}
