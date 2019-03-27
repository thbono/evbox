# Transaction

EVBox assignment

To run:

    mvn spring-boot:run

The rest API will be available at:

    http://localhost:8080

To run unit tests:

    mvn test
    
To run integration tests:

    mvn verify   

## API

The following URIs are available:

POST /transactions - create a transaction. Body example:

    { "stationId": 1 }

PUT /transaction/x - stop transaction x. Body example:
                                       
    { "consumption": 20 }

GET /transactions - total list of transactions

GET /transactions/stopped - transactions stopped in last minute

GET /transactions/started - transactions started in last minute

DELETE /transactions - delete stopped transactions

## Deployment

To build a Docker image named transaction:

    docker build -t transaction .
    
Default exposed port is 8080.

## Improvements

- TODO: change database to a time shard scheme, improving searches by date (e.g. ElasticSearch)
