# Access Management

API exposing data, api, and ui endpoints related to Identity Access Management.
Please see the [Access Management Service](https://wiki.rubiconproject.com/display/Tech/Access+Management+Service) for more information.


## Quick Start
Build the project using: mvn clean package

Run the service using: ./t.sh

The Swagger page can be accessed at: http://localhost:8080/access/client/

The Monitoring page can be accessed at: http://localhost:8080/access/monitor

### Setting Up The Service Locally  
* Download JDK 8: https://docs.aws.amazon.com/corretto/latest/corretto-8-ug/downloads-list.html
* If Maven is not installed, install Maven 3: http://maven.apache.org/download.cgi    
* If you are developing locally please make sure to create `~/.m2/settings.xml` file with the content below:
```xml
  <settings xmlns="http://maven.apache.org/POM/4.0.0"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://maven.apache.org/POM/4.0.0http://maven.apache.org/xsd/settings-1.0.0.xsd">
    <mirrors>
        <mirror>
            <id>internal-repository</id>
            <name>Rubicon Nexus</name>
            <url>https://mvn.fanops.net/nexus/content/groups/public/</url>
            <mirrorOf>central</mirrorOf>
        </mirror>
    </mirrors>
  </settings>
```

### Docker
Build & Run image: `sh docker-start.sh`

Stop campaign management service: `docker stop rp-access-management`

Start campaign management service: `docker start rp-access-management`


### How To Develop Against This Code Base

Our team has a few general guidelines that must be followed when developing in our code bases. Please see the
following [document](https://magnite.atlassian.net/wiki/spaces/PLAT/pages/246153349/How+to+Develop+In+the+Publisher+Squad+Code+Bases)