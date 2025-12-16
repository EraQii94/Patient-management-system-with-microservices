# Patient Management System with Microservices

A comprehensive patient management system built using a modern microservices architecture. This system provides various services for managing patient information, authentication, billing, analytics, and related healthcare operations.

## Architecture

This system follows a microservices architecture pattern with the following main components:

- **API Gateway**: Central entry point that routes requests to appropriate services
- **Authentication Service**: Handles user authentication and authorization using JWT tokens
- **Patient Service**: Handles patient data management including creation, updates, and retrieval of patient information
- **Billing Service (gRPC)**: Manages billing operations using gRPC for efficient communication between services
- **Analytics Service**: Provides analytical capabilities and data insights
- **PostgreSQL Database**: Relational database for storing system data
- **Apache Kafka**: Event streaming platform for asynchronous communication between services
- **gRPC Communication**: High-performance communication between services

## Services

### API Gateway
The API gateway serves as the central entry point for all client requests:
- Routes requests to appropriate microservices
- Handles cross-cutting concerns like monitoring, logging, and security
- Built with Spring Cloud Gateway and WebFlux

### Authentication Service
Manages user authentication and authorization:
- JWT-based authentication
- User registration and login
- Role-based access control
- Integration with PostgreSQL database
- OpenAPI/Swagger documentation

### Patient Service
Handles all patient-related operations including:
- Patient registration and profile management
- Patient search and retrieval
- Data validation and processing
- Integration with gRPC for communication with other services
- Messaging with Apache Kafka
- Built with Spring Boot, JPA, and Hibernate
- Uses PostgreSQL database with support for H2 during testing

### Billing Service (gRPC)
Manages billing and financial operations using gRPC:
- Bill creation and processing
- Payment handling
- Invoice management
- Financial reporting
- Efficient inter-service communication protocol

### Analytics Service
Provides analytical capabilities and data insights:
- Data aggregation and analysis
- Real-time event processing with Apache Kafka
- gRPC integration for service communication
- Built for scalability and performance

## Technology Stack

- **Languages**: Java 21
- **Framework**: Spring Boot 3.5.8, Spring Cloud 2025.0.0
- **API Gateway**: Spring Cloud Gateway with WebFlux
- **Security**: Spring Security, JWT (Json Web Tokens)
- **Database**: PostgreSQL, with H2 for testing
- **Communication**: REST APIs, gRPC, Apache Kafka
- **Build Tool**: Maven
- **Documentation**: OpenAPI/Swagger
- **Architecture**: Microservices
- **Containerization**: Docker
- **Messaging**: Apache Kafka for event-driven architecture

## Setup

### Prerequisites
- Java 21
- PostgreSQL (or Docker for containerized setup)
- Maven 3.6+
- Docker (optional, for containerized deployment)

### Installation

1. Clone the repository:
   ```bash
   git clone <repository-url>
   cd patient-management-system
   ```

2. Set up the PostgreSQL database:
   ```bash
   # Navigate to the postgres directory and follow setup instructions
   cd postgres
   # Follow the database setup instructions in the postgres directory
   # Or use Docker to run PostgreSQL:
   docker run --name pms-postgres -e POSTGRES_DB=pms -e POSTGRES_USER=admin -e POSTGRES_PASSWORD=password -p 5432:5432 -d postgres:latest
   ```

3. Build all services:
   ```bash
   # Build the API Gateway
   cd ../api-gateway
   mvn clean install

   # Build the Auth Service
   cd ../auth-service
   mvn clean install

   # Build the Patient Service
   cd ../patient-service
   mvn clean install

   # Build the Billing Service
   cd ../billing-service-grpc
   mvn clean install

   # Build the Analytics Service
   cd ../Analytics-Service
   mvn clean install
   ```

4. Configure environment variables:
   Each service typically requires specific environment variables for database connections, service URLs, etc. Check the individual service directories for specific configuration files (application.properties or application.yml).

## Running the Application

### Starting Services (Development Mode)

For local development, you can run services individually:

1. Start the PostgreSQL database (if not using Docker)

2. Start the API Gateway:
   ```bash
   cd api-gateway
   mvn spring-boot:run
   ```
   The API Gateway will be available at `http://localhost:8080`

3. Start the Authentication Service:
   ```bash
   cd auth-service
   mvn spring-boot:run
   ```
   The Auth Service will be available at `http://localhost:{port}` (typically 8081)

4. Start the Patient Service:
   ```bash
   cd patient-service
   mvn spring-boot:run
   ```
   The Patient Service will be available at `http://localhost:{port}` (typically 8082)

5. Start the Billing Service:
   ```bash
   cd billing-service-grpc
   mvn spring-boot:run
   ```
   The Billing Service will be available via gRPC (typically port 9090)

6. Start the Analytics Service:
   ```bash
   cd Analytics-Service
   mvn spring-boot:run
   ```
   The Analytics Service will be available at `http://localhost:{port}` (typically 8083)

### Using Docker (Production-like Environment)

Each service contains a Dockerfile for containerization. Build and run using:

```bash
# Build all service images
docker build -t pms/api-gateway ./api-gateway
docker build -t pms/auth-service ./auth-service
docker build -t pms/patient-service ./patient-service
docker build -t pms/billing-service-grpc ./billing-service-grpc
docker build -t pms/analytics-service ./Analytics-Service

# Run containers (after setting up network and database)
docker network create pms-network

# Run PostgreSQL
docker run --name pms-postgres --network pms-network -e POSTGRES_DB=pms -e POSTGRES_USER=admin -e POSTGRES_PASSWORD=password -p 5432:5432 -d postgres:latest

# Run services
docker run --name pms-auth --network pms-network -e SPRING_DATASOURCE_URL=jdbc:postgresql://pms-postgres:5432/pms -d pms/auth-service
docker run --name pms-patient --network pms-network -e SPRING_DATASOURCE_URL=jdbc:postgresql://pms-postgres:5432/pms -d pms/patient-service
# ... similar for other services
```

## API Documentation

### REST APIs
REST API endpoints are managed through the API Gateway and individual services. Swagger/OpenAPI documentation is available for services that use it (such as the auth-service).

### gRPC Services
gRPC service definitions are located in the respective service directories using Protocol Buffers (.proto files).

## Database Schema

Database schema and setup instructions are located in the `postgres` directory. Each service that requires database access will have its entities defined in the respective service modules.

## Testing

To run the unit and integration tests:
```bash
# Run tests for a specific service
cd <service-directory>
mvn test

# Run tests for all services
mvn test
```

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

Distributed under the MIT License. See `LICENSE` file for more information.

## Contact

Project Maintainer: [Your Name/Team Name]
Project Link: [https://github.com/your-username/patient-management-system-with-microservices](https://github.com/your-username/patient-management-system-with-microservices)

## Acknowledgments

- Spring Boot framework for rapid application development
- gRPC for efficient service-to-service communication
- Apache Kafka for event-driven architecture
- Docker for containerization and deployment
- PostgreSQL for reliable data storage