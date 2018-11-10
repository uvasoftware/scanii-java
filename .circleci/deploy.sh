#!/usr/bin/env bash

echo "installing dependencies"
apt-get install -qqy openjdk-8-jdk-headless wget
update-ca-certificates -f &>/dev/null

# maven
source ./.circleci/mvn-install.sh

# installing AWS CLI
apt-get install -qqy python-pip && pip install awscli aws-sam-cli

cd ~/ci

# removing snapshot marker:
mvn -q build-helper:parse-version versions:set -DnewVersion=\${parsedVersion.majorVersion}.\${parsedVersion.minorVersion}.\${parsedVersion.incrementalVersion} versions:commit

# building
mvn -q -DskipTests clean package

ls -lha target/*

# SAM packaging
sam package --s3-bucket scanii-assets --s3-prefix sam/guarana  --template-file template.yml --output-template-file guarana.yaml || exit 1

# tagging release:

git add -f guarana.yaml || exit 2

VERSION=$(grep \<version\> pom.xml | xargs | awk -F '[<>]' '{ print $3}')

echo "###################  using version: v$VERSION ###################"

# tag repo
git config --global user.email "circleci@uvasoftware.com"
git config --global user.name "CircleCI"
git tag -a v${VERSION} -m "Release by CircleCI v${VERSION}"
git push origin v${VERSION}

# bumping it to a new snapshot release:
mvn -q build-helper:parse-version versions:set -DnewVersion=\${parsedVersion.majorVersion}.\${parsedVersion.minorVersion}.\${parsedVersion.nextIncrementalVersion}-SNAPSHOT versions:commit

VERSION=$(grep \<version\> pom.xml | xargs | awk -F '[<>]' '{ print $3}')

echo "next version is: $VERSION"

#commit version change
git status
git commit -a -m "bump to ${VERSION} [ci skip]"
git push origin master
