# Spring Workshop - Spring Security - Lab

This repository includes the lab assignment on securing a resource server and accessing the protected resources with valid credentials. 

## Getting Started

This project has three services that are typical for a secured environment. 
* A resource server that has protected resources - in our case employee data
* A client application that wants to access and show the data to a user
* An authorization server that "speaks" OIDC and can issue access tokens

### Building source code

The source material that we're working on comprises several Maven modules. Please make sure that everything compiles successfully. You'll find the top-level `pom.xml` at the root folder of the lab content. Issue a

```bash
$ mvn clean package
```

from within the root folder. This builds all the modules; the fat JARs for every module will be located at the `target` folder for each and every Maven module. Building the Docker images for our services relies on these JAR files, so make sure that you build the whole solution first before building the Docker images. You can also start the modules from within your IDE, the services will be able to connect either way.

### Building Docker images

The source material that you're given features a `docker` folder. You'll find all the scripts for the assignment in that folder. To make sure that you've got all the required Docker images present on your system, we recommend that you start up the baseline scenario. This can be done by issuing

*Linux / MacOS*

```bash
$ cd docker
$ ./build-containers.sh
$ docker-compose up
```

*Windows*

```bash
$ cd docker
$ build-containers.bat
$ docker-compose up
```

This will launch each service and expose their HTTP ports to the host system.

Please make sure that all services launch correctly and respond with their public API.

* The authorization server listens on port `9091`.
* The client application listens on port `9090`.
* The resource server service listens on port `9095`.

All of these services expose their port through the host system and are thus available via `localhost`.

* Click [here](http://localhost:9095/swagger-ui.html) for opening the OpenAPI UI for the resource service.
* Click [here](http://localhost:9091/oauth2/jwks) to see the generated JSON Web Keys on the authorization server.
* Click [here](http://localhost:9090/eureka) for opening the dashboard of the Eureka Server

The API specification is minimalistic. Nevertheless, play around with the OpenAPI UI a bit to get a feel for what we'll be working on as part of the workshop.

If you have any questions or run into problems, please don't hesitate to ask for help.

### Docker CLI Hints

*Starting containers using `docker-compose` - use `--build` if you rebuilt the application with changes * 

```bash
$ docker-compose up --build
```

*Stopping containers using `docker-compose`*

```bash
$ docker-compose stop
```

*Removing containers using `docker-compose`*

```bash
$ docker-compose rm -f
```