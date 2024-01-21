package br.com.fiap.mikes.production.cucumber

import br.com.fiap.mikes.production.adapter.outbound.database.entity.ProductHistoryEntity
import br.com.fiap.mikes.production.cucumber.config.RestAssuredExtension
import io.cucumber.java.After
import io.cucumber.java.Before
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.restassured.RestAssured
import io.restassured.response.Response
import io.restassured.response.ResponseOptions
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should not be null`
import java.time.LocalDateTime

class ChangeProductionStatusStepsDefs : CucumberTest() {

    private lateinit var response: ResponseOptions<Response>

    private lateinit var status: String
    private lateinit var orderId: String

    private lateinit var topicArn: String
    private lateinit var queueUrlReceivedStatus: String

    @Before(value = "@ChangeProductionStatus")
    fun setup() {
        RestAssured.baseURI = "http://localhost:$port"
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()

        repository.deleteAll()

        val createTopicResponse = snsClient.createTopic { it.name(topicName) }
        topicArn = createTopicResponse.topicArn()

        val createQueueResponseReceivedStatus = sqsClient.createQueue { it.queueName(receivedStatusQueueName) }
        queueUrlReceivedStatus = createQueueResponseReceivedStatus.queueUrl()

        val queueAttributesResponse =
            sqsClient.getQueueAttributes { it.queueUrl(queueUrlReceivedStatus).attributeNamesWithStrings("QueueArn") }
        val queueArn: String? = queueAttributesResponse.attributesAsStrings()["QueueArn"]

        snsClient.subscribe {
            it.topicArn(topicArn).protocol("sqs").endpoint(queueArn).attributes(
                mapOf(
                    "RawMessageDelivery" to "true"
                )
            )
        }
    }

    @After(value = "@ChangeProductionStatus")
    fun setupAll() {
        snsClient.deleteTopic { it.topicArn(topicArn) }
        sqsClient.purgeQueue { it.queueUrl(queueUrlReceivedStatus) }
    }

    @Given("que a alteração do status da produção é recebido com o id do pedido inválido")
    fun `receive update status production history with invalid order id`(payload: Map<String, Any>) {
        repository.save(buildProductionHistoryEntity())

        status = payload["status"].toString()
        orderId = payload["orderId"].toString()
        response = RestAssuredExtension.post("/production-history", payload)
    }

    @Given("que a alteração do status da produção é recebido com o status inválido")
    fun `receive update status production history with invalid status`(payload: Map<String, Any>) {
        repository.save(buildProductionHistoryEntity())

        status = payload["status"].toString()
        orderId = payload["orderId"].toString()
        response = RestAssuredExtension.post("/production-history", payload)
    }

    @Given("que a alteração do status da produção é recebido indo de 'RECEIVED' para 'READY'")
    fun `receive update status production history with not allowed status`(payload: Map<String, Any>) {
        repository.save(buildProductionHistoryEntity())

        status = payload["status"].toString()
        orderId = payload["orderId"].toString()
        response = RestAssuredExtension.post("/production-history", payload)
    }

    @Given("que a alteração do status da produção é recebido indo de 'RECEIVED' para 'RECEIVED'")
    fun `receive update status production history with the same status`(payload: Map<String, Any>) {
        repository.save(buildProductionHistoryEntity())

        status = payload["status"].toString()
        orderId = payload["orderId"].toString()
        response = RestAssuredExtension.post("/production-history", payload)
    }

    @Given("que a alteração do status da produção é recebido com dados válidos")
    fun `receive update status production history with valid data`(payload: Map<String, Any>) {
        repository.save(buildProductionHistoryEntity())

        status = payload["status"].toString()
        orderId = payload["orderId"].toString()
        response = RestAssuredExtension.post("/production-history", payload)
    }

    @Then("deve retornar o status {int} com o campo {string} igual a {string}")
    fun `validate request status and error message`(status: Int, message: String, value: String) {
        response.statusCode `should be equal to` status
        response.body.jsonPath().get<String>(message).toString() `should be equal to` value
    }

    @Then("não deve haver novo registro com a alteração no status da produção")
    fun `validate that the status has not changed`() {
        repository.findByOrderId(orderId).find { it.status == status } `should be equal to` null
    }

    @Then("deve retornar o status {int}")
    fun `validate request status`(status: Int) {
        response.statusCode `should be equal to` status
    }

    @Then("deve haver um novo registro com a alteração no status da produção")
    fun `validate that the status has new changed`() {
        val productionHistory = repository.findByOrderId(orderId).find { it.status == status }

        productionHistory.`should not be null`()
        productionHistory.status `should be equal to` status
        productionHistory.orderId `should be equal to` orderId
    }

    @Then("deve ter enviado uma mensagem para o tópico de produção")
    fun `validate if there is message in the queue`() {
        val sqsMessages = sqsClient.receiveMessage { it.queueUrl(queueUrlReceivedStatus).waitTimeSeconds(1) }

        sqsMessages.messages().size `should be equal to` 1

        sqsMessages.messages().first().body() `should be equal to` "{\"orderId\":\"$orderId\",\"status\":\"$status\"}"
    }

    private fun buildProductionHistoryEntity() = ProductHistoryEntity(
        "40548f9c-a2ff-4c35-8c53-b5e481e329af",
        "26bfdd39-9e38-4f51-9bac-77ade65771da",
        "RECEIVED",
        LocalDateTime.now(),
        LocalDateTime.now()
    )
}