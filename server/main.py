from fastapi import FastAPI

from auth import controller as auth_controller
from database import Base, engine
from storage import controller as storage_controller

Base.metadata.create_all(bind=engine)

app = FastAPI()

app.root_path = "/api"

app.include_router(auth_controller.router)
app.include_router(storage_controller.router)
