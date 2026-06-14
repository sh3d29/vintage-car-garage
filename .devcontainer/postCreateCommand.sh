#!/bin/bash

sudo apt update
sudo apt install -y sqlite3 curl

cd server
curl -sSL https://install.python-poetry.org | python3 -
poetry install

cd ../client
npm i