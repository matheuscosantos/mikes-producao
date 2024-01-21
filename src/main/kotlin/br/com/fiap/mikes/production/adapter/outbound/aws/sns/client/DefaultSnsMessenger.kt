package br.com.fiap.mikes.production.adapter.outbound.aws.sns.client

import br.com.fiap.mikes.production.application.port.outbound.productionhistory.dto.ProductionHistorySentMessage
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.awspring.cloud.sns.core.SnsTemplate
import org.springframework.messaging.support.GenericMessage
import org.springframework.stereotype.Component

@Component
class DefaultSnsMessenger(private val snsTemplate: SnsTemplate) : SnsMessenger<ProductionHistorySentMessage> {

    override fun send(topicName: String, message: ProductionHistorySentMessage) {
        snsTemplate.send(
            topicName, GenericMessage(
                jacksonObjectMapper().writeValueAsString(message)
            )
        )
    }
}