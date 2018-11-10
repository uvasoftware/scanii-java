#!/usr/bin/env bash

echo "installing dependencies"
apt-get install -qqy openjdk-8-jdk-headless wget
update-ca-certificates -f &>/dev/null

# maven
source ./.circleci/mvn-install.sh

cd ~/ci


# tag repo
git config --global user.email "circleci@uvasoftware.com"
git config --global user.name "CircleCI"

# Maven Release:
mvn --settings ./.circleci/settings.xml -DskipTests release:clean release:prepare release:perform -B || exit 1
