package br.com.fiap.mikes.production.adapter.outbound.aws.sns

import br.com.fiap.mikes.production.adapter.outbound.aws.sns.client.SnsMessenger
import br.com.fiap.mikes.production.application.port.outbound.productionhistory.ProductionHistorySentMessenger
import br.com.fiap.mikes.production.application.port.outbound.productionhistory.dto.ProductionHistorySentMessage
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class SnsProductionHistorySentMessenger(private val snsMessenger: SnsMessenger<ProductionHistorySentMessage>) :
    ProductionHistorySentMessenger {

    @Value("\${spring.cloud.aws.sns.topic-name}")
    private lateinit var topicName: String

    override fun send(productionHistorySentMessage: ProductionHistorySentMessage) {
        snsMessenger.send(topicName, productionHistorySentMessage)
    }
}