#!/bin/sh
set -e
until nc -z ${DB_HOST:-postgres-auth} ${DB_PORT:-5432}; do
  echo "Esperando a que Postgres esté disponible..."
  sleep 2
done

exec java -jar app.jar
