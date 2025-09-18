# Access Management

API exposing data, api, and ui endpoints related to Access Management.


## Quick Start
Build the project using: mvn clean package

Run the service using: ./t.sh

The Swagger page can be accessed at: http://localhost:8080/access/client/

The Monitoring page can be accessed at: http://localhost:8080/access/monitor

### Setting Up The Service Locally  
* Download JDK 8: https://docs.aws.amazon.com/corretto/latest/corretto-8-ug/downloads-list.html
* If Maven is not installed, install Maven 3: http://maven.apache.org/download.cgi

### Docker
Build & Run image: `sh docker-start.sh`

Stop campaign management service: `docker stop rp-access-management`

Start campaign management service: `docker start rp-access-management`