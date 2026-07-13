import { Links, Meta, Outlet, Scripts, ScrollRestoration } from "react-router";

import {
  ColorSchemeScript,
  MantineProvider,
  mantineHtmlProps,
} from "@mantine/core";
import { ModalsProvider } from "@mantine/modals";
import { Notifications } from "@mantine/notifications";
import "@mantine/core/styles.css";
import "@mantine/dates/styles.css";
import "@mantine/notifications/styles.css";

import type { Route } from "./+types/root";
import { theme } from "./theme";
import { AuthProvider } from "./context/AuthContext";

export const links: Route.LinksFunction = () => [];

export const meta: Route.MetaFunction = () => [
  { charSet: "utf-8" },
  { name: "viewport", content: "width=device-width, initial-scale=1" },
  { title: "Vintage Car Garage" },
];

export function Layout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="de" {...mantineHtmlProps}>
      <head>
        <Meta />
        <Links />
        <ColorSchemeScript />
      </head>
      <body>
        <MantineProvider theme={theme}>
          <ModalsProvider>
            <Notifications />
            <AuthProvider>{children}</AuthProvider>
          </ModalsProvider>
        </MantineProvider>
        <ScrollRestoration />
        <Scripts />
      </body>
    </html>
  );
}

export default function App() {
  return <Outlet />;
}
