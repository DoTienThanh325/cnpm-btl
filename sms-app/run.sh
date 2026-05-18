#!/bin/bash
# Run script for SMS Java Swing application
cd "$(dirname "$0")"
java -cp "out:lib/mysql-connector-j.jar" com.sms.Main
