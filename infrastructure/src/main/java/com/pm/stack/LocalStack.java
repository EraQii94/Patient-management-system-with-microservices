package com.pm.stack;


import software.amazon.awscdk.*;
import software.amazon.awscdk.services.ec2.*;
import software.amazon.awscdk.services.ec2.InstanceType;
import software.amazon.awscdk.services.ecs.*;
import software.amazon.awscdk.services.ecs.Protocol;
import software.amazon.awscdk.services.ecs.patterns.ApplicationLoadBalancedFargateService;
import software.amazon.awscdk.services.logs.LogGroup;
import software.amazon.awscdk.services.logs.RetentionDays;
import software.amazon.awscdk.services.msk.CfnCluster;
import software.amazon.awscdk.services.rds.*;
import software.amazon.awscdk.services.route53.CfnHealthCheck;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


// A stack for local development and testing
public class LocalStack extends Stack {


    private final Vpc vpc;
    private final Cluster ecsCluster;


    public LocalStack(final App scope, final String id, final StackProps props) {

        super(scope, id, props); // Call the parent constructor from Stack class
        this.vpc = createVpc(); // Create a VPC for the stack

        DatabaseInstance authServiceDb =
                createDatabaseInstance("AuthServiceDb", "authdb"); // Create AuthService database instance
        DatabaseInstance patientServiceDb =
                createDatabaseInstance("PatientServiceDb", "patientdb"); // Create PatientService database instance

        CfnHealthCheck authHealthCheck =
                healthCheck(authServiceDb, "AuthServiceDbHealthCheck"); // Create health check for AuthService database
        CfnHealthCheck patientHealthCheck =
                healthCheck(patientServiceDb, "PatientServiceDbHealthCheck"); // Create health check for PatientService database

        CfnCluster mskCluster = createMskCluster(); // Create MSK cluster for Kafka

        this.ecsCluster = createEcsCluster(); // Create ECS cluster for Fargate services

        /// Create Fargate services to simulate microservices
        FargateService authService =
                createFargateService("AuthService",
                        "auth-service",
                        List.of(4005),
                        authServiceDb,
                        Map.of("JWT_SECRET", "p9J2R0aVxM7wLZ5Q9C4mXk8T0vFJwZ1bYcR2nH3E5sA=")
                );

        authService.getNode().addDependency(authHealthCheck); // Ensure health check is created before the service
        authService.getNode().addDependency(authServiceDb); // Ensure DB is created before the service


        /// Create other Fargate services
        FargateService billingService =
                createFargateService("BillingService",
                        "billing-service",
                        List.of(4001, 9001),
                        null,
                        null
                );

        /// Create Analytics and Patient services
        FargateService analyticsService =
                createFargateService("AnalyticsService",
                        "analytics-service",
                        List.of(4002),
                        null,
                        null);

        analyticsService.getNode().addDependency(mskCluster);

        /// Create Patient service
        FargateService patientService = createFargateService("PatientService",
                "patient-service",
                List.of(4000),
                patientServiceDb,
                Map.of(
                        "BILLING_SERVICE_ADDRESS", "host.docker.internal",
                        "BILLING_SERVICE_GRPC_PORT", "9001"
                ));
        patientService.getNode().addDependency(patientServiceDb);
        patientService.getNode().addDependency(patientHealthCheck);
        patientService.getNode().addDependency(billingService);
        patientService.getNode().addDependency(mskCluster);

        createApiGatewayService();
    }

    private void createApiGatewayService() {
        FargateTaskDefinition taskDefinition = FargateTaskDefinition.Builder
                .create(this, "ApiGatewayTaskDefinition")
                .cpu(256)
                .memoryLimitMiB(512)
                .build();

        ContainerDefinitionOptions containerOptions =
                ContainerDefinitionOptions.builder()
                        .image(ContainerImage.fromRegistry("api-gateway"))
                        .environment(Map.of(
                                "SPRING_PROFILES_ACTIVE", "prod",
                                "AUTH_SERVICE_URL", "http://host.docker.internal:4005"
                        ))
                        .portMappings(List.of(4004).stream()
                                .map(port -> PortMapping.builder()
                                        .containerPort(port)
                                        .hostPort(port)
                                        .protocol(Protocol.TCP)
                                        .build())
                                .toList())
                        .logging(LogDriver.awsLogs(AwsLogDriverProps.builder()
                                .logGroup(LogGroup.Builder.create(this, "ApiGatewayLogGroup")
                                        .logGroupName("/ecs/api-gateway")
                                        .removalPolicy(RemovalPolicy.DESTROY)
                                        .retention(RetentionDays.ONE_DAY)
                                        .build())
                                .streamPrefix("api-gateway")
                                .build()))
                        .build();

        taskDefinition.addContainer("ApiGatewayContainer", containerOptions);


        ApplicationLoadBalancedFargateService apiGateway =
                ApplicationLoadBalancedFargateService.Builder
                        .create(this,"APIGatewayService")
                        .cluster(ecsCluster)
                        .serviceName("api-gateway")
                        .taskDefinition(taskDefinition)
                        .desiredCount(1)
                        .healthCheckGracePeriod(Duration.seconds(60))
                        .build();

    }

    public static void main(final String[] args) {
        // Create the app and stack
        App app = new App(AppProps.builder().outdir("./cdk.out").build());
        // Use BootstraplessSynthesizer for local development
        StackProps props = StackProps.builder()
                .synthesizer(new BootstraplessSynthesizer())
                .build();

        // Create the LocalStack instance
        new LocalStack(app, "LocalStack", props);
        app.synth();

        System.out.println("app synthesized");
    }

