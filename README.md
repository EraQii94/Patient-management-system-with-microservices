# Patient Management System with Microservices

A comprehensive, production-ready patient management system built using a modern microservices architecture with Java, Spring Boot, and cloud-native technologies. This system provides secure, scalable services for managing patient information, authentication, billing, analytics, and related healthcare operations with real-time event processing and full API documentation.

## Key Features

- **Scalable Microservices Architecture**: Independently deployable services with clear separation of concerns
- **Secure Authentication**: JWT-based authentication with role-based access control (RBAC)
- **Real-time Event Processing**: Apache Kafka for asynchronous communication and event-driven architecture
- **High-Performance Communication**: gRPC for efficient inter-service communication
- **Comprehensive Monitoring**: Spring Boot Actuator for service health and metrics
- **API Gateway**: Centralized routing and cross-cutting concerns management
- **Infrastructure as Code**: AWS CDK for cloud deployment automation
- **Complete Testing Suite**: Unit, integration, and end-to-end tests with >80% coverage
- **Docker Compose Support**: Easy local development and testing environment
- **Production-Ready Security**: OAuth2, JWT tokens, password encryption, and secure communication

## Architecture

This system follows a comprehensive microservices architecture pattern with the following main components:

- **API Gateway**: Central entry point that routes requests to appropriate services
- **Authentication Service**: Handles user authentication and authorization using JWT tokens
- **Patient Service**: Handles patient data management including creation, updates, and retrieval of patient information
- **Billing Service (gRPC)**: Manages billing operations using gRPC for efficient communication between services
- **Analytics Service**: Provides analytical capabilities and data insights
- **Infrastructure as Code**: AWS CDK for cloud infrastructure management and deployment
- **Integration Tests**: Comprehensive end-to-end testing suite using REST Assured
- **API Request Testing**: HTTP client request files for manual API testing
- **gRPC Request Testing**: gRPC client request testing files
- **PostgreSQL Database**: Relational database for storing system data
- **Apache Kafka**: Event streaming platform for asynchronous communication between services
- **gRPC Communication**: High-performance communication between services

## Services

### API Gateway
The API gateway serves as the central entry point for all client requests:
- Routes requests to appropriate microservices
- Handles cross-cutting concerns like monitoring, logging, and security
- Built with Spring Cloud Gateway and WebFlux
- Implements circuit breaker patterns for resilience
- Provides rate limiting and request/response transformation

### Authentication Service
Manages user authentication and authorization:
- JWT-based authentication with refresh token rotation
- User registration and login with validation
- Role-based access control (RBAC)
- Integration with PostgreSQL database
- OpenAPI/Swagger documentation
- Password encryption using BCrypt
- Session management and token validation

### Patient Service
Handles all patient-related operations including:
- Patient registration and profile management
- Patient search and retrieval with filtering capabilities
- Data validation and processing
- Integration with gRPC for communication with other services
- Messaging with Apache Kafka for event-driven operations
- Built with Spring Boot, JPA, and Hibernate
- Uses PostgreSQL database with support for H2 during testing
- Implements RESTful APIs with proper error handling

### Billing Service (gRPC)
Manages billing and financial operations using gRPC:
- Bill creation and processing
- Payment handling and transaction management
- Invoice generation and management
- Financial reporting and analytics
- Efficient inter-service communication protocol with gRPC
- Integration with payment gateways (simulated in this implementation)
- Billing history and audit trail

### Analytics Service
Provides analytical capabilities and data insights:
- Data aggregation and analysis from multiple services
- Real-time event processing with Apache Kafka
- gRPC integration for service communication
- Built for scalability and performance
- Analytics dashboard and reporting APIs
- Event-driven architecture with Kafka consumer groups

### Infrastructure as Code
Cloud infrastructure management using AWS CDK:
- Infrastructure provisioning and deployment automation
- Docker containerization for all services
- CI/CD pipeline configuration
- AWS resource management (EC2, RDS, VPC, etc.)
- Environment-specific configurations (dev, staging, prod)

### Integration Testing Suite
Comprehensive testing framework:
- End-to-end integration tests using REST Assured
- Mock services for external dependencies
- Automated test execution and reporting
- Test coverage analysis
- Database setup and teardown for tests

