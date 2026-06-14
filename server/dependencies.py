import jwt
from fastapi import Depends, HTTPException, status
from sqlalchemy.orm import Session

from auth.service import get_user_by_email, oauth2_bearer
from config import settings
from database import get_db
from schemas.user import CarOwnerDB, StorageOwnerDB


def route_guard(
    token: str = Depends(oauth2_bearer),
    db: Session = Depends(get_db),
) -> CarOwnerDB | StorageOwnerDB:

    credentials_exception = HTTPException(
        status_code=status.HTTP_401_UNAUTHORIZED,
        detail="Could not validate credentials",
        headers={"WWW-Authenticate": "Bearer"},
    )

    try:
        payload = jwt.decode(
            token, settings.secret_key, algorithms=[settings.algorithm]
        )

        email: str | None = payload.get("sub")

        if email is None:
            raise credentials_exception

    except jwt.PyJWTError:
        raise credentials_exception

    user = get_user_by_email(email, db)

    if user is None:
        raise credentials_exception

    return user
