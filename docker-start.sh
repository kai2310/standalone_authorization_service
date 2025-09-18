#!/bin/bash

# stop and delete running container with same name
docker stop rp-access-management || true && docker rm -v rp-access-management || true

docker build --file=Dockerfile \
  --tag=rp-access-management:local --rm=true .

# Run the Docker container
# run the app in Los Angeles timezone
docker run --name=rp-access-management \
  --publish=8080:8080 --publish=8181:8181 \
  --volume config:/app/rp-access-service/conf \
  --env JAVA_OPTS="-Duser.timezone=America/Los_Angeles -XX:+AlwaysPreTouch -XX:ConcGCThreads=2 -XX:MaxTenuringThreshold=2 -XX:TargetSurvivorRatio=30" \
  --env CATALINA_OPTS="-Dfile.encoding=UTF8 -Djava.net.preferIPv4Stack=true -Djava.security.egd=file:/dev/./urandom -Dsun.net.inetaddr.ttl=600 -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=8181 -Dcom.sun.management.jmxremote.rmi.port=8181 -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false -Djava.rmi.server.hostname=localhost -Dapplication_environment=local -Dlog.dir=/app/rp-access-service/log" \
  rp-access-management:local
