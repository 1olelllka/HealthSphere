# Healthsphere

HealthSphere is a web-based application that allows patients to make appointments to specific doctors, view their medical
records, which may include prescriptions and further details.
Doctors can manage specific medical records, prescriptions, book
an appointment for specific patients. This application is
open-source and is not intended to be used in a real-world
environment for personal medical records.

## Tech Stack

**Client:** React, Redux, TailwindCSS

**Server:** Spring Boot

**Database:** PostgreSQL

**Cache:** Redis

**Search Engine:** Elasticsearch

**Message Broker:** RabbitMQ

## Installation

After cloning the repository, you'll need to configure several .env files.

#### Client Setup

In the **client** folder, add a .env file with the following content:

```env
VITE_SERVER_URL=http://localhost:8000 # Default url
```

#### Server Setup

In the **server** folder, add a .env file with the following content:

```env
# Application Configuration
SPRING_APPLICATION_NAME=HealthSphere_Backend

# DataSource Configuration
SPRING_DATASOURCE_DRIVER_CLASS_NAME=org.postgresql.Driver
SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/healthsphere
SPRING_DATASOURCE_USERNAME=<your_db_username> # !!! CHANGE IT
SPRING_DATASOURCE_PASSWORD=<your_db_password> # !!! CHANGE IT

# JPA Configuration
SPRING_JPA_HIBERNATE_DDL_AUTO=update
SPRING_JPA_SHOW_SQL=true
SPRING_JPA_PROPERTIES_HIBERNATE_FORMAT_SQL=true

# Elasticsearch Configuration (default)
SPRING_ELASTICSEARCH_URIS=http://elasticsearch:9200

# RabbitMQ Configuration (default)
SPRING_RABBITMQ_HOST=rabbitmq
SPRING_RABBITMQ_PORT=5672
SPRING_RABBITMQ_USERNAME=<your_rabbitmq_username> # !!! CHANGE IT
SPRING_RABBITMQ_PASSWORD=<your_rabbitmq_password> # !!! CHANGE IT

# Redis Configuration
SPRING_REDIS_HOST=redis
SPRING_REDIS_PORT=6379

# Cache Configuration
SPRING_CACHE_TYPE=redis

# API Documentation
SPRING_API_DOCS_ENABLED=true

# Server Configuration
SERVER_PORT=8000
SERVER_ADDRESS=0.0.0.0

# Management Configuration
MANAGEMENT_ENDPOINTS_WEB_CORS_ALLOWED_ORIGINS=http://localhost:3000
MANAGEMENT_ENDPOINTS_WEB_CORS_ALLOWED_METHODS=GET,POST,PUT,PATCH,DELETE,OPTIONS
MANAGEMENT_ENDPOINTS_WEB_CORS_ALLOW_CREDENTIALS=true

# SpringDoc Configuration
SPRINGDOC_SHOW_ACTUATOR=true
SPRINGDOC_SWAGGER_UI_DISABLE_SWAGGER_DEFAULT_URL=true
SPRINGDOC_SWAGGER_UI_PATH=/apiDocs

# JWT_SECRET (Change if needed, it's random letters in base64)
JWT_SECRET=f5++WN/baVl8/OsIbD+9g/bpLwLR9QbBXIDpxbl0Jws=
```

In the same server folder, add the following files:

- .env.postgres

```env
POSTGRES_DB=healthsphere
POSTGRES_PASSWORD=<your_db_password> # CHANGE IT
POSTGRES_USER=<your_db_username> # CHANGE IT
```

- .env.elasticsearch

```env
ELASTIC_PASSWORD=secret # Default
discovery.type=single-node
xpack.security.enabled=false
```

- .env.rabbitmq

```env
RABBITMQ_DEFAULT_PASS=<your_rabbitmq_password> # !!! CHANGE IT
RABBITMQ_DEFAULT_USER=<your_rabbitmq_username> # !!! CHANGE IT
```

#### Running the application

After setting up the .env files, you can run the application with Docker:

```bash
  docker-compose up
```

or

```bash
  docker compose up
```

If you did everything by instruction, you can access the site by localhost:3000 on your machine.

**After the installation the sample data will be added automatically**

## Documentation

In order to access the documentation, you should have your application up and running. **_Ensure Swagger is enabled in your configuration to access API documentation_** Type in your browser:

```url
http://localhost:8000/apiDocs
```

You will find all of the API endpoints there.

## Disclaimer

This project, HealthSphere, was created solely for educational purposes. It is a demonstration of a health management system and is not intended for use in production environments or for managing real-world medical records. Use this software at your own risk, as it has not undergone the rigorous testing and security audits required for handling sensitive medical data in a live setting.

## License

[Apache License 2.0](https://choosealicense.com/licenses/apache-2.0/)
