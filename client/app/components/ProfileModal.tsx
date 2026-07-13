import { Modal } from "@mantine/core";
import { useNavigate } from "react-router";
import type { UserRole } from "~/types/dtos";
import { CarOwnerProfileForm } from "~/routes/car-owner/Profile";
import { StorageOwnerProfileForm } from "~/routes/storage-owner/Profile";

export function ProfileModal({
  opened,
  onClose,
  role,
}: {
  opened: boolean;
  onClose: () => void;
  role: UserRole;
}) {
  const navigate = useNavigate();

  function onSuccess() {
    onClose();
    navigate(`/${role}/dashboard`);
  }

  return (
    <Modal
      opened={opened}
      onClose={onClose}
      title={role === "storage-owner" ? "Profil & Lager" : "Profil"}
      size={role === "storage-owner" ? "lg" : "md"}
    >
      {role === "car-owner" ? (
        <CarOwnerProfileForm onSuccess={onSuccess} />
      ) : (
        <StorageOwnerProfileForm onSuccess={onSuccess} />
      )}
    </Modal>
  );
}
