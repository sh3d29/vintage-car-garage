export type EitherType<T> =
  | { value: T; error: null }
  | { value: null; error: Error };

export class Either {
  static async succeed<T>(value: T): Promise<EitherType<T>> {
    return { value, error: null };
  }

  static async fail<T>(error: Error): Promise<EitherType<T>> {
    return { value: null, error };
  }
}
