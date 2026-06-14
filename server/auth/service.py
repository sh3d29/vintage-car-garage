import uuid
from datetime import datetime, timedelta, timezone

import bcrypt
import jwt
from fastapi.security import OAuth2PasswordBearer
from sqlalchemy import select
from sqlalchemy.orm import Session

from config import settings
from models.user import CarOwner, StorageOwner
from schemas.user import CarOwnerDB, StorageOwnerDB

oauth2_bearer = OAuth2PasswordBearer(tokenUrl="api/login")


def verify_password(plain_password: str, hashed_password: str) -> bool:
    return bcrypt.checkpw(
        plain_password.encode("utf-8"), hashed_password.encode("utf-8")
    )


def get_password_hash(password: str) -> str:
    salt = bcrypt.gensalt()
    hashed_password = bcrypt.hashpw(password.encode("utf-8"), salt)
    return hashed_password.decode("utf-8")


def get_user_by_email(email: str, db: Session) -> StorageOwnerDB | CarOwnerDB | None:
    car_owner = db.scalar(select(CarOwnerDB).where(CarOwnerDB.email == email))
    if car_owner:
        return car_owner

    storage_owner = db.scalar(
        select(StorageOwnerDB).where(StorageOwnerDB.email == email)
    )
    if storage_owner:
        return storage_owner

    return None


def authenticate_user(
    email: str, password: str, db: Session
) -> StorageOwnerDB | CarOwnerDB | None:
    user = get_user_by_email(email, db)

    if not user or not verify_password(password, str(user.password_hash)):
        return None
    return user


def create_access_token(email: str) -> str:
    expires = datetime.now(timezone.utc) + timedelta(
        minutes=settings.access_token_expire_minutes
    )

    payload = {"sub": email, "exp": expires}
    return jwt.encode(payload, settings.secret_key, algorithm=settings.algorithm)


def create_storage_owner(storage_owner: StorageOwner, db: Session) -> StorageOwnerDB:
    user_id = str(uuid.uuid4())

    new_storage_owner = StorageOwnerDB(
        id=user_id,
        password_hash=get_password_hash(storage_owner.password),
        **storage_owner.model_dump(exclude={"password"}),
    )

    db.add(new_storage_owner)
    db.commit()
    db.refresh(new_storage_owner)

    return new_storage_owner


def create_car_owner(car_owner: CarOwner, db: Session) -> CarOwnerDB:
    user_id = str(uuid.uuid4())

    new_car_owner = CarOwnerDB(
        id=user_id,
        password_hash=get_password_hash(car_owner.password),
        **car_owner.model_dump(exclude={"password"}),
    )

    db.add(new_car_owner)
    db.commit()
    db.refresh(new_car_owner)

    return new_car_owner
