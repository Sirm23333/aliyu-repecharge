#!/bin/bash

# 把java-mini-faas-ana-1.0-SNAPSHOT.jar更新到本地maven库，方便打包

export dir=/home/sirm/文档/Project/IdeaProjects/java-mini-fass-ana/target/java-mini-faas-ana-1.0-SNAPSHOT.jar

mvn install:install-file -Dfile=$dir -DgroupId=com.java.mini.faas.ana -DartifactId=java-mini-faas-ana -Dversion=1.0-SNAPSHOT -Dpackaging=jar