#!/bin/bash
# Compile script for SMS Java Swing application
set -e
cd "$(dirname "$0")"

MYSQL_CONNECTOR_VERSION="${MYSQL_CONNECTOR_VERSION:-8.4.0}"
JAR_PATH="lib/mysql-connector-j.jar"

mkdir -p lib
if [ ! -f "$JAR_PATH" ]; then
    echo "Downloading MySQL Connector/J ${MYSQL_CONNECTOR_VERSION}..."
    URL="https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/${MYSQL_CONNECTOR_VERSION}/mysql-connector-j-${MYSQL_CONNECTOR_VERSION}.jar"
    if command -v curl >/dev/null 2>&1; then
        curl -fsSL "$URL" -o "$JAR_PATH"
    else
        wget -q "$URL" -O "$JAR_PATH"
    fi
fi

find src -name "*.java" | xargs javac -encoding UTF-8 -cp "$JAR_PATH" -d out
echo "Compilation complete. Run with: ./run.sh"
