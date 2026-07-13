import {
  AppShell,
  Avatar,
  Burger,
  Group,
  Menu,
  NavLink,
  Text,
  Title,
  UnstyledButton,
} from "@mantine/core";
import { useDisclosure } from "@mantine/hooks";
import { Link, Outlet, useLocation, useNavigate } from "react-router";
import { useAuthContext } from "~/context/AuthContext";
import type {
  CarOwnerOutputDto,
  StorageOwnerOutputDto,
  UserRole,
} from "~/types/dtos";
import ProtectedRoute from "./ProtectedRoute";
import { ProfileModal } from "~/components/ProfileModal";
import type { PropsWithChildren } from "react";

export interface NavItem {
  to: string;
  label: string;
}

interface AppLayoutProps extends PropsWithChildren {
  links: NavItem[];
  requiredRole?: UserRole;
}

function displayName(
  user: CarOwnerOutputDto | StorageOwnerOutputDto | null,
): string {
  if (!user) return "";
  if ("companyName" in user) return user.companyName;
  return `${user.firstName} ${user.lastName}`;
}

export default function AppLayout({ links, requiredRole }: AppLayoutProps) {
  const [opened, { toggle, close }] = useDisclosure();
  const [profileOpened, { open: openProfile, close: closeProfile }] =
    useDisclosure();
  const location = useLocation();
  const navigate = useNavigate();
  const { user, role, logout } = useAuthContext();

  async function handleLogout() {
    await logout();
    navigate("/", { replace: true });
  }

  return (
    <ProtectedRoute requiredRole={requiredRole}>
      <AppShell
        header={{ height: 60 }}
        navbar={{
          width: 240,
          breakpoint: "sm",
          collapsed: { mobile: !opened },
        }}
        padding="md"
      >
        <AppShell.Header>
          <Group h="100%" px="md" justify="space-between">
            <Group gap="sm">
              <Burger
                opened={opened}
                onClick={toggle}
                hiddenFrom="sm"
                size="sm"
              />
              <Title order={4}>Vintage Car Garage Community</Title>
            </Group>

            <Menu shadow="md" width={200} position="bottom-end">
              <Menu.Target>
                <UnstyledButton>
                  <Group gap="xs">
                    <Avatar radius="xl" size="sm" color="dark" />
                    <Text size="sm" fw={500} visibleFrom="xs">
                      {displayName(user)}
                    </Text>
                  </Group>
                </UnstyledButton>
              </Menu.Target>
              <Menu.Dropdown>
                <Menu.Item onClick={openProfile}>Profil</Menu.Item>
                <Menu.Divider />
                <Menu.Item color="red" onClick={handleLogout}>
                  Abmelden
                </Menu.Item>
              </Menu.Dropdown>
            </Menu>
          </Group>
        </AppShell.Header>

        <AppShell.Navbar p="md">
          {links.map((link) => (
            <NavLink
              key={link.to}
              component={Link}
              to={link.to}
              label={link.label}
              active={location.pathname === link.to}
              onClick={close}
            />
          ))}
        </AppShell.Navbar>

        <AppShell.Main>
          <Outlet />
        </AppShell.Main>
      </AppShell>

      {role && (
        <ProfileModal
          opened={profileOpened}
          onClose={closeProfile}
          role={role}
        />
      )}
    </ProtectedRoute>
  );
}
