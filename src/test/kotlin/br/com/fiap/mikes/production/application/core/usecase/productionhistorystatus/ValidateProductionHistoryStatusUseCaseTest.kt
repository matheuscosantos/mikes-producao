package br.com.fiap.mikes.production.application.core.usecase.productionhistorystatus

import br.com.fiap.mikes.production.application.core.domain.ProductionHistory
import br.com.fiap.mikes.production.application.core.domain.valueobject.OrderId
import br.com.fiap.mikes.production.application.core.domain.valueobject.ProductionHistoryId
import br.com.fiap.mikes.production.application.core.domain.valueobject.ProductionStatus
import br.com.fiap.mikes.production.application.port.outbound.productionhistorystatus.ProductionHistoryStatusRepository
import br.com.fiap.mikes.production.application.port.outbound.productionhistorystatus.dto.ProductionHistoryStatusOutboundResponse
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
class ValidateProductionHistoryStatusUseCaseTest {

    @InjectMockKs
    private lateinit var validateProductionHistoryStatusUseCase: ValidateProductionHistoryStatusUseCase

    @MockK
    private lateinit var productionHistoryStatusRepository: ProductionHistoryStatusRepository

    @Nested
    inner class ValidateProductionHistoryStatusIsFailure {

        @Test
        fun `should validate is fail when non exists production history status`() {
            val orderId = OrderId.new(UUID.randomUUID().toString()).getOrThrow()

            val newProductionHistory = buildProductionHistory(
                id = ProductionHistoryId.generate(),
                orderId = orderId,
                status = ProductionStatus.RECEIVED
            )

            val lastProductionHistory = buildProductionHistory(
                id = ProductionHistoryId.generate(),
                orderId = orderId,
                status = ProductionStatus.PREPARING
            )

            every { productionHistoryStatusRepository.findByStatus(any()) } returns null

            val productHistory = validateProductionHistoryStatusUseCase(newProductionHistory, lastProductionHistory)

            productHistory.isFailure `should be` true
            productHistory.onFailure { it.message `should be` "Production history status not found" }

            verify(exactly = 1) { productionHistoryStatusRepository.findByStatus(any()) }
        }

        @Test
        fun `should validate is fail when status now allowed`() {
            val orderId = OrderId.new(UUID.randomUUID().toString()).getOrThrow()

            val newProductionHistory = buildProductionHistory(
                id = ProductionHistoryId.generate(),
                orderId = orderId,
                status = ProductionStatus.RECEIVED
            )

            val lastProductionHistory = buildProductionHistory(
                id = ProductionHistoryId.generate(),
                orderId = orderId,
                status = ProductionStatus.FINISHED
            )

            every { productionHistoryStatusRepository.findByStatus(any()) } returns buildProductionHistoryStatus()

            val productHistory = validateProductionHistoryStatusUseCase(newProductionHistory, lastProductionHistory)

            productHistory.isFailure `should be` true
            productHistory.onFailure { it.message `should be` "status not allowed" }

            verify(exactly = 1) { productionHistoryStatusRepository.findByStatus(any()) }
        }
    }

    @Nested
    inner class ValidateProductionHistoryStatusIsSuccess {

        @Test
        fun `should validate is success`() {
            val orderId = OrderId.new(UUID.randomUUID().toString()).getOrThrow()

            val newProductionHistory = buildProductionHistory(
                id = ProductionHistoryId.generate(),
                orderId = orderId,
                status = ProductionStatus.PREPARING
            )

            val lastProductionHistory = buildProductionHistory(
                id = ProductionHistoryId.generate(),
                orderId = orderId,
                status = ProductionStatus.RECEIVED
            )

            every { productionHistoryStatusRepository.findByStatus(any()) } returns buildProductionHistoryStatus()

            val productHistory = validateProductionHistoryStatusUseCase(newProductionHistory, lastProductionHistory)

            productHistory.isSuccess `should be` true
            productHistory.onSuccess {
                it.id `should be equal to` newProductionHistory.id
                it.orderId `should be equal to` newProductionHistory.orderId
                it.status `should be equal to` newProductionHistory.status
            }

            verify(exactly = 1) { productionHistoryStatusRepository.findByStatus(any()) }
        }
    }

    private fun buildProductionHistory(id: ProductionHistoryId, orderId: OrderId, status: ProductionStatus) =
        ProductionHistory.new(
            id = id,
            orderId = orderId,
            status = status,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
        ).getOrThrow()

    private fun buildProductionHistoryStatus() =
        ProductionHistoryStatusOutboundResponse(
            status = ProductionStatus.RECEIVED.value,
            nextStatus = ProductionStatus.PREPARING.value
        )
}