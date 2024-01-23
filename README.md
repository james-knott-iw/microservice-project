# Microservice Project (Build Something Useful)

This repository contains the code for a Spring Boot API, a Dockerfile to containerise the API and a Docker Compose file to orchestrate the Kong gateway, API and PostgresSQL database images.

## System Requirements

- Java JDK17+
- Maven
- Docker

## Getting Started

First you need to clone the repository.

This can be done by HTTPS, SSH or GitHubCLI.

### HTTPS

```bash
git clone https://github.com/james-knott-iw/microservice-project.git
```

### SSH

```bash
git clone git@github.com:james-knott-iw/microservice-project.git
```

### GitHubCLI

```bash
gh repo clone james-knott-iw/microservice-project
```

Open the `/microservice-project` directory in a terminal or IDE.

## Microservice

To begin, enter the [/microservice](/microservice) directory:

```bash
cd microservice
```

### Package Spring Boot API into JAR file

The next step is to package the spring boot application into a `.jar` file which will be located at `/target/microservice-0.0.1-SNAPSHOT.jar`:

```bash
mvn clean package -DskipTests
```

### Docker Compose File

In this project we need 3 applications running in 3 separate containers. Our Spring Boot API, the Postgres Database and pgAdmin dashboard. A Docker compose file helps to define multiple containers at once. There is one located in [/demo-api/compose.yaml](/demo-api/compose.yaml). Each container is defined as a `service`.

#### App

The first service defined is `app`. This service runs our Spring Boot API container.

- The service is based off an image called `microservice:latest`.
- The build context specifies that the image will be built using a Dockerfile within the same directory i.e. `/demo-api`. This Dockerfile will build the `microservice:latest` image.
- The `app` service `depends_on` the [db](#db) service. Therefore, the `app` service will start after the [db](#db) service service starts.
- There are three environment variables to set. `SPRING_DATASOURCE_URL` specifies the database URL so Spring knows where to connect to. `SPRING_DATA_SOURCE_USERNAME` and `SPRING_DATASOURCE_PASSWORD` specify the credentials used to log into the database.
- Port `8080` on the host machine (your local machine) is mapped to port `8080` on the container. The Spring Boot application hosts the server on port `8080` on the container and this allows you to access it through port `8080` on your machine.

#### DB

The second service defined is `db`. This service runs our Postgres database container.

- This service uses the `postgres:13.1-alpine` image.
- `restart` is set to `always`. Once the container is started it will restart anytime it stops or fails.
- The `container_name` is set to `db` to allow for easy identification.
- Two environment variables are set. `POSTGRES_USER` and `POSTGRES_PASSWORD` allow you to define the user credentials for the Postgres database.

#### pgAdmin

The third service defined is `pgadmin`. This service runs our pgAdmin container which is an admin dashboard GUI for the Postgres DB.

- This service uses the `dpage/pgadmin4:latest` image.
- `restart` is set to `always`. Once the container is started it will restart anytime it stops or fails.
- Two environment variables are set. `PGADMIN_DEFAULT_EMAIL` and `PGADMIN_DEFAULT_PASSWORD` these are the admin credentials used to login to the pgAdmin dashboard.
- Port `5050` on the host machine(your machine) is mapped to to port `80` on the container. This will allow you to access the containers port `80` through port `5050` on your machine (the host).
- This service depends on the [db](#db) service. Therefore, `pgadmin` will start after the [db](#db) service is running.

#### Kong

The fourth service defined is `kong`. This service runs our Kong gateway container. This is an API gateway which sits in front of our API. Consumers will send requests to the API gateway which will then forward them onto our API.

- This service uses the `kong:latest` image.
- `restart` is set to `always`. Once the container is started it will restart anytime it stops or fails.

- There are a few environment variables set:
  - `KONG_DATABASE` set to `off` - specifies the type of database Kong is using.
  - `KONG_DECLARATIVE_CONFIG` set to `/kong/declarative/kong.yml` - the path to the declarative config which configures the  Kong gateway features such as Services and Routes.
  - `KONG_PG_USER` set to `kong` - the `POSTGRES_USER` set in the `postgres` service.
  - `KONG_PG_PASSWORD` set to `kongpass` - the `POSTGRES_PASSWORD` set in the `postgres` service.
  - All `_LOG` variables set filepaths for the logs to output to.
  - `KONG_ADMIN_LISTEN` set to `0.0.0.0:8001` - specifies the port the Kong Admin API listens on for requests.
  - `KONG_ADMIN_GUI_URL` set to `http://localhost:8002` - specifies the URL for accessing the Kong Manager GUI.
- Ports mappings `8000:8000` and `8443:8443` from the host's ports to container's ports - which take requests from Consumers through in HTTP and HTTPS, respectively.
- Ports mappings `8001:8001` and `8444:8444` from the host's ports to container's ports - which listen for requests to the Admin API through HTTP and HTTPS, respectively.
- Port mapping `8002:8002` maps the host's `8002` port to the container's `8002` port - which is used to listen for HTTP requests for the Kong Manager GUI.

#### kong-net

This network is set as the default Docker network. As this is the default network, all containers defined in [compose.yaml](/microservice/compose.yaml) will be on this Docker network and thus, will be able to communicate with each other.

## Running the Containers

To run the containers defined in [compose.yaml](/microservice/compose.yaml):

```bash
docker-compose up -d
```

This command will run the containers in detached mode and therefore, they will run in the background.

To stop and remove the containers:

```bash
docker-compose down
```

## Access the pgAdmin Dashboard

The pgAdmin dashboard is available at [http://localhost:5050](http://localhost:5050). The first time accessing the dashboard you will have to login.  

- Your email address will be whatever you defined in the [pgadmin](#pgadmin) service environment variable `PGADMIN_DEFAULT_EMAIL`.
- Your password will be whatever you defined in the [pgadmin](#pgadmin) service environment variable `PGADMIN_DEFAULT_PASSWORD`.

Once successfully logged in, you will be brought to the dashboard home page. To view the Postgres database defined in [db](#db), you will need to click `Add New Server`.

- In the `General` tab, give your server the name `db`.
- Navigate to the `Connection` tab.
- For `Host name/address` we can use the name of the Postgres service [db](#db).
- Make sure the port is `5432`.
- `Username` is the `POSTGRES_USER` defined in the [db](#db) service.
- `Password` is the `POSTGRES_PASSWORD` defined in the [db](#db) service.
- Then click save and you should see the `db` server under `Servers` in the `Object Explorer`.

Now if you look at `Databases`, you will see `compose-postgres` this is the Postgres database holding the `Person` and `Pet` tables for our Spring Boot API. Here you can explore the Postgres database and manage it using the admin UI.

### Import Mock Data

Before we can test the API and Kong gateway we need to populate the postgres database with data. The data we will use is located in [MOCK_DATA.csv](/microservice/MOCK_DATA.csv).

### Testing the Spring Boot API and Kong Gateway

- To test the Demo API, import the Postman requests JSON file [Microservice MVP.postman_collection.json](/microservice/Microservice%20MVP.postman_collection.json). This Postman Collection contains requests to perform CRUD on Person and Pet Entities.