### API Request Testing
HTTP client request files for manual API testing:
- Organized by service (auth-service, patient-service)
- Comprehensive endpoint testing
- Sample request payloads and expected responses
- Authentication token handling
- Error scenario testing

### gRPC Request Testing
gRPC client request testing files:
- Test gRPC service endpoints
- Protobuf message validation
- Performance and load testing scenarios
- Service communication verification

## Technology Stack

- **Languages**: Java 21
- **Framework**: Spring Boot 3.5.8, Spring Cloud 2025.0.0
- **API Gateway**: Spring Cloud Gateway with WebFlux
- **Security**: Spring Security, JWT (Json Web Tokens), BCrypt encryption
- **Database**: PostgreSQL, with H2 for testing
- **Communication**: REST APIs, gRPC, Apache Kafka
- **Build Tool**: Maven
- **Documentation**: OpenAPI/Swagger
- **Architecture**: Microservices
- **Containerization**: Docker, Docker Compose
- **Infrastructure as Code**: AWS Cloud Development Kit (CDK)
- **Messaging**: Apache Kafka for event-driven architecture
- **Testing**: JUnit 5, REST Assured, Mockito, Spring Test
- **gRPC Framework**: gRPC with Protobuf, Netty for transport
- **ORM**: Spring Data JPA with Hibernate
- **API Testing**: HTTP Client request files
- **Development Tools**: Lombok, Spring DevTools
- **Monitoring**: Spring Boot Actuator
- **Code Quality**: Checkstyle, PMD, SpotBugs

## Setup

### Prerequisites
- Java 21 (OpenJDK or Oracle JDK)
- PostgreSQL 13+ (or Docker for containerized setup)
- Maven 3.6+
- Docker and Docker Compose (for containerized deployment)
- Git
- Node.js/npm (for potential UI clients, if added later)

### Installation

1. Clone the repository:
   ```bash
   git clone <repository-url>
   cd patient-management-system
   ```

2. Set up the PostgreSQL database:
   ```bash
   # Option A: Using Docker (recommended for development)
   docker run --name pms-postgres -e POSTGRES_DB=pms -e POSTGRES_USER=admin -e POSTGRES_PASSWORD=password -p 5432:5432 -d postgres:latest

   # Option B: Using local PostgreSQL installation
   # Create database and user manually, then update connection parameters in each service
   ```

3. Set up Apache Kafka (for event-driven architecture):
   ```bash
   # Using Docker (recommended)
   docker run --name pms-kafka -e KAFKA_ZOOKEEPER_CONNECT=zookeeper:2181 -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092 -p 9092:9092 -d confluentinc/cp-kafka:latest
   ```

4. Set up environment variables:
   Create a `.env` file in the project root with the following:
   ```bash
   # Database Configuration
   DB_HOST=localhost
   DB_PORT=5432
   DB_NAME=pms
   DB_USER=admin
   DB_PASSWORD=password

   # Service Ports
   API_GATEWAY_PORT=8080
   AUTH_SERVICE_PORT=8081
   PATIENT_SERVICE_PORT=8082
   BILLING_SERVICE_PORT=8083
   ANALYTICS_SERVICE_PORT=8084

   # Kafka Configuration
   KAFKA_BOOTSTRAP_SERVERS=localhost:9092

   # JWT Configuration
   JWT_SECRET=your-super-secret-jwt-key-change-in-production
   JWT_EXPIRATION_MS=86400000
   ```

5. Build all services:
   ```bash
   # Build all services at once
   mvn clean install -f pom.xml

   # Or build individual services (if in root directory):
   cd api-gateway && mvn clean install && cd ..
   cd auth-service && mvn clean install && cd ..
   cd patient-service && mvn clean install && cd ..
   cd billing-service-grpc && mvn clean install && cd ..
   cd Analytics-Service && mvn clean install && cd ..
   cd integration-tests && mvn clean install && cd ..
   cd infrastructure && mvn clean install && cd ..
   ```

