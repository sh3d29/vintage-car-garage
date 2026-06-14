from enum import Enum
from typing import Optional

from pydantic import BaseModel


class UserType(str, Enum):
    STORAGE_OWNER = "storage_owner"
    CAR_OWNER = "car_owner"


class User(BaseModel):
    email: str
    password: str


class StorageOwner(User):
    company_name: str
    costs: str
    service_time: str
    location_id: str
    storage_id: str
    car_manufacturer: Optional[list[str]]


class CarOwner(User):
    first_name: str
    last_name: str


class Token(BaseModel):
    access_token: str
    token_type: str
