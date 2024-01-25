package br.com.fiap.mikes.production.cucumber.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.testcontainers.containers.localstack.LocalStackContainer
import org.testcontainers.utility.DockerImageName
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.sns.SnsClient
import software.amazon.awssdk.services.sqs.SqsAsyncClient
import software.amazon.awssdk.services.sqs.SqsClient

@Configuration
class LocalStackContainerConfiguration {

    @Bean
    fun localStackContainer(): LocalStackContainer {
        val localStackContainer = LocalStackContainer(DockerImageName.parse("localstack/localstack:3.0.2"))
            .withServices(LocalStackContainer.Service.SNS, LocalStackContainer.Service.SQS)

        localStackContainer.start()

        return localStackContainer
    }

    @Bean
    @Primary
    fun snsClient(localStackContainer: LocalStackContainer): SnsClient {
        return SnsClient.builder()
            .endpointOverride(localStackContainer.getEndpointOverride(LocalStackContainer.Service.SNS))
            .region(Region.of(localStackContainer.region))
            .build()
    }

    @Bean
    @Primary
    fun sqsClient(localStackContainer: LocalStackContainer): SqsClient {
        return SqsClient.builder()
            .endpointOverride(localStackContainer.getEndpointOverride(LocalStackContainer.Service.SQS))
            .region(Region.of(localStackContainer.region))
            .credentialsProvider {
                AwsBasicCredentials.create(
                    localStackContainer.accessKey,
                    localStackContainer.secretKey
                )
            }
            .build()
    }

    @Bean
    fun sqsAsyncClient(localStackContainer: LocalStackContainer): SqsAsyncClient {
        return SqsAsyncClient.builder()
            .endpointOverride(localStackContainer.getEndpointOverride(LocalStackContainer.Service.SQS))
            .region(Region.of(localStackContainer.region))
            .credentialsProvider {
                AwsBasicCredentials.create(
                    localStackContainer.accessKey,
                    localStackContainer.secretKey
                )
            }
            .build()
    }
}