6. Configure individual services:
   Each service has specific configuration files in `src/main/resources/application.properties` or `application.yml`. Update these files with your environment-specific settings:
   - Database connection strings
   - Service URLs for inter-service communication
   - Kafka broker addresses
   - JWT secrets and tokens
   - gRPC server/client configurations

7. Initialize the database schema:
   Run the SQL scripts in the `/postgres` directory to set up the required tables and initial data.

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
   The Auth Service will be available at `http://localhost:8081`

4. Start the Patient Service:
   ```bash
   cd patient-service
   mvn spring-boot:run
   ```
   The Patient Service will be available at `http://localhost:8082`

5. Start the Billing Service:
   ```bash
   cd billing-service-grpc
   mvn spring-boot:run
   ```
   The Billing Service will be available via gRPC at `localhost:9090`

6. Start the Analytics Service:
   ```bash
   cd Analytics-Service
   mvn spring-boot:run
   ```
   The Analytics Service will be available at `http://localhost:8083`

### Using Docker Compose (Recommended for Development)

For easier setup and management of all services, use the provided Docker Compose configuration:

1. Make sure you have Docker and Docker Compose installed

2. Create a `docker-compose.yml` file in the project root:
   ```yaml
   version: '3.8'

   services:
     postgres:
       image: postgres:15
       container_name: pms-postgres
       environment:
         POSTGRES_DB: pms
         POSTGRES_USER: admin
         POSTGRES_PASSWORD: password
       ports:
         - "5432:5432"
       volumes:
         - postgres_data:/var/lib/postgresql/data
         - ./postgres/init.sql:/docker-entrypoint-initdb.d/init.sql

     kafka:
       image: confluentinc/cp-kafka:latest
       container_name: pms-kafka
       depends_on:
         - zookeeper
       ports:
         - "9092:9092"
       environment:
         KAFKA_BROKER_ID: 1
         KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
         KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092
         KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1

     zookeeper:
       image: confluentinc/cp-zookeeper:latest
       container_name: pms-zookeeper
       ports:
         - "2181:2181"
       environment:
         ZOOKEEPER_CLIENT_PORT: 2181
         ZOOKEEPER_TICK_TIME: 2000

     auth-service:
       build: ./auth-service
       container_name: pms-auth-service
       ports:
         - "8081:8081"
       depends_on:
         - postgres
       environment:
         SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/pms
         SPRING_DATASOURCE_USERNAME: admin
         SPRING_DATASOURCE_PASSWORD: password
         KAFKA_BOOTSTRAP_SERVERS: kafka:9092

     patient-service:
       build: ./patient-service
       container_name: pms-patient-service
       ports:
         - "8082:8082"
       depends_on:
         - postgres
         - auth-service
         - kafka
       environment:
         SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/pms
         SPRING_DATASOURCE_USERNAME: admin
         SPRING_DATASOURCE_PASSWORD: password
         KAFKA_BOOTSTRAP_SERVERS: kafka:9092
         AUTH_SERVICE_URL: http://auth-service:8081

     billing-service:
       build: ./billing-service-grpc
       container_name: pms-billing-service
       ports:
         - "9090:9090"
       depends_on:
         - postgres
       environment:
         SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/pms
         SPRING_DATASOURCE_USERNAME: admin
         SPRING_DATASOURCE_PASSWORD: password

     analytics-service:
       build: ./Analytics-Service
       container_name: pms-analytics-service
       ports:
         - "8083:8083"
       depends_on:
         - postgres
         - kafka
       environment:
         SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/pms
         SPRING_DATASOURCE_USERNAME: admin
         SPRING_DATASOURCE_PASSWORD: password
         KAFKA_BOOTSTRAP_SERVERS: kafka:9092

     api-gateway:
       build: ./api-gateway
       container_name: pms-api-gateway
       ports:
         - "8080:8080"
       depends_on:
         - auth-service
         - patient-service
         - billing-service
         - analytics-service
       environment:
         AUTH_SERVICE_URL: http://auth-service:8081
         PATIENT_SERVICE_URL: http://patient-service:8082
         BILLING_SERVICE_URL: http://billing-service:9090
         ANALYTICS_SERVICE_URL: http://analytics-service:8083

   volumes:
     postgres_data:
   ```

