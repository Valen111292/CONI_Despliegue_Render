FROM tomcat:9.0-jdk11-openjdk-slim

# 1. Instalar netcat (nc). Es crucial para que el script 'wait-for-it.sh' pueda
# verificar la conexión con la base de datos MySQL en el puerto 3306.
RUN apt-get update && apt-get install -y netcat-traditional && rm -rf /var/lib/apt/lists/*

# 2. Copia el script de espera y dale permisos de ejecución.
# Asegúrate de que 'wait-for-it.sh' está en la misma carpeta 'backend/'.
COPY wait-for-it.sh /usr/local/bin/
RUN chmod +x /usr/local/bin/wait-for-it.sh

# 3. Copia el WAR de la aplicación.
COPY dist/CONI.war webapps/ROOT.war

# 4. Define el comando de entrada (CMD).
# Ejecuta el script de espera, indicándole esperar al servicio 'database' en el puerto '3306',
# y una vez listo, que ejecute el comando principal de Tomcat.
CMD ["/usr/local/bin/wait-for-it.sh", "database", "3306", "catalina.sh", "run"]

EXPOSE 8080