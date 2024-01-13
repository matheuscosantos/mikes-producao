package br.com.fiap.mikes.production.application.core.usecase.productionhistory

import br.com.fiap.mikes.production.application.core.domain.ProductionHistory
import br.com.fiap.mikes.production.application.core.domain.valueobject.OrderId
import br.com.fiap.mikes.production.application.core.domain.valueobject.ProductionHistoryId
import br.com.fiap.mikes.production.application.core.domain.valueobject.ProductionStatus
import br.com.fiap.mikes.production.application.mapper.productionhistory.ProductionHistoryMapper
import br.com.fiap.mikes.production.application.port.outbound.productionhistory.ProductionHistoryRepository
import br.com.fiap.mikes.production.application.port.outbound.productionhistory.dto.ProductionHistoryOutboundResponse
import io.mockk.Called
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.amshove.kluent.`should be`
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.LocalDateTime
import java.util.UUID

@ExtendWith(MockKExtension::class)
class FindProductionHistoryLastByOrderUseCaseTest {

    @InjectMockKs
    private lateinit var findProductionHistoryLastByOrderUseCase: FindProductionHistoryLastByOrderUseCase

    @MockK
    private lateinit var productionHistoryMapper: ProductionHistoryMapper

    @MockK
    private lateinit var productionHistoryRepository: ProductionHistoryRepository

    @Nested
    inner class NotFindProductionHistoryLastByOrderWhenInvalidValues {

        @Test
        fun `should return failure when orderId is invalid`() {
            val productHistory = findProductionHistoryLastByOrderUseCase(
                orderId = "INVALID_ORDER_ID"
            )

            productHistory.isFailure `should be` true
            productHistory.onFailure { it.message `should be` "invalid order id." }

            verify { productionHistoryMapper wasNot Called }
            verify { productionHistoryRepository wasNot Called }
        }

        @Test
        fun `should return failure when non exists production history`() {
            val orderId = OrderId.new(UUID.randomUUID().toString()).getOrThrow()

            every { productionHistoryRepository.findLastByOrderId(any()) } returns null

            val productHistory = findProductionHistoryLastByOrderUseCase(
                orderId = orderId.value
            )

            productHistory.isFailure `should be` true
            productHistory.onFailure { it.message `should be` "product history not found." }

            verify(exactly = 1) { productionHistoryRepository.findLastByOrderId(any()) }
            verify { productionHistoryMapper wasNot Called }
        }
    }

    @Nested
    inner class FindProductionHistoryByOrder {

        @Test
        fun `should return last production history`() {
            val id = ProductionHistoryId.generate()
            val orderId = OrderId.new(UUID.randomUUID().toString()).getOrThrow()

            every { productionHistoryRepository.findLastByOrderId(any()) } returns buildProductionHistoryOutboundResponse(
                id = id.value,
                orderId = orderId.value
            )

            every { productionHistoryMapper.new(any()) } returns buildProductionHistory(
                id = id,
                orderId = orderId
            )

            val productHistory = findProductionHistoryLastByOrderUseCase(
                orderId = orderId.value
            )

            productHistory.isSuccess `should be` true

            productHistory.onSuccess {
                it.id `should be equal to` id
                it.orderId `should be equal to` orderId
                it.status `should be equal to` ProductionStatus.RECEIVED
            }

            verify(exactly = 1) { productionHistoryRepository.findLastByOrderId(any()) }
            verify(exactly = 1) { productionHistoryMapper.new(any()) }
        }
    }

    private fun buildProductionHistoryOutboundResponse(id: String, orderId: String) =
        ProductionHistoryOutboundResponse(
            id = id,
            orderId = orderId,
            status = ProductionStatus.RECEIVED.value,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
        )

    private fun buildProductionHistory(id: ProductionHistoryId, orderId: OrderId) =
        ProductionHistory.new(
            id = id,
            orderId = orderId,
            status = ProductionStatus.RECEIVED,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
        )
}