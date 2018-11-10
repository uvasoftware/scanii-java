#!/usr/bin/env bash

echo "installing dependencies"
apt-get install -qqy openjdk-8-jdk-headless wget
update-ca-certificates -f &>/dev/null

# maven
source ./.circleci/mvn-install.sh

# running tests
cd ~/ci
mvn -q test
