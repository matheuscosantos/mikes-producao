package br.com.fiap.mikes.production.adapter.outbound.aws.sns.client

import io.awspring.cloud.sns.core.SnsTemplate
import org.springframework.messaging.support.GenericMessage
import org.springframework.stereotype.Component

@Component
class DefaultSnsMessenger(private val snsTemplate: SnsTemplate) : SnsMessenger {

    override fun send(topicName: String, message: String) {
        snsTemplate.send(
            topicName, GenericMessage(message)
        )
    }
}