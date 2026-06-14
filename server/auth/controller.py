from fastapi import APIRouter, Depends, HTTPException, status
from fastapi.security import OAuth2PasswordRequestForm
from sqlalchemy.orm import Session

from database import get_db
from models.user import CarOwner, StorageOwner, Token

from . import service

router = APIRouter()


@router.post(
    "/register/storage_owner",
    response_model=StorageOwner,
    response_model_exclude={"password"},
)
def register_storage_owner(storage_owner: StorageOwner, db: Session = Depends(get_db)):
    existing_user = service.get_user_by_email(storage_owner.email, db)
    if existing_user:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST, detail="Email already registered"
        )

    new_user = service.create_storage_owner(storage_owner, db)

    return StorageOwner(
        email=new_user.email,
        password=new_user.password_hash,
        company_name=new_user.company_name,
        costs=new_user.costs,
        service_time=new_user.service_time,
        location_id=new_user.location_id,
        storage_id=new_user.storage_id,
        car_manufacturer=new_user.car_manufacturer,
    )


@router.post(
    "/register/car_owner", response_model=CarOwner, response_model_exclude={"password"}
)
def register_car_owners(car_owner: CarOwner, db: Session = Depends(get_db)):
    existing_user = service.get_user_by_email(car_owner.email, db)
    if existing_user:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST, detail="Email already registered"
        )

    new_user = service.create_car_owner(car_owner, db)

    return CarOwner(
        email=new_user.email,
        password=new_user.password_hash,
        first_name=new_user.first_name,
        last_name=new_user.last_name,
    )


@router.post("/login", response_model=Token)
def login(
    form_data: OAuth2PasswordRequestForm = Depends(), db: Session = Depends(get_db)
):

    authenticated_user = service.authenticate_user(
        email=form_data.username, password=form_data.password, db=db
    )

    if not authenticated_user:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Invalid credentials",
            headers={"WWW-Authenticate": "Bearer"},
        )

    token = service.create_access_token(email=authenticated_user.email)

    return {"access_token": token, "token_type": "bearer"}
