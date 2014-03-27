#!/bin/sh
#version=`xpath pom.xml "/project/version/text()"  2>/dev/null | tail -1`
version=$1
echo checking version $version
for type in -javadoc.jar -sources.jar .jar .pom ; do
file="target/xmlprojector-$version$type"
echo $file
gpg --verify $file".asc" $file || exit 1 
done