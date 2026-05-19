#!/bin/bash
# Compile script for SMS Java Swing application
cd "$(dirname "$0")"
find src -name "*.java" | xargs javac -encoding UTF-8 -cp "lib/*" -d out
echo "Compilation complete. Run with: ./run.sh"
