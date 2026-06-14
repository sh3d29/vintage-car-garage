import { Links, Meta, Outlet, Scripts, ScrollRestoration } from "react-router";

import { MantineProvider } from "@mantine/core";
import "@mantine/core/styles.css";
import type { Route } from "./+types/root";

export const links: Route.LinksFunction = () => [];

export const meta: Route.MetaFunction = () => [
  {
    charSet: "utf-8",
  },
  {
    name: "viewport",
    content: "width=device-width, initial-scale=1",
  },
];

export function Layout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="de">
      <head>
        <Meta />
        <Links />
      </head>
      <body>
        <MantineProvider>{children}</MantineProvider>
        <ScrollRestoration />
        <Scripts />
      </body>
    </html>
  );
}

export default function App() {
  return <Outlet />;
}
