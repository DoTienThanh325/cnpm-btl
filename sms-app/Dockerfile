FROM eclipse-temurin:17-jdk-jammy AS build

WORKDIR /build

# MySQL Connector/J for both compile and runtime
ARG MYSQL_CONNECTOR_VERSION=8.4.0
ADD https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/${MYSQL_CONNECTOR_VERSION}/mysql-connector-j-${MYSQL_CONNECTOR_VERSION}.jar /build/lib/mysql-connector-j.jar

COPY src ./src
RUN mkdir -p out && \
    find src -name "*.java" > sources.txt && \
    javac -encoding UTF-8 -cp lib/mysql-connector-j.jar -d out @sources.txt

FROM eclipse-temurin:17-jre-jammy

# X11 + fonts for Swing rendering inside the container
RUN apt-get update && apt-get install -y --no-install-recommends \
        libxext6 libxrender1 libxtst6 libxi6 libfreetype6 fontconfig fonts-dejavu \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app
COPY --from=build /build/out ./out
COPY --from=build /build/lib ./lib

ENV SMS_DB_HOST=mysql \
    SMS_DB_PORT=3306 \
    SMS_DB_NAME=sms \
    SMS_DB_USER=sms \
    SMS_DB_PASSWORD=sms

CMD ["java", "-cp", "out:lib/mysql-connector-j.jar", "com.sms.Main"]
