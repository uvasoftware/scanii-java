#!/usr/bin/env bash

echo "installing dependencies"
apt-get install -qqy openjdk-8-jdk-headless wget
update-ca-certificates -f &>/dev/null

# maven
source ./.circleci/mvn-install.sh

cd ~/ci

# Maven Release:
mvn --settings ./.circleci/settings.xml -q -DskipTests release:clean release:prepare release:perform -B || exit 1
