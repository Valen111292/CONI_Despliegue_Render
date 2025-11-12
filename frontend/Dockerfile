# --- 1. Fase de Construcción (Build Stage) ---
FROM node:20-alpine AS build

# Establecer el directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiar archivos de manifiesto
COPY package.json .
COPY package-lock.json .

# Instalar las dependencias
RUN npm install --legacy-peer-deps

# Copiar el código fuente completo del frontend
COPY . .

# ----------------------------------------------------
# 💡 FIX DEFINITIVO: Permisos de Ejecución
# ----------------------------------------------------
# 1. Aseguramos que estamos como usuario root (aunque esto ya lo intentamos, lo mantenemos).
USER root 

# 2. Hacemos que todos los archivos dentro de /app tengan permisos de ejecución.
# Esto es crucial para que 'react-scripts' pueda correr.
RUN chmod -R 777 /app

# Ejecutar el comando de construcción de React. Esto generará la carpeta 'build'
RUN npm run build
# ----------------------------------------------------

# --- 2. Fase de Ejecución (Run Stage) ---
# Usamos una imagen de Nginx muy ligera para servir los archivos estáticos.
FROM nginx:alpine

# Eliminar el archivo de configuración por defecto de Nginx
RUN rm /etc/nginx/conf.d/default.conf

# Copiar la configuración personalizada de Nginx
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Copiar los archivos estáticos generados desde la fase de construcción
COPY --from=build /app/build /usr/share/nginx/html

# El puerto por defecto para Nginx es 80
EXPOSE 80

# Comando para iniciar el servidor
CMD ["nginx", "-g", "daemon off;"]