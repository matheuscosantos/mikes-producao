package br.com.fiap.mikes.production.cucumber

import br.com.fiap.mikes.production.ProductionApplication
import br.com.fiap.mikes.production.adapter.outbound.database.jpa.ProductHistoryJpaRepository
import io.cucumber.spring.CucumberContextConfiguration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.ActiveProfiles
import software.amazon.awssdk.services.sns.SnsClient
import software.amazon.awssdk.services.sqs.SqsClient

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
}