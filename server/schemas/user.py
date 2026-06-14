from typing import Optional

from sqlalchemy import JSON, String
from sqlalchemy.orm import Mapped, mapped_column

from database import Base


class StorageOwnerDB(Base):
    __tablename__ = "storage_owners"

    id: Mapped[str] = mapped_column(String, primary_key=True, unique=True)
    email: Mapped[str] = mapped_column(String, unique=True)
    password_hash: Mapped[str] = mapped_column(String)
    company_name: Mapped[str] = mapped_column(String)
    costs: Mapped[str] = mapped_column(String)
    service_time: Mapped[str] = mapped_column(String)
    location_id: Mapped[str] = mapped_column(String)
    storage_id: Mapped[str] = mapped_column(String)
    car_manufacturers: Mapped[Optional[list[str]]] = mapped_column(JSON)


class CarOwnerDB(Base):
    __tablename__ = "car_owners"

    id: Mapped[str] = mapped_column(String, primary_key=True, unique=True)
    email: Mapped[str] = mapped_column(String, unique=True)
    password_hash: Mapped[str] = mapped_column(String)
    first_name: Mapped[str] = mapped_column(String)
    last_name: Mapped[str] = mapped_column(String)
