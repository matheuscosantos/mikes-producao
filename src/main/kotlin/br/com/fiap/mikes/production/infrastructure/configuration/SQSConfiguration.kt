package br.com.fiap.mikes.production.infrastructure.configuration

import io.awspring.cloud.sqs.config.SqsMessageListenerContainerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.sqs.SqsAsyncClient
import java.net.URI

@Configuration
class SQSConfiguration {

    @Value("\${spring.cloud.aws.region}")
    private lateinit var region: String

    @Value("\${spring.cloud.aws.endpoint}")
    private lateinit var endpoint: String

    @Bean
    @Profile("local")
    fun sqsAsyncClientLocal(): SqsAsyncClient {
        return SqsAsyncClient.builder()
            .endpointOverride(URI.create(endpoint))
            .region(Region.of(region))
            .build()
    }

    @Bean
    @Profile("!local")
    fun sqsAsyncClientCloud(): SqsAsyncClient {
        return SqsAsyncClient.builder().build()
    }

    @Bean
    fun defaultSqsListenerContainerFactory(sqsAsyncClient: SqsAsyncClient): SqsMessageListenerContainerFactory<Any> {
        return SqsMessageListenerContainerFactory
            .builder<Any>()
            .sqsAsyncClient(sqsAsyncClient)
            .build()
    }
}
