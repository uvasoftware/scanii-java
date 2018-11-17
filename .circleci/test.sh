#!/usr/bin/env bash

# maven
source ./.circleci/mvn-install.sh

# running tests
cd ~/ci
mvn verify
