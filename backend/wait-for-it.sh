#!/bin/bash
# wait-for-it.sh

# Nombre del host de la base de datos y puerto
HOST="$1"
PORT="$2"
shift 2

# Espera un máximo de 60 segundos a que la base de datos esté disponible
echo "Esperando a que $HOST:$PORT esté listo..."

for i in {1..60}; do
  nc -z "$HOST" "$PORT" > /dev/null 2>&1
  result=$?
  if [ $result -eq 0 ]; then
    echo "Base de datos lista después de $i segundos."
    exec "$@"
    exit 0
  fi
  sleep 1
done

echo "Error: La base de datos no está disponible después de 60 segundos."
exit 1