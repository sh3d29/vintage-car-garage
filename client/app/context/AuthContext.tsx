import {
  createContext,
  useContext,
  useEffect,
  useState,
  type PropsWithChildren,
} from "react";
import { useLocation, useNavigate } from "react-router";
import { api } from "~/api/fetch";
import type {
  AuthUserDto,
  CarOwnerOutputDto,
  StorageOwnerOutputDto,
  UserRole,
} from "~/types/dtos";

type CurrentUser = CarOwnerOutputDto | StorageOwnerOutputDto;

interface AuthContextProps {
  user: CurrentUser | null;
  role: UserRole | null;
  isLoading: boolean;
  login: (auth: AuthUserDto) => void;
  logout: () => Promise<void>;
  refresh: () => Promise<void>;
}

const AuthContext = createContext<AuthContextProps | undefined>(undefined);

export function useAuthContext() {
  const ctx = useContext(AuthContext);
  if (!ctx) {
    throw new Error("useAuth must be used within an AuthProvider");
  }
  return ctx;
}

export function AuthProvider({ children }: PropsWithChildren) {
  const [user, setUser] = useState<CurrentUser | null>(null);
  const [role, setRole] = useState<UserRole | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  function login(auth: AuthUserDto) {
    setUser(auth.user);
    setRole(auth.role);
    setIsLoading(false);
  }

  async function logout() {
    await api.post("auth/sign-out");
    setUser(null);
    setRole(null);
  }

  async function refresh() {
    const { value, error } = await api.get<AuthUserDto>("auth/me");

    if (error) {
      setUser(null);
      setRole(null);
    } else {
      setUser(value.user);
      setRole(value.role);
    }

    setIsLoading(false);
  }

  useEffect(() => {
    refresh();
  }, []);

  return (
    <AuthContext.Provider
      value={{ user, role, isLoading, login, logout, refresh }}
    >
      {children}
    </AuthContext.Provider>
  );
}
