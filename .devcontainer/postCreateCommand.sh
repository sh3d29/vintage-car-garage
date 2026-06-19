#!/bin/bash

# 1. System-Pakete
sudo apt update
sudo apt install -y sqlite3 curl zip unzip

# 2. Das BEREITS VORHANDENE SDKMAN vom Dev-Container laden
# Microsoft speichert es standardmäßig unter /usr/local/sdkman
source "/usr/local/sdkman/bin/sdkman-init.sh"

# 3. Quarkus installieren (nutzt jetzt die richtige, globale Version)
sdk install quarkus

# 4. Frontend
cd client || exit
npm install
cd ..