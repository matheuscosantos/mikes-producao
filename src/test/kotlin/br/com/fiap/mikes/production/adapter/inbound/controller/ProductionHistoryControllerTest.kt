package br.com.fiap.mikes.production.adapter.inbound.controller

import br.com.fiap.mikes.production.adapter.inbound.entrypoint.controller.ProductionHistoryController
import br.com.fiap.mikes.production.adapter.inbound.entrypoint.controller.dto.ProductionHistoryRequestDto
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
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import java.time.LocalDateTime

@ExtendWith(MockKExtension::class)
class ProductionHistoryControllerTest {

    @InjectMockKs
    private lateinit var productionHistoryController: ProductionHistoryController

    @MockK
    private lateinit var createProductionHistoryService: CreateProductionHistoryService

    @MockK
    private lateinit var productionHistorySentMessenger: ProductionHistorySentMessenger

    @Test
    fun `should not process message when invalid order id`() {

        every { createProductionHistoryService(any()) } returns Result.failure(Exception())

        assertThrows<Exception> {
            productionHistoryController.createProductionHistory(buildProductionHistory(orderId = "invalid order id"))
        }

        verify(exactly = 1) { createProductionHistoryService(any()) }
        verify(exactly = 0) { productionHistorySentMessenger.send(any()) }
    }

    @Test
    fun `should not process message when invalid status`() {

        every { createProductionHistoryService(any()) } returns Result.failure(Exception())

        assertThrows<Exception> {
            productionHistoryController.createProductionHistory(buildProductionHistory(status = "received"))
        }

        verify(exactly = 1) { createProductionHistoryService(any()) }
        verify(exactly = 0) { productionHistorySentMessenger.send(any()) }
    }

    @Test
    fun `should process message when valid data`() {

        every { createProductionHistoryService(any()) } returns Result.success(ProductionHistory.new())
        every { productionHistorySentMessenger.send(any()) } returns Unit

        val result = productionHistoryController.createProductionHistory(buildProductionHistory())

        verify(exactly = 1) { createProductionHistoryService(any()) }
        verify(exactly = 1) { productionHistorySentMessenger.send(any()) }

        result.body?.orderId `should be equal to` "e2904c8c-5dae-4ae9-8221-d06599875c6c"
        result.body?.status `should be equal to` "RECEIVED"
    }

    private fun buildProductionHistory(
        orderId: String = "e2904c8c-5dae-4ae9-8221-d06599875c6c",
        status: String = "RECEIVED"
    ): ProductionHistoryRequestDto {
        return ProductionHistoryRequestDto(
            orderId = orderId,
            status = status
        )
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