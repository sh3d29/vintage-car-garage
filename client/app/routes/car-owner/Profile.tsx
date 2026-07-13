import { useEffect, useState } from "react";
import { useNavigate } from "react-router";
import {
  Group,
  PasswordInput,
  SimpleGrid,
  Stack,
  TextInput,
} from "@mantine/core";
import { useForm } from "@mantine/form";
import { notifications } from "@mantine/notifications";
import { useAuthContext } from "~/context/AuthContext";
import { api } from "~/api/fetch";
import { PrimaryButton } from "~/components/Button";
import type { CarOwnerInputDto, CarOwnerOutputDto } from "~/types/dtos";

export function CarOwnerProfileForm({ onSuccess }: { onSuccess: () => void }) {
  const { user, refresh, logout } = useAuthContext();
  const id = user!.id;
  const navigate = useNavigate();
  const [submitting, setSubmitting] = useState(false);

  const form = useForm({
    initialValues: {
      email: "",
      firstName: "",
      lastName: "",
      password: "",
      confirmPassword: "",
    },
    validate: {
      email: (v) => (/^\S+@\S+$/.test(v) ? null : "Ungültige E-Mail"),
      firstName: (v) => (v ? null : "Vorname erforderlich"),
      lastName: (v) => (v ? null : "Nachname erforderlich"),
      confirmPassword: (v, values) =>
        values.password && v !== values.password
          ? "Passwörter stimmen nicht überein"
          : null,
    },
  });

  useEffect(() => {
    (async () => {
      const { value } = await api.get<CarOwnerOutputDto>(`/car-owners/${id}`);
      if (value) {
        form.setValues({
          email: value.email,
          firstName: value.firstName,
          lastName: value.lastName,
          password: "",
          confirmPassword: "",
        });
      }
    })();
  }, [id]);

  async function submit(values: typeof form.values) {
    setSubmitting(true);
    const payload: CarOwnerInputDto = {
      email: values.email,
      firstName: values.firstName,
      lastName: values.lastName,
    };
    const passwordChanged = values.password.trim().length > 0;
    if (passwordChanged) payload.password = values.password;

    const { error } = await api.patch(`/car-owners/${id}`, payload);
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
        <Group justify="flex-end">
          <PrimaryButton type="submit" loading={submitting}>
            Speichern
          </PrimaryButton>
        </Group>
      </Stack>
    </form>
  );
}
