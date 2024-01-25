package br.com.fiap.mikes.production.adapter.outbound.aws.sns.client

import io.awspring.cloud.sns.core.SnsTemplate
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test

class DefaultSnsMessengerTest {

    @Test
    fun `test default sns messenger`() {
        val snsTemplate = mockk<SnsTemplate>()

        val defaultSnsMessenger = DefaultSnsMessenger(snsTemplate)

        val topicName = "topicName"
        val snsConfirmedMessenger = """{"orderId":"1","status":"received"}"""

        every { snsTemplate.send(topicName, any()) } returns Unit

        defaultSnsMessenger.send(topicName, snsConfirmedMessenger)

        verify { snsTemplate.send(topicName, match { it.payload == snsConfirmedMessenger }) }
    }
}
