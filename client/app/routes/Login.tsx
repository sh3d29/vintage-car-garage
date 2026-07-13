import {
  Alert,
  Anchor,
  Container,
  Paper,
  PasswordInput,
  Text,
  TextInput,
  Title,
} from "@mantine/core";
import { PrimaryButton } from "~/components/Button";
import { useForm } from "@mantine/form";
import { useState } from "react";
import { Link, Navigate, useNavigate } from "react-router";
import { api } from "~/api/fetch";
import PublicRoute from "~/components/PublicRoute";
import { useAuthContext } from "~/context/AuthContext";
import type { AuthUserDto } from "~/types/dtos";

export default function Login() {
  const navigate = useNavigate();
  const { user, role, login } = useAuthContext();
  const [error, setError] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);

  const form = useForm({
    initialValues: { email: "", password: "" },
  });

  async function handleSubmit(values: typeof form.values) {
    setSubmitting(true);

    const { value, error } = await api.post<AuthUserDto>(
      "/auth/sign-in",
      values,
    );

    setSubmitting(false);

    if (error) {
      setError("E-Mail oder Passwort ist falsch.");
      return;
    }

    login(value);
    navigate(`/${value.role}/dashboard`);
  }

  return (
    <PublicRoute>
      <Container size={420} my={40}>
        <Title ta="center" fw={900}>
          Vintage Car Garage Community
        </Title>

        <Text c="dimmed" size="sm" ta="center" mt={5}>
          Noch kein Konto?{" "}
          <Anchor size="sm" component={Link} to="/register">
            Hier registrieren
          </Anchor>
        </Text>

        <Paper withBorder shadow="md" p={30} mt={30} radius="md">
          <form onSubmit={form.onSubmit(handleSubmit)}>
            <TextInput
              label="E-Mail"
              placeholder="deine@email.de"
              required
              {...form.getInputProps("email")}
            />

            <PasswordInput
              label="Passwort"
              placeholder="Dein Passwort"
              required
              mt="md"
              {...form.getInputProps("password")}
            />

            {error && (
              <Alert color="red" mt="md" variant="light">
                {error}
              </Alert>
            )}

            <PrimaryButton fullWidth mt="xl" type="submit" loading={submitting}>
              Anmelden
            </PrimaryButton>
          </form>
        </Paper>
      </Container>
    </PublicRoute>
  );
}
