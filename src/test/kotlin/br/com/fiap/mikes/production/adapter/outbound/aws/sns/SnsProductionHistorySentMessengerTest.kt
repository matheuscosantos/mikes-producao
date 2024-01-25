package br.com.fiap.mikes.production.adapter.outbound.aws.sns

import br.com.fiap.mikes.production.adapter.outbound.aws.sns.client.SnsMessenger
import br.com.fiap.mikes.production.application.port.outbound.productionhistory.dto.ProductionHistorySentMessage
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test

class SnsProductionHistorySentMessengerTest {

    @Test
    fun `test sns order confirmed messenger`() {
        val topicName = "topicName"
        val snsConfirmedMessenger = """{"orderId":"1","status":"received"}"""
        val productionHistoryMessage = ProductionHistorySentMessage("1", "received")

        val snsMessenger = mockk<SnsMessenger>()

        every { snsMessenger.send(any(), any()) } returns Unit

        SnsProductionHistorySentMessenger(topicName, snsMessenger).send(productionHistoryMessage)

        verify { snsMessenger.send(topicName, snsConfirmedMessenger) }
    }
}
