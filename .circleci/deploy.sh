#!/usr/bin/env bash

echo "installing dependencies"
apt-get install -qqy openjdk-8-jdk-headless wget gpg
update-ca-certificates -f &>/dev/null

# maven
source ./.circleci/mvn-install.sh

cd ~/ci


# tag repo
git config --global user.email "circleci@uvasoftware.com"
git config --global user.name "CircleCI"

# Maven Release:
mvn -DskipTests  --settings ./.circleci/settings.xml release:prepare  release:perform -B || exit 1
