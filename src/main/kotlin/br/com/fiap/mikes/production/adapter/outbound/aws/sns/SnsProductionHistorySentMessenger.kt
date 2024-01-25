package br.com.fiap.mikes.production.adapter.outbound.aws.sns

import br.com.fiap.mikes.production.adapter.outbound.aws.sns.client.SnsMessenger
import br.com.fiap.mikes.production.application.port.outbound.productionhistory.ProductionHistorySentMessenger
import br.com.fiap.mikes.production.application.port.outbound.productionhistory.dto.ProductionHistorySentMessage
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class SnsProductionHistorySentMessenger(
    @Value("\${spring.cloud.aws.sns.topic-name}")
    private val topicName: String,
    private val snsMessenger: SnsMessenger
) : ProductionHistorySentMessenger {

    override fun send(productionHistorySentMessage: ProductionHistorySentMessage) {
        snsMessenger.send(topicName, jacksonObjectMapper().writeValueAsString(productionHistorySentMessage))
    }
}