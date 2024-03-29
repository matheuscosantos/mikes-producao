package br.com.fiap.mikes.production.infrastructure.configuration

import io.awspring.cloud.sns.core.TopicArnResolver
import io.awspring.cloud.sns.core.TopicsListingTopicArnResolver
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.sns.SnsClient
import software.amazon.awssdk.services.sqs.SqsAsyncClient
import software.amazon.awssdk.services.sqs.SqsClient
import java.net.URI

@Configuration
class AWSConfiguration {

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

    @Bean
    @Profile("!local && !test")
    fun sqsAsyncClientCloud(): SqsAsyncClient {
        return SqsAsyncClient.builder().build()
    }

    @Bean
    @Profile("local")
    fun snsClientLocal(): SnsClient {
        return SnsClient.builder()
            .endpointOverride(URI.create(endpoint))
            .region(Region.of(region))
            .build()
    }

    @Bean
    @Profile("!local && !test")
    fun snsClientCloud(): SnsClient {
        return SnsClient.builder().build()
    }

    @Bean
    fun topicArnResolver(snsClient: SnsClient): TopicArnResolver {
        return TopicsListingTopicArnResolver(snsClient)
    }
}