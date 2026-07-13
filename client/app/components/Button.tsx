import { Button, type ButtonProps } from "@mantine/core";
import type { ComponentPropsWithoutRef } from "react";

type Props = ButtonProps & ComponentPropsWithoutRef<"button">;

export function PrimaryButton(props: Props) {
  return <Button {...props} />;
}

export function CancelButton(props: Props) {
  return <Button {...props} type="button" variant="default" />;
}

export function TableActionButton(props: Props) {
  return <Button {...props} variant="default" size="compact-sm" />;
}

export function DeleteButton(props: Props) {
  return <Button {...props} color="red" variant="outline" size="compact-sm" />;
}

export function ConfirmButton(props: Props) {
  return (
    <Button {...props} color="green" variant="outline" size="compact-sm" />
  );
}
