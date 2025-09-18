#!/bin/bash
export MAVEN_OPTS="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005 -Xmx512m -XX:MaxPermSize=128m -Dcom.sun.management.jmxremote -Dapplication_environment=local -Daccess.logfile.location=config -Dlog.dir=target/logs"
mvn clean package tomcat7:run-war -DskipTests=true