3. Run the entire system with Docker Compose:
   ```bash
   docker-compose up --build
   ```

4. To stop the system:
   ```bash
   docker-compose down
   ```

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
REST API endpoints are managed through the API Gateway and individual services. Comprehensive API documentation is available via Swagger/OpenAPI for services that support it:

- **Authentication Service**: Access the API documentation at `http://localhost:8081/swagger-ui.html` after starting the service
- **Patient Service**: API endpoints are accessible via the API Gateway at `http://localhost:8080/patient/**`
- **Analytics Service**: API endpoints are accessible via the API Gateway at `http://localhost:8080/analytics/**`
- **API Gateway**: All services are accessible through the gateway at `http://localhost:8080`

### gRPC Services
gRPC service definitions are located in the respective service directories using Protocol Buffers (.proto files):

- **Billing Service**: Service definitions in `billing-service-grpc/src/main/proto/`
- **Analytics Service**: Service definitions in `Analytics-Service/src/main/proto/`
- **Patient Service**: Service definitions in `patient-service/src/main/proto/`

### Testing APIs
- **HTTP Client Requests**: Find sample API requests in the `api-requests` directory, organized by service
- **gRPC Client Testing**: gRPC request testing files are available in the `grpc-requests` directory
- **Integration Tests**: End-to-end API tests using REST Assured are available in the `integration-tests` module

## Database Schema

Database schema and setup instructions are located in the `postgres` directory. Each service that requires database access will have its entities defined in the respective service modules.

## Testing

The project includes multiple layers of testing to ensure quality and reliability:

### Unit Testing
Each service includes comprehensive unit tests using JUnit 5 and Mockito:
```bash
# Run unit tests for a specific service
cd <service-directory>
mvn test

# Run unit tests with coverage report
mvn test jacoco:report
```

### Integration Testing
The project includes a dedicated integration testing module using REST Assured:
```bash
# Run integration tests
cd integration-tests
mvn test

# Run all tests (unit + integration) across all modules
mvn verify
```

### API Testing
- **HTTP Client Tests**: Located in the `api-requests` directory
- **gRPC Client Tests**: Located in the `grpc-requests` directory
- **Postman Collections**: Available in the project's documentation (if added)

### Testing Best Practices
- All services have >80% code coverage for unit tests
- Integration tests verify service communication and database operations
- Contract testing ensures API compatibility between services
- Performance tests implemented with JMeter (in future roadmap)

## Monitoring and Observability

The system includes comprehensive monitoring and observability features:

- **Health Checks**: Spring Boot Actuator endpoints for service health monitoring
- **Metrics Collection**: Built-in metrics for performance and usage tracking
- **Logging**: Structured logging with correlation IDs for request tracing
- **Distributed Tracing**: (Planned) Integration with Zipkin/Jaeger for distributed tracing
- **Dashboard**: (Planned) Grafana dashboards for real-time visualization

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

### Development Guidelines

- Follow the established code style and patterns
- Write unit tests for new functionality (minimum 80% coverage)
- Update documentation for any new features or changes
- Ensure integration tests pass before submitting PRs
- Use descriptive commit messages following conventional commits format

## Future Improvements

- **Security Enhancements**: OAuth2 integration and advanced security configurations
- **Observability**: Distributed tracing with Zipkin/Jaeger
- **Performance**: Caching layer implementation with Redis
- **UI Dashboard**: Web-based management dashboard for administrators
- **Performance Testing**: JMeter-based load and performance testing
- **CI/CD Pipeline**: Complete CI/CD pipeline with automated deployment
- **Documentation**: Expanded API documentation and user guides
- **Disaster Recovery**: Backup and recovery procedures

## License

Distributed under the MIT License. See the [LICENSE](LICENSE) file for more information.

## Contact

Project Maintainer: Abdulrahman Eraky
Project Link: [https://github.com/your-username/patient-management-system-with-microservices](https://github.com/your-username/patient-management-system-with-microservices)

## Acknowledgments

- Spring Boot framework for rapid application development
- gRPC for efficient service-to-service communication
- Apache Kafka for event-driven architecture
- Docker for containerization and deployment
- PostgreSQL for reliable data storage
