from fastapi import APIRouter, Depends

from dependencies import route_guard

router = APIRouter(prefix="/storage", dependencies=[Depends(route_guard)])


@router.post("/test", response_model=str)
def register_storage_owner():
    return "test successful"
