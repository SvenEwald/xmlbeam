#!/bin/sh
#version=`xpath pom.xml "/project/version/text()"  2>/dev/null | tail -1`
version=$1
cd target
for type in -javadoc.jar -sources.jar .jar .pom ; do
file="xmlprojector-$version$type"
allfiles="$allfiles $file $file.asc"
done
jar -cvf xmlbeam-$version"-bundle.jar" $allfiles
cd ..
