#!/usr/bin/env bash

set -e

cd "$(dirname "$0")"

# Carrega as variáveis de ambiente
set -a
source .env.local
set +a

# Inicia o backend PREVI+
./mvnw spring-boot:run
