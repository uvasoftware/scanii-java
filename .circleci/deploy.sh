#!/usr/bin/env bash

echo "installing dependencies"
apt-get install -qqy openjdk-8-jdk-headless wget gpg
update-ca-certificates -f &>/dev/null

# maven
source ./.circleci/mvn-install.sh

cd ~/ci || exit

# removing snapshot marker:
mvn -q build-helper:parse-version versions:set -DnewVersion=\${parsedVersion.majorVersion}.\${parsedVersion.minorVersion}.\${parsedVersion.incrementalVersion} versions:commit

# PGP key import
echo "${GPG_KEY}" | base64 --decode &>/tmp/pgp-subkey
gpg --import /tmp/pgp-subkey

# Maven Release:
mvn --settings .circleci/settings.xml -DskipTests -P release-sign-artifacts clean package deploy

# tagging release:
VERSION=$(grep \<version\> pom.xml | xargs | awk -F '[<>]' '{ print $3}')

echo "###################  using version: v$VERSION ###################"

# tag repo
git config --global user.email "circleci@uvasoftware.com"
git config --global user.name "CircleCI"
git tag -a v"${VERSION}" -m "Release by CircleCI v${VERSION}"
git push origin v"${VERSION}"

# bumping it to a new snapshot release:
mvn -q build-helper:parse-version versions:set -DnewVersion=\${parsedVersion.majorVersion}.\${parsedVersion.nextMinorVersion}.0-SNAPSHOT versions:commit

VERSION=$(grep \<version\> pom.xml | xargs | awk -F '[<>]' '{ print $3}')

echo "next version is: $VERSION"

#commit version change
git status
git commit -a -m "bump to ${VERSION} [ci skip]"
git push origin master
