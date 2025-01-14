# Incident Service

## Overview
The Incident Service is a key component of the support automation system, designed to manage and process user-reported incidents. This microservice enables functionalities like creating, viewing, editing, and deleting incidents, along with assigning priorities, categories, and responsible departments.

## Features
- Create, view, edit, and delete incidents.
- Assign categories, statuses, priorities, and analysts to incidents.
- Notify administrators of new incidents and handle asynchronous image deletion via Kafka.
- Integrate seamlessly with other microservices such as the User Service and Image Service.
- Authenticate with JWT Bearer.

## Technologies Used
- **Java Development Kit (JDK) 21+**
- **Spring Boot** for core application framework.
- **PostgreSQL** as the relational database.
- **Liquibase** for database migration.
- **Lombok** and **Mapstruct** for reducing boilerplate code.
- **Kafka** for asynchronous communication.
- **Kafka UI** for message management.
- **gRPC** for synchronous communication.
- **Spring Data** for database interactions.
- **Spring Security** for authentication and authorization.
- **Keycloak** for authentication and JWT token generation.
- **Docker** and **Docker Compose** for containerization and orchestration.
- **Swagger** for REST API documentation.


## Getting Started

### Requirements
- Docker and Docker Compose installed.
- Java Development Kit (JDK) 21 or higher.
- PostgreSQL database.
- Kafka and Kafka UI for message management.
- Keycloak for authentication and JWT token generation.

### Setup Instructions

#### 1. Clone the repository

Clone this repository to your local machine:

```bash
git clone https://github.com/br0mberg/SupportDesk-IncidentService
cd SupportDesk-IncidentService
```

#### 2. Configure your application.properties
Make sure to set up the following properties in your application.properties 
file located in src/main/resources/.

### 3. Set up Docker Compose
Use the following docker-compose.yml to configure and start your containers.
```bash
docker-compose up --build
```
This will start the Incident Service, Kafka, Postgres, and all other required services containers.

### 4. Access the services
- The Incident Service API will be accessible at http://localhost:8081.
- Kafka UI will be available at http://localhost:8383 for managing Kafka messages.
- Keycloak will be accessible at http://localhost:8080 for user authentication.
- Swagger UI will be accessible at http://localhost:8081/swagger-ui.html for api doc.

For more details on available endpoints, please refer to the Swagger documentation at http://localhost:8081/swagger-ui.html.

Happy using! 🎉

Let me know if you need any more adjustments!
