import { Center, Loader } from "@mantine/core";
import { Navigate } from "react-router";
import { useAuthContext } from "~/context/AuthContext";

export default function PublicRoute({
  children,
}: {
  children: React.ReactNode;
}) {
  const { user, role, isLoading } = useAuthContext();

  if (isLoading) {
    return (
      <Center h="100vh">
        <Loader />
      </Center>
    );
  }

  if (user && role) {
    return <Navigate to={`/${role}/dashboard`} replace />;
  }

  return <>{children}</>;
}
