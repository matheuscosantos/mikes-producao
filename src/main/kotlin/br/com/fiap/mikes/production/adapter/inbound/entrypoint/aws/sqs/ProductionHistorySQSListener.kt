package br.com.fiap.mikes.production.adapter.inbound.entrypoint.aws.sqs

import br.com.fiap.mikes.production.application.port.inbound.productionhistory.CreateProductionHistoryService
import br.com.fiap.mikes.production.application.port.inbound.productionhistory.dto.CreateProductionHistoryInboundRequest
import br.com.fiap.mikes.production.application.port.outbound.productionhistory.ProductionHistorySentMessenger
import br.com.fiap.mikes.production.application.port.outbound.productionhistory.dto.ProductionHistorySentMessage
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.awspring.cloud.sqs.annotation.SqsListener
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class ProductionHistorySQSListener(
    private val createProductionHistoryService: CreateProductionHistoryService,
    private val productionHistorySentMessenger: ProductionHistorySentMessenger
) {

    @SqsListener("\${spring.cloud.aws.sqs.queue-name}")
    fun receiveMessage(message: String) {
        runCatching {
            val createProductionHistory = jacksonObjectMapper()
                .readValue(message, CreateProductionHistoryInboundRequest::class.java)

            createProductionHistoryService(createProductionHistory).fold(
                onSuccess = {
                    productionHistorySentMessenger.send(
                        ProductionHistorySentMessage(
                            orderId = createProductionHistory.orderId,
                            status = createProductionHistory.status,
                        )
                    )

                    logger.info("Success to create production history: ${createProductionHistory.orderId}")
                },
                onFailure = {
                    logger.error("Failed to create production history: ${it.message}")
                }
            )
        }.onFailure {
            logger.error("Failed to create production history: ${it.message}")
        }
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(ProductionHistorySQSListener::class.java)
    }
}
