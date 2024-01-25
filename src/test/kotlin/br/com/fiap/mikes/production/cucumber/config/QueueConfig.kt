package br.com.fiap.mikes.production.cucumber.config

import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import software.amazon.awssdk.services.sqs.SqsClient
import software.amazon.awssdk.services.sqs.model.QueueAttributeName

@Component
class QueueConfig {

    @Value("\${spring.cloud.aws.sqs.queue-name}")
    private lateinit var listenerQueueName: String

    @Autowired
    private lateinit var sqsClient: SqsClient

    @PostConstruct
    fun createQueues() {
        sqsClient.createQueue {
            it.queueName(listenerQueueName).attributes(mapOf(QueueAttributeName.MAXIMUM_MESSAGE_SIZE to "256"))
        }
    }
}