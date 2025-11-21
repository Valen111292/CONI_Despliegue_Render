# Etapa 1: construir la app
FROM node:18-alpine AS build
WORKDIR /app

COPY package*.json ./
RUN npm install

COPY . .

# 🔥 Fix permisos para react-scripts (Windows → Linux issue)
RUN chmod +x node_modules/.bin/react-scripts

RUN npm run build

# Etapa 2: servir con nginx
FROM nginx:stable-alpine
COPY --from=build /app/build /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
