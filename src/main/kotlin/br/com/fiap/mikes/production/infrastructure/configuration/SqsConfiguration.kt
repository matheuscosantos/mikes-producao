package br.com.fiap.mikes.production.infrastructure.configuration

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.sqs.SqsClient
import java.net.URI

@Configuration
class SqsConfiguration {

    @Value("\${spring.cloud.aws.region}")
    private lateinit var region: String

    @Value("\${spring.cloud.aws.endpoint}")
    private lateinit var endpoint: String

    @Bean
    @Profile("local")
    fun sqsClientLocal(): SqsClient {
        return SqsClient.builder()
            .endpointOverride(URI.create(endpoint))
            .region(Region.of(region))
            .build()
    }

    @Bean
    @Profile("!local && !test")
    fun sqsClientCloud(): SqsClient {
        return SqsClient.builder().build()
    }
}