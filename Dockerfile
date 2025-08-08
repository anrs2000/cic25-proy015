# Etapa 1: Build (construcción)
FROM maven:3.8.5-openjdk-17-slim AS builder

# Establecer directorio de trabajo
WORKDIR /app

# Copiar archivos de configuración Maven
COPY pom.xml .

# Descargar dependencias (aprovecha cache de Docker)
RUN mvn dependency:go-offline -B

# Copiar código fuente
COPY src ./src

# Compilar y empaquetar la aplicación
RUN mvn clean package

# Etapa 2: Runtime (ejecución)
FROM eclipse-temurin:17.0.16_8-jre-alpine

# Metadatos de la imagen
LABEL maintainer="tu-email@ejemplo.com"
LABEL description="Aplicación Spring Boot - Proyecto Arbol"
LABEL version="1.0"

# Crear usuario no-root para seguridad
RUN addgroup -S appuser && adduser -S -G appuser appuser

# Crear directorio de la aplicación
WORKDIR /app

# Copiar el JAR desde la etapa de build
COPY --from=builder /app/target/*.jar app.jar

# Cambiar propietario del directorio
RUN chown -R appuser:appuser /app

# Cambiar a usuario no-root
USER appuser

# Exponer puerto de la aplicación
EXPOSE 8080

# Variables de entorno por defecto
ENV SPRING_PROFILES_ACTIVE=prod
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# Healthcheck
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Comando de ejecución
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]