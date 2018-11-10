#!/usr/bin/env bash

echo "installing dependencies"
apt-get install -qqy openjdk-8-jdk-headless wget
update-ca-certificates -f &>/dev/null

# maven
source ./.circleci/mvn-install.sh

cd ~/ci

# building
mvn -q -DskipTests clean package

ls -lha target/*

# Maven Release:
mvn --settings ./.circleci/settings.xml release:clean release:prepare release:perform -B || exit 1
