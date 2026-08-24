# Multi-stage: frontend + Spring Boot in single container (Render free tier friendly)
# Stage 1: Build SPA
FROM node:20-alpine AS frontend
WORKDIR /app
COPY package.json package-lock.json ./
RUN npm ci
COPY . .
# Exclude backend from frontend build context (vite ignores it)
RUN npm run build

# Stage 2: Build Spring Boot jar (copies SPA into static/)
FROM maven:3.9-eclipse-temurin-21 AS backend
WORKDIR /build
COPY backend/pom.xml ./pom.xml
# cache deps
RUN mvn dependency:go-offline -B
COPY backend/src ./src
# copy SPA build into Spring static resources
COPY --from=frontend /app/dist ./src/main/resources/static
RUN mvn package -DskipTests -B

# Stage 3: Runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=backend /build/target/*.jar app.jar
EXPOSE 8080
# Render sets PORT; Spring reads server.port=${PORT:8080}
ENV JAVA_OPTS=""
CMD ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
