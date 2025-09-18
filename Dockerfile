FROM docker.rp-core.com/hub/maven:3.6.1-jdk-8 AS builder

# Set the working directory
WORKDIR /app

# Copy the Maven project files
COPY pom.xml .
COPY src ./src

ARG BUILD_NUMBER=n/a
ARG IMAGE_TAG=n/a
ARG GIT_COMMIT=n/a
# Build the project and create the WAR file
RUN --mount=type=cache,target=/root/.m2,rw mvn package -U \
    -Dbuild.number=$BUILD_NUMBER \
    -Dci.buildResultKey=$IMAGE_TAG \
    -Dci.repository.revision.number=$GIT_COMMIT

# sonar scan
FROM docker.rp-core.com/devops/docker_centos_base:2_7.8 AS sonar

# Install Sonar CLI package
ARG SONAR_SCANNER_CLI_VERSION=6.2.1.4610-linux-x64
RUN yum install -y unzip \
  && curl https://nexus.fanops.net/nexus/repository/sonarsource.com_sonar-scanner-cli/sonar-scanner-cli-${SONAR_SCANNER_CLI_VERSION}.zip -o sonar-scanner-cli.zip \
  && unzip sonar-scanner-cli.zip -d /opt \
  && mv /opt/sonar-scanner-${SONAR_SCANNER_CLI_VERSION} /opt/sonar-scanner \
  && rm sonar-scanner-cli.zip

ARG GIT_BRANCH=n/a
ARG SONAR_AUTH_TOKEN=n/a
ARG SONAR_HOST_URL=n/a
WORKDIR /app
COPY --from=builder /app /app
COPY sonar-project.properties /app/sonar-project.properties
RUN /opt/sonar-scanner/bin/sonar-scanner -Dproject.settings=sonar-project.properties -Dsonar.branch.name=$GIT_BRANCH -Dsonar.host.url=$SONAR_HOST_URL -Dsonar.login=$SONAR_AUTH_TOKEN;

# Use CentOS 7 as the base image for the final image
FROM docker.rp-core.com/devops/docker_centos_base:2_7.8 AS installer

LABEL maintainer="dvplus.platform.magnite.com"

# Install Java 8, wget, and Tomcat 7
RUN yum install -y java-1.8.0-openjdk wget

ENV TOMCAT_URL="https://maven.fanops.net/nexus/repository/rpm/sources/apache-tomcat/7.0.90/apache-tomcat-7.0.90.tar.gz"
# Download and extract Tomcat
WORKDIR /usr/share/
RUN wget $TOMCAT_URL -O tomcat.tar.gz && \
    mkdir -p /usr/share/tomcat && \
    tar -xzf tomcat.tar.gz -C /usr/share/tomcat --strip-components=1 && \
    rm -f tomcat.tar.gz

# Set environment variables
ENV CATALINA_HOME=/usr/share/tomcat
ENV PATH=$CATALINA_HOME/bin:$PATH

# Copy the custom server.xml file to the Tomcat configuration directory
COPY tomcat/server.xml $CATALINA_HOME/conf/

# Copy the WAR file from the builder stage to Tomcat's webapps directory
COPY --from=builder /app/target/access.war $CATALINA_HOME/webapps/

# Copy the logging.properties file to the Tomcat configuration directory
COPY tomcat/logging.properties $CATALINA_HOME/conf/

# Expose Tomcat & JMX ports
EXPOSE 8080 8181

# Set the entrypoint to start Tomcat
ENTRYPOINT ["sh", "-c", "catalina.sh run"]
