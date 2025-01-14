# Hotel Merger API

The following guide outlines the design decisions, local setup, deployment, and example request responses for the hotel
merger API.

## Design decisions

### Design

* The hotel merger API follows REST architecture and uses HATEAOS to avoid client side URL construction for common
  entities.


* The code employees several design patterns including and not limited to Singleton pattern for cache and Strategy
  pattern to resolve response attributes based on different suppliers for the same hotel.


* The code uses SOLID design principals and OOP principals promoting modularity, reducing complexity, enhancing code
  reusability, fostering collaboration, and enabling quicker adaptations to changing requirements.


* Logs are used within the application with varying log levels INFO, DEBUG, WARN, SEVERE depending on the situation.


* Unit tests are added to cover critical code segments.

### Performance

* A simple cache is used to serve any previous calculated results thus eliminating the need for querying the supplier
  endpoints on each and every call.
    * This cache has a default TTL of 5min for all entries and the entire cache will be wiped every 5mins.
    * Since the code follows dependency inversion, this cache can be replaced independently with a distributed cache
      like Redis for complex scenarios without changing the high-level cache consuming module logic.
    * The supplier endpoints do not provide a way to query a specific hotel / destination ID. Upon cache miss, suppliers will be queried to get all results and re-merging will happen.


* Pagination and filtering support added. Every request will get Max 10 results (configurable) along with other
  pagination meta information like total result count, current page, next / prev page links if applicable.


* Parallel querying of suppliers is used to get the hotel responses with a simple global fixed size thread pool to reduce
  overhead on individual thread creation and management per request.


* External supplier querying has a configurable timeout of 5s. If the external request does not complete within 5s, then it will be cancelled and only the successful responses will be processed.

### Scalability

* The service is RESTful, stateless and can scale independently as a microservices if necessary.


* The service is containerized with Docker to orchestrate with Kubernetes to allow autoscaling and auto recovery if necessary.

### Robustness

* API versioning is added to ensure integrity of the API request / response contracts.


* Input validation and sanitization is added to avoid unintentional or malicious behaviour.


* Exception handlers and a global exception handler are added to catch and report any exception thus avoiding service crash.

## Assumptions

The following assumptions were made during the implementation of this service.

* No rate limiting, authentication and authorization within this service as they should ideally be handled at gateway level.


* No inference of missing required response attributes. 
  * Default data type value will be returned in such cases.

  * ex: If all the suppliers does not provide lat/lng values for a given hotel, then lat/lng values will NOT be inferred based on the address or obtained by querying any 3rd party service. lat/lng will be set to 0.


* External supplier services API contracts are fixed.

## How to run locally

This service is built using Maven, Spring Boot and Java v23. The service starts locally on http://localhost:8080/api/v1/hotels

### Using docker (Recommended)

* This is the recommended way to run the service locally using Docker. Make sure Docker is installed on the computer.


* Run the below commands and it will start the pipeline **`Build -> Test -> Run`**  

```bash
  chmod +x pipeline.sh  // Gives execute permission
  ./pipeline.sh
```

### Without docker

* Install Maven and JDK 23. 


* Run the following command

```bash
  chmod +x mvnrun.sh  // Gives execute permission
  ./mvnrun.sh
```

## Deployments and Endpoints

### Deployment
* Build pipeline is setup with one-click GitHub and DigitalOcean integration. Any commit to the repo will trigger a build and deploy. 
* NOTE: This deployment uses the lowest tier available and the start-up will be slower sometimes.

### Endpoints

Service available at: https://kusalk-hotel-merger-api-dbcnr.ondigitalocean.app/api/v1/hotels

* Query params for filtering: 
  * A comma separated list of optional `destinationIDs` and `hotelIDs`
  * Optional `limit` and `offset` for pagination.

Examples

* Return all merged hotels
  ```
  curl -X GET "https://kusalk-hotel-merger-api-dbcnr.ondigitalocean.app/api/v1/hotels" \
  -H "Accept: application/json" | jq
  ```
* Return hotels after filtering with `destinationIDs` : 
  ```
  curl -X GET "https://kusalk-hotel-merger-api-dbcnr.ondigitalocean.app/api/v1/hotels?destinationIDs=5432,1122" \
  -H "Accept: application/json" | jq
  ```
* Return hotels after filtering with `hotelIDs` : 
  ```
  curl -X GET "https://kusalk-hotel-merger-api-dbcnr.ondigitalocean.app/api/v1/hotels?hotelIDs=SjyX,iJhz" \
  -H "Accept: application/json" | jq
  ```
* Returns a single specified hotel by ID :
  ```
  curl -X GET "https://kusalk-hotel-merger-api-dbcnr.ondigitalocean.app/api/v1/hotels/iJhz" \
  -H "Accept: application/json" | jq
  ```
* With limits and offsets : 
  ```
  curl -X GET "https://kusalk-hotel-merger-api-dbcnr.ondigitalocean.app/api/v1/hotels?hotelIDs=SjyX,iJhz&destinationIDs=5432,1122&limit=1&offset=1" \
  -H "Accept: application/json" | jq
  ```