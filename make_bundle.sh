#!/bin/sh
version=$1
groupId="org/xmlbeam"
artifactId="xmlprojector"

cd target
mkdir -p "$groupId/$artifactId/$version"
for type in -javadoc.jar -sources.jar .jar .pom ; do
    file="xmlprojector-$version$type"
    cp "$file" "$groupId/$artifactId/$version/"
    cp "$file.asc" "$groupId/$artifactId/$version/"
    md5sum "$file" | cut -d' ' -f1 > "$groupId/$artifactId/$version/$file.md5"    
    sha1sum "$file" | cut -d' ' -f1 > "$groupId/$artifactId/$version/$file.sha1"
done
jar -cvMf xmlbeam-$version"-bundle.jar" "$groupId/"
cd ..
