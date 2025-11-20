# Imagen base: Tomcat + JDK 17
FROM tomcat:10.1-jdk17-temurin

# Eliminar apps por defecto de Tomcat
RUN rm -rf /usr/local/tomcat/webapps/*

# Copiar el WAR generado por NetBeans (asegúrate de hacer Clean and Build antes)
COPY dist/CONI.war /usr/local/tomcat/webapps/ROOT.war

# Exponer puerto 8080
EXPOSE 8080

# Iniciar Tomcat
CMD ["catalina.sh", "run"]