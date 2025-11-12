# Usar una imagen base de Tomcat
FROM tomcat:9.0-jdk17-temurin

# Eliminar la aplicación de ejemplo predeterminada
RUN rm -rf /usr/local/tomcat/webapps/*

# Copiar el archivo .war al contexto /CONI
COPY dist/CONI.war /usr/local/tomcat/webapps/CONI.war

# Exponer el puerto de Tomcat
EXPOSE 8080

# Comando para iniciar Tomcat
CMD ["catalina.sh", "run"]