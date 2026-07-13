import { Center, Loader } from "@mantine/core";
import { useEffect, type PropsWithChildren } from "react";
import { Navigate, useNavigate } from "react-router";
import { useAuthContext } from "~/context/AuthContext";
import type { UserRole } from "~/types/dtos";

interface ProtectedRouteProps extends PropsWithChildren {
  requiredRole?: UserRole;
}

export default function ProtectedRoute({
  requiredRole,
  children,
}: ProtectedRouteProps) {
  const { user, role, isLoading } = useAuthContext();

  if (isLoading) {
    return (
      <Center h="100vh">
        <Loader />
      </Center>
    );
  }

  if (!user) {
    return <Navigate to="/" replace />;
  }

  if (requiredRole && role !== requiredRole) {
    return <Navigate to={`/${role}/dashboard`} replace />;
  }

  return <>{children}</>;
}
