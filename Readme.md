## Notes: 

### How to setup
Docker command to start with running Docker Container:
`docker-compose build --no-cache` - builds the image from the Dockerfile
`docker-compose up` - runs everything, including the service, redis, rabbitmq and postgres

### How to run tests
`./mvnw -Dtest=SpringBootTestWithTestContainers test`

NB: Unit tests are written with @TestContainers, and therefore need running Docker

### Example Requests
Sample Postman request:
`curl --location 'http://localhost:8080/json_api/current' \
--header 'Content-Type: application/json' \
--data '{
    "requestId": "123456",
    "timestamp": 1737414058,
    "client": "johndoe",
    "currency": "EUR"
}'` - JSON API current request

`curl --location 'http://localhost:8080/json_api/history' \
--header 'Content-Type: application/json' \
--data '{
    "requestId": "123456",
    "timestamp": 1737414058,
    "client": "johndoe",
    "currency": "EUR",
    "period": 1
}'` - JSON API history request

`curl --location 'http://localhost:8080/xml_api/command' \
--header 'Content-Type: application/xml' \
--data '<command id="123456" >
<history consumer="13617162" currency="EUR" period="24" />
</command>'` - XML API history request

`curl --location 'http://localhost:8080/xml_api/command' \
--header 'Content-Type: application/xml' \
--data '<command id="12323234">
    <get consumer="13617162" >
        <currency>EUR</currency>
    </get>
</command>'` - XML API current request 
