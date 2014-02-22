#!/bin/sh
version=`xpath pom.xml "/project/version/text()"  2>/dev/null | tail -1`
for type in -javadoc.jar -sources.jar .jar .pom ; do
file="target/xmlprojector-$version$type"
gpg --verify $file".asc" $file || exit 1 
done