FROM eclipse-temurin:21-jdk-alpine AS build

RUN apk add --no-cache maven

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY . .
RUN mvn clean package -DskipTests

# --- Runtime : glibc obligatoire pour OpenCV (opencv-platform) ---
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# Libs nécessaires pour OpenCV/JavaCV en runtime
# (miroir fr.archive.ubuntu.com car archive/security.ubuntu.com sont bloqués en HTTP sur ce VPS)
RUN sed -i 's|http://archive.ubuntu.com|http://fr.archive.ubuntu.com|g; s|http://security.ubuntu.com|http://fr.archive.ubuntu.com|g' /etc/apt/sources.list /etc/apt/sources.list.d/* 2>/dev/null || true \
    && apt-get update && apt-get install -y --no-install-recommends \
       libgomp1 \
       ffmpeg \
    && rm -rf /var/lib/apt/lists/*

RUN groupadd -r spring && useradd -r -g spring spring \
    && mkdir -p /app/uploads/fuel-proofs \
    && chown -R spring:spring /app

USER spring:spring

COPY --from=build --chown=spring:spring /app/target/*.jar app.jar

EXPOSE 8090

ENV JAVA_OPTS="-Xms256m -Xmx512m"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]