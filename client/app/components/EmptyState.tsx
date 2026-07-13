import { Center, Stack, Text, ThemeIcon } from "@mantine/core";
import type { ReactNode } from "react";

interface EmptyStateProps {
  icon?: ReactNode;
  title: string;
  description?: string;
  action?: ReactNode;
}

export function EmptyState({
  icon,
  title,
  description,
  action,
}: EmptyStateProps) {
  return (
    <Center py="xl">
      <Stack align="center" gap="xs">
        {icon && (
          <ThemeIcon variant="light" size="xl" radius="xl" color="gray">
            {icon}
          </ThemeIcon>
        )}
        <Text fw={600}>{title}</Text>
        {description && (
          <Text c="dimmed" size="sm" ta="center" maw={360}>
            {description}
          </Text>
        )}
        {action}
      </Stack>
    </Center>
  );
}
