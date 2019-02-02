# Reservations API

A REST API service that manages a campsite reservations. The API is implemented using Spring Boot, Spring MVC,
Spring Data JPA and PostgreSQL as database. Flyway is used for database migration.

## Prerequisites

1) Docker and Docker Compose

## Lauching the application

Execute the command `$ docker-compose -V`. The -V flag recreates anonymous volumes instead of retrieving data from the previous containers.

## Running the tests

You can run the tests executing the command `$ ./mvnw clean test`.
 
There are unit test for Reservation creation and the 
Availability Services as well as integration tests for the Reservation Service which test race conditions with concurrent writes.
To handle the case when multiple users attempt to reserve the campsite for the same/overlapping date(s), an Exclusion constraint 
was created on the reservation table. More info about exclusion constraints can be found 
[here](https://www.postgresql.org/docs/10/ddl-constraints.html#DDL-CONSTRAINTS-EXCLUSION).

## Usage 

API documentation was created using SpringFox and Swagger UI. It can be found on http://localhost:8080/swagger-ui.html.

The API expose the following endpoints: 

### Creating a reservation

Endpoint: ```POST /v1.0/campsite/reservations```

Request example: 

```
POST /v1.0/campsite/reservations HTTP/1.1
HOST: localhost:8080
content-type: application/json

{
  "firstName": "Thomas",
  "lastName": "Anderson",
  "email": "neo@matrix.com",
  "arrival": "2019-02-11",
  "departure": "2019-02-13"
}
```
Response body
```
HTTP/1.1 201
content-type: application/json;charset=UTF-8 
location: http://localhost:8080/v1.0/campsite/reservations/1ec37597-1cf6-4991-9d84-1148e4367ab7 
{
  "id": "1ec37597-1cf6-4991-9d84-1148e4367ab7",
  "firstName": "Thomas",
  "lastName": "Anderson",
  "email": "neo@matrix.com",
  "arrival": "2019-02-11",
  "departure": "2019-02-13",
  "canceled": "false",
  "version": "0"
}
```

### Getting an existing reservation

Endpoint: ```GET /v1.0/campsite/reservations/{id}```

Request example:

```
GET /v1.0/campsite/reservations/1ec37597-1cf6-4991-9d84-1148e4367ab7 HTTP/1.1
HOST: localhost:8080
accept: application/json
```

Response example:

```
HTTP/1.1 200
Content-Type: application/json;charset=UTF-8
{
  "id": "1ec37597-1cf6-4991-9d84-1148e4367ab7",
  "firstName": "Thomas",
  "lastName": "Anderson",
  "email": "neo@matrix.com",
  "arrival": "2019-02-11",
  "departure": "2019-02-13",
  "canceled": "false",
  "version": "0"
}
```


### Updating an existing reservation

Endpoint ```PUT /v1.0/campsite/reservations/{id}```

Request example: 

```
PUT /v1.0/campsite/reservations/1ec37597-1cf6-4991-9d84-1148e4367ab7 HTTP/1.1
HOST: localhost:8080
content-type: application/json

{
  "firstName": "Thomas",
  "lastName": "Anderson",
  "email": "neo@matrix.com",
  "arrival": "2019-02-13",
  "departure": "2019-02-15",
  "version": "0"
}
```

Response example:

```
HTTP/1.1 200
Content-Type: application/json;charset=UTF-8
{
  "id": "1ec37597-1cf6-4991-9d84-1148e4367ab7",
  "firstName": "Thomas",
  "lastName": "Anderson",
  "email": "neo@matrix.com",
  "arrival": "2019-02-13",
  "departure": "2019-02-15",
  "canceled": "false",
  "version": "1"
}
```

### Canceling an existing reservation

Endpoint ```DELETE /v1.0/campsite/reservations/{id}```


Request example: 

```
DELETE /v1.0/campsite/reservations/1ec37597-1cf6-4991-9d84-1148e4367ab7 HTTP/1.1
HOST: localhost:8080
```

Response example:

```
HTTP/1.1 200
```

### Consulting the campsite availability

Endpoint: ```GET /v1.0/campsite/availability```

When this endpoint is used, the system defines the search period to be one month starting from tomorrow.
The last day of a reservation is not considered in the calculation of not available days.

Request example: 

```
GET /v1.0/campsite/availability HTTP/1.1
HOST: localhost:8080
```

Response example:

```
HTTP/1.1 200
Content-Type: application/json;charset=UTF-8
{
  "startDate": "2019-02-01",
  "endDate": "2019-03-01",
  "notAvailableDates": []
}
```

### Consulting the campsite for a giving period

Endpoint ```GET /v1.0/campsite/availability/2019-02-10/2019-02-20```

Request example: 

```
GET /v1.0/campsite/availability HTTP/1.1
HOST: localhost:8080
```

Response example:

```
HTTP/1.1 200
Content-Type: application/json;charset=UTF-8
{
  "startDate": "2019-02-10",
  "endDate": "2019-02-20",
  "notAvailableDates": [
    "2019-02-11",
    "2019-02-12"
  ]
}
```

## Known Issues

1) Lack of tests for API
2) Vendor specific feature to handle concurrent attempts to reserve the campsite for the same/overlapping date(s) 
3) Blocking implementation instead of non-blocking
4) Not exactly a REST API as HATEOAS was not used. More info [here](http://roy.gbiv.com/untangled/2008/rest-apis-must-be-hypertext-driven).
5) Application, Domain and infrastructure should be modules.
6) Unit and Integration tests should be separated in a better way.
