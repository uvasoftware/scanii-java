#!/usr/bin/env bash

echo "installing GPG keys"

if [ ! -z "$GPG_SECRET_KEYS" ]; then echo $GPG_SECRET_KEYS | base64 --decode | gpg --import; fi

if [ ! -z "$GPG_OWNERTRUST" ]; then echo $GPG_OWNERTRUST | base64 --decode | gpg --import-ownertrust; fi

mvn -B --settings .travis/settings.xml release:prepare -DdryRun=true
mvn -B --settings .travis/settings.xml release:perform -DdryRun=true
