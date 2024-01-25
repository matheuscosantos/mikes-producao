package br.com.fiap.mikes.production.cucumber

import br.com.fiap.mikes.production.ProductionApplication
import br.com.fiap.mikes.production.adapter.outbound.database.jpa.ProductHistoryJpaRepository
import io.cucumber.spring.CucumberContextConfiguration
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.ActiveProfiles
import software.amazon.awssdk.services.sns.SnsClient
import software.amazon.awssdk.services.sqs.SqsClient
import software.amazon.awssdk.services.sqs.model.QueueAttributeName

@ActiveProfiles("test")
@SpringBootTest(classes = [ProductionApplication::class], webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@CucumberContextConfiguration
class CucumberTest {

    @LocalServerPort
    internal var port: Int? = 0

    @Autowired
    internal lateinit var snsClient: SnsClient

    @Autowired
    internal lateinit var sqsClient: SqsClient

    @Autowired
    internal lateinit var repository: ProductHistoryJpaRepository

    @Value("\${spring.cloud.aws.sqs.queue-name}")
    internal lateinit var listenerQueueName: String

    @Value("\${spring.cloud.aws.sqs.queue-name2}")
    internal lateinit var receivedStatusQueueName: String

    @Value("\${spring.cloud.aws.sns.topic-name}")
    internal lateinit var topicName: String

    internal var topicArn: String = ""
    internal var queueUrlListenerQueue: String = ""
    internal var queueUrlReceivedStatus: String = ""

    fun createResources() {
        val accessKey = System.getProperty("spring.cloud.aws.credentials.access-key")
        val secretKey = System.getProperty("spring.cloud.aws.credentials.secret-key")

        logger.info("Propertie access-key: $accessKey")
        logger.info("Propertie secret-key: $secretKey")

        topicArn = snsClient.createTopic { it.name(topicName) }.topicArn()

        queueUrlListenerQueue = sqsClient.getQueueUrl { it.queueName(listenerQueueName) }.queueUrl()

        queueUrlReceivedStatus = sqsClient.createQueue {
            it.queueName(receivedStatusQueueName).attributes(mapOf(QueueAttributeName.MAXIMUM_MESSAGE_SIZE to "256"))
        }.queueUrl()

        val queueArn = sqsClient.getQueueAttributes {
            it.queueUrl(queueUrlReceivedStatus).attributeNamesWithStrings("QueueArn")
        }.attributesAsStrings()["QueueArn"]

        snsClient.subscribe {
            it.topicArn(topicArn).protocol("sqs").endpoint(queueArn).attributes(mapOf("RawMessageDelivery" to "true"))
        }
    }

    fun deleteResources() {
        snsClient.deleteTopic { it.topicArn(topicArn) }
        sqsClient.deleteQueue { it.queueUrl(queueUrlReceivedStatus) }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(CucumberTest::class.java)
    }
}