    // Create a VPC
    private Vpc createVpc() {
        return Vpc.Builder
                .create(this, "PatientManagementVpc")
                .vpcName("PatientManagementVpc")
                .maxAzs(2) // Default is all AZs in region
                .build();
    }

    // Create a PostgreSQL database instance
    private DatabaseInstance createDatabaseInstance(String id, String dbName) {
        return DatabaseInstance.Builder
                .create(this, id)
                .engine(DatabaseInstanceEngine.postgres(
                        PostgresInstanceEngineProps.builder()
                                .version(PostgresEngineVersion.VER_17_2)
                                .build())) // Specify the database engine and version
                .vpc(this.vpc) // Specify the VPC where the database will be deployed
                .instanceType(InstanceType.of(InstanceClass.BURSTABLE2, InstanceSize.MICRO)) // Specify the instance type
                .allocatedStorage(20) // Specify the allocated storage in GB
                .credentials(Credentials.fromGeneratedSecret("admin")) // Generate admin credentials
                .databaseName(dbName) // Specify the initial database name
                .removalPolicy(RemovalPolicy.DESTROY) // Destroy the database if the stack was deleted, NOT recommended for production environments
                .build();
    }

    // Create a Route 53 health check for the database instance
    private CfnHealthCheck healthCheck(DatabaseInstance db, String id) {
        return CfnHealthCheck.Builder
                .create(this, id)
                .healthCheckConfig(CfnHealthCheck.HealthCheckConfigProperty.builder()
                        .type("TCP") // Use TCP health check for database
                        .port(Token.asNumber(db.getDbInstanceEndpointPort())) // Use the database port
                        .ipAddress(db.getDbInstanceEndpointAddress()) // Use the database endpoint address
                        .requestInterval(30) // Interval between health checks in seconds
                        .failureThreshold(3) // Number of consecutive failures before considering unhealthy
                        .build()
                )
                .build();
    }

    // Create an MSK cluster for Kafka
    private CfnCluster createMskCluster() {
        return CfnCluster.Builder
                .create(this, "MskCluster")
                .clusterName("kafka-cluster")
                .kafkaVersion("2.8.1")
                .numberOfBrokerNodes(1)
                .brokerNodeGroupInfo(
                        CfnCluster.BrokerNodeGroupInfoProperty.builder()
                                .instanceType("kafka.m5.xlarge")
                                .clientSubnets(vpc.getPrivateSubnets()
                                        .stream()
                                        .map(ISubnet::getSubnetId)
                                        .collect(Collectors.toList()))
                                .brokerAzDistribution("DEFAULT")
                                .build()
                ) // Configure broker node group info with VPC subnets
                .build();
    }

    // Create an ECS cluster
    private Cluster createEcsCluster() {
        return Cluster.Builder
                .create(this, "PatientManagementEcsCluster")
                .vpc(vpc)
                .defaultCloudMapNamespace(CloudMapNamespaceOptions.builder()
                        .name("patient-management.local")
                        .build()) // Optional: Service discovery namespace
                .build();
    }

    private FargateService createFargateService(String id,
                                                String imageName,
                                                List<Integer> ports,
                                                DatabaseInstance db,
                                                Map<String, String> environmentVariables) {
        FargateTaskDefinition taskDefinition = FargateTaskDefinition.Builder
                .create(this, id + "task")
                .cpu(256)
                .memoryLimitMiB(512)
                .build();

        // Add container to task definition
        ContainerDefinitionOptions.Builder containerOptions = ContainerDefinitionOptions.builder()
                .image(ContainerImage.fromRegistry(imageName))
                .portMappings(ports.stream()
                        .map(port -> PortMapping.builder()
                                .containerPort(port)
                                .hostPort(port)
                                .protocol(Protocol.TCP)
                                .build())
                        .toList())
                .logging(LogDriver.awsLogs(AwsLogDriverProps.builder()
                        .logGroup(LogGroup.Builder.create(this, id + "LogGroup")
                                .logGroupName("/ecs/" + imageName)
                                .removalPolicy(RemovalPolicy.DESTROY)
                                .retention(RetentionDays.ONE_DAY)
                                .build())
                        .streamPrefix(imageName)
                        .build())
                );

        // Add environment variables for database connection
        Map<String, String> envVars = new HashMap<>();
        envVars.put("SPRING_KAFKA_BOOTSTRAP_SERVERS", "localhost.localstack.cloud:4510, localhost.localstack.cloud:4511, localhost.localstack.cloud:4512");

        if (environmentVariables != null) {
            envVars.putAll(environmentVariables);
        }

        if (db != null) {
            envVars.put("SPRING_DATASOURCE_URL", "jdbc:postgresql://%s:%s/%s-db".formatted(
                    db.getDbInstanceEndpointAddress(),
                    db.getDbInstanceEndpointPort(),
                    imageName
            ));
            envVars.put("SPRING_DATASOURCE_USERNAME", "admin");
            envVars.put("SPRING_DATASOURCE_PASSWORD",
                    db.getSecret().secretValueFromJson("password").toString());
            envVars.put("SPRING_JPA_HIBERNATE_DDL_AUTO", "update");
            envVars.put("SPRING_SQL_INIT_MODE", "always");
            envVars.put("SPRING_DATASOURCE_HIKARI_INITIALIZATION_FAIL_TIMEOUT", "60000"); // Increase timeout for DB connection
        }

        containerOptions.environment(envVars);
        taskDefinition.addContainer(imageName + "Container", containerOptions.build()); // Add container to task definition

        // Create the Fargate service
        return FargateService.Builder
                .create(this, id)
                .cluster(this.ecsCluster)
                .taskDefinition(taskDefinition) // Set the task definition
                .assignPublicIp(false) // Do not assign public IP
                .build();
    }

}
