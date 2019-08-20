#!/usr/bin/env bash

JAVA_DOWNLOAD="http://scanii-assets.s3.amazonaws.com/builds/jdk/OpenJDK8U-jdk_x64_linux_hotspot_8u222b10.tar.gz"

apt-get install -qqy wget

echo "Downloading java from: ${JAVA_DOWNLOAD}"

cd /tmp || exit
wget -q ${JAVA_DOWNLOAD}
tar -zxvf OpenJDK8U-jdk_x64_linux_hotspot_*.tar.gz
mv jdk8u*/ /opt/jdk/
ln -sf /opt/jdk/bin/* /usr/local/bin/
rm -rf /tmp/OpenJDK8U-jdk_x64_linux_hotspot_*.tar.gz
java -version

# maven
cd ~/ci || exit
source ./.circleci/mvn-install.sh
