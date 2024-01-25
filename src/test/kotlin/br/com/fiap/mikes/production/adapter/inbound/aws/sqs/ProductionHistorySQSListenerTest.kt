package br.com.fiap.mikes.production.adapter.inbound.aws.sqs

import br.com.fiap.mikes.production.adapter.inbound.entrypoint.aws.sqs.ProductionHistorySQSListener
import br.com.fiap.mikes.production.application.core.domain.ProductionHistory
import br.com.fiap.mikes.production.application.core.domain.valueobject.OrderId
import br.com.fiap.mikes.production.application.core.domain.valueobject.ProductionHistoryId
import br.com.fiap.mikes.production.application.core.domain.valueobject.ProductionStatus
import br.com.fiap.mikes.production.application.port.inbound.productionhistory.CreateProductionHistoryService
import br.com.fiap.mikes.production.application.port.outbound.productionhistory.ProductionHistorySentMessenger
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.LocalDateTime

@ExtendWith(MockKExtension::class)
class ProductionHistorySQSListenerTest {

    @InjectMockKs
    private lateinit var productionHistorySQSListener: ProductionHistorySQSListener

    @MockK
    private lateinit var createProductionHistoryService: CreateProductionHistoryService

    @MockK
    private lateinit var productionHistorySentMessenger: ProductionHistorySentMessenger

    @Test
    fun `should not process message when receive message in invalid format`() {

        productionHistorySQSListener.receiveMessage("invalid message")

        verify(exactly = 0) { createProductionHistoryService(any()) }
        verify(exactly = 0) { productionHistorySentMessenger.send(any()) }
    }

    @Test
    fun `should not process message when invalid order id`() {

        every { createProductionHistoryService(any()) } returns Result.failure(Exception())

        productionHistorySQSListener.receiveMessage("""{"orderId": "invalid order id", "status": "RECEIVED"}""")

        verify(exactly = 1) { createProductionHistoryService(any()) }
        verify(exactly = 0) { productionHistorySentMessenger.send(any()) }
    }

    @Test
    fun `should not process message when invalid status`() {

        every { createProductionHistoryService(any()) } returns Result.failure(Exception())

        productionHistorySQSListener.receiveMessage("""{"orderId": "invalid order id", "status": "received"}""")

        verify(exactly = 1) { createProductionHistoryService(any()) }
        verify(exactly = 0) { productionHistorySentMessenger.send(any()) }
    }

    @Test
    fun `should process message when valid data`() {

        every { createProductionHistoryService(any()) } returns Result.success(ProductionHistory.new())
        every { productionHistorySentMessenger.send(any()) } returns Unit

        productionHistorySQSListener.receiveMessage("""{"orderId": "invalid order id", "status": "RECEIVED"}""")

        verify(exactly = 1) { createProductionHistoryService(any()) }
        verify(exactly = 1) { productionHistorySentMessenger.send(any()) }
    }

    private fun ProductionHistory.Companion.new(): ProductionHistory {
        return new(
            id = ProductionHistoryId.generate(),
            orderId = OrderId.new("e2904c8c-5dae-4ae9-8221-d06599875c6c").getOrThrow(),
            status = ProductionStatus.RECEIVED,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
        ).getOrThrow()
    }
}