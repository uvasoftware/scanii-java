#!/usr/bin/env bash

export JAVA_HOME=$(readlink -f /usr/bin/java | sed "s:bin/java::")

# maven
source ./.circleci/mvn-install.sh

# running tests
cd ~/ci
mvn verify
