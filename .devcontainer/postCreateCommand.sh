#!/bin/bash

sudo apt update
sudo apt install -y sqlite3 curl zip unzip

source "/usr/local/sdkman/bin/sdkman-init.sh"

sdk install quarkus

cd client || exit
npm install
cd ..