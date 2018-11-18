#!/usr/bin/env bash

update-ca-certificates -f &>/dev/null

# maven
source ./.circleci/mvn-install.sh

# running tests
cd ~/ci
mvn verify
