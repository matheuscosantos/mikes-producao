package br.com.fiap.mikes.production.cucumber

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.cucumber.java.After
import io.cucumber.java.Before
import io.cucumber.java.Scenario
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.restassured.RestAssured
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should not be null`

class InitProductionStepsDefs : CucumberTest() {

    private var status: String = ""
    private var orderId: String = ""

    private var topicArn: String = ""
    private var queueUrlListenerQueue: String = ""
    private var queueUrlReceivedStatus: String = ""

    @Before(value = "@InitProduction")
    fun setupBefore(scenario: Scenario) {
        RestAssured.baseURI = "http://localhost:$port"
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()

        repository.deleteAll()

        val createTopicResponse = snsClient.createTopic { it.name(topicName) }
        topicArn = createTopicResponse.topicArn()

        val createQueueResponseListenerQueue = sqsClient.createQueue {
            it.queueName(listenerQueueName)
        }
        queueUrlListenerQueue = createQueueResponseListenerQueue.queueUrl()

        val createQueueResponseReceivedStatusQueue = sqsClient.createQueue {
            it.queueName(receivedStatusQueueName)
        }

        queueUrlReceivedStatus = createQueueResponseReceivedStatusQueue.queueUrl()

        val queueAttributesResponseReceivedStatus =
            sqsClient.getQueueAttributes { it.queueUrl(queueUrlReceivedStatus).attributeNamesWithStrings("QueueArn") }
        val queueArnReceivedStatus: String? = queueAttributesResponseReceivedStatus.attributesAsStrings()["QueueArn"]

        snsClient.subscribe {
            it.topicArn(topicArn).protocol("sqs").endpoint(queueArnReceivedStatus).attributes(
                mapOf(
                    "RawMessageDelivery" to "true"
                )
            )
        }
    }

    @After(value = "@InitProduction")
    fun setupAfter(scenario: Scenario) {
        snsClient.deleteTopic { it.topicArn(topicArn) }
        sqsClient.deleteQueue { it.queueUrl(queueUrlListenerQueue) }
        sqsClient.deleteQueue { it.queueUrl(queueUrlReceivedStatus) }
    }

    @Given("que o processo da produção é iniciado com o id do pedido inválido")
    fun `starts production process with invalid order id`(payload: Map<String, Any>) {
        sqsClient.sendMessage {
            it.queueUrl(queueUrlListenerQueue).messageBody(
                jacksonObjectMapper().writeValueAsString(payload)
            )
        }

        status = payload["status"].toString()
        orderId = payload["orderId"].toString()
    }

    @Given("que o processo da produção é iniciado com o status inválido")
    fun `starts production process with invalid status`(payload: Map<String, Any>) {
        sqsClient.sendMessage {
            it.queueUrl(queueUrlListenerQueue).messageBody(
                jacksonObjectMapper().writeValueAsString(payload)
            )
        }

        status = payload["status"].toString()
        orderId = payload["orderId"].toString()
    }

    @Given("que o processo da produção é iniciado com status 'READY' que não é permitido")
    fun `starts production process with not allowed status`(payload: Map<String, Any>) {
        sqsClient.sendMessage {
            it.queueUrl(queueUrlListenerQueue).messageBody(
                jacksonObjectMapper().writeValueAsString(payload)
            )
        }

        status = payload["status"].toString()
        orderId = payload["orderId"].toString()
    }

    @Given("que o processo da produção é iniciado é iniciado com dados válidos")
    fun `starts production process with valid data`(payload: Map<String, Any>) {
        sqsClient.sendMessage {
            it.queueUrl(queueUrlListenerQueue).messageBody(
                jacksonObjectMapper().writeValueAsString(payload)
            )
        }

        status = payload["status"].toString()
        orderId = payload["orderId"].toString()
    }

    @Then("não deve haver novo registro da produção do pedido")
    fun `validate that there was no new registration`() {
        runBlocking {
            delay(5000)
            repository.findByOrderId(orderId).find { it.status == status } `should be equal to` null
        }
    }

    @Then("deve haver um novo registro da produção do pedido")
    fun `validate that there was a new registration`() {
        runBlocking {
            delay(5000)

            val productionHistory = repository.findByOrderId(orderId).find { it.status == status }

            productionHistory.`should not be null`()
            productionHistory.status `should be equal to` status
            productionHistory.orderId `should be equal to` orderId
        }
    }

    @Then("não deve haver mensagem na fila de atualização do status do pedido")
    fun `validate if there is no message in the queue`() {
        val sqsMessages = sqsClient.receiveMessage { it.queueUrl(queueUrlReceivedStatus).waitTimeSeconds(1) }
        sqsMessages.messages().size `should be equal to` 0
    }

    @Then("deve haver mensagem na fila de atualização do status do pedido")
    fun `validate if there is message in the queue`() {
        val sqsMessages = sqsClient.receiveMessage { it.queueUrl(queueUrlReceivedStatus).waitTimeSeconds(1) }

        sqsMessages.messages().size `should be equal to` 1

        sqsMessages.messages().first().body() `should be equal to` "{\"orderId\":\"$orderId\",\"status\":\"$status\"}"
    }
}