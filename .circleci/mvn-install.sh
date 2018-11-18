#!/usr/bin/env bash

apt-get update -qq && apt-get install -qqy wget

export MVN_DOWNLOAD="https://scanii-assets.s3.amazonaws.com/builds/maven/apache-maven-3.6.0-bin.tar.gz"

echo "Downloading maven from: ${MVN_DOWNLOAD}"

cd /tmp
wget ${MVN_DOWNLOAD}
tar -zxvf apache-maven* &>/dev/null
mv apache-maven-*/ /opt
ln -sf /opt/apache-maven-*/bin/mvn /usr/local/bin/
rm -rf /tmp/*

echo "##################################################################################"
mvn --version || exit 99
echo "##################################################################################"
