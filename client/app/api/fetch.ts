import { Either, type EitherType } from "./either";

const API_BASE = "/api";

async function request<T>(
  method: string,
  url: string,
  body?: unknown,
): Promise<EitherType<T>> {
  const options: RequestInit = {
    method,
    credentials: "include",
    headers: { "Content-Type": "application/json" },
    body: body !== undefined ? JSON.stringify(body) : undefined,
  };

  return apiFetch<T>(`${API_BASE}/${url}`, options);
}

async function apiFetch<T>(
  url: string,
  options?: RequestInit,
): Promise<EitherType<T>> {
  try {
    const response = await fetch(url, options);

    const text = await response.text();
    const data = text ? JSON.parse(text) : undefined;

    if (!response.ok) {
      return Either.fail(new Error(data?.errorMessage ?? response.statusText));
    }

    if (response.status === 204) {
      return Either.succeed<T>(undefined as T);
    }

    return Either.succeed<T>(data as T);
  } catch (err) {
    return Either.fail(new Error(String(err)));
  }
}

export const api = {
  get: <T>(url: string) => request<T>("GET", url),
  post: <T, TBody = unknown>(url: string, body?: TBody) =>
    request<T>("POST", url, body),
  patch: <T, TBody = unknown>(url: string, body?: TBody) =>
    request<T>("PATCH", url, body),
  put: <T, TBody = unknown>(url: string, body?: TBody) =>
    request<T>("PUT", url, body),
  delete: <T = void>(url: string) => request<T>("DELETE", url),
};
