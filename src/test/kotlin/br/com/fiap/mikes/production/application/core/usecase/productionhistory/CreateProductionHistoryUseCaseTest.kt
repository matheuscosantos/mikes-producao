package br.com.fiap.mikes.production.application.core.usecase.productionhistory

import br.com.fiap.mikes.production.application.core.domain.ProductionHistory
import br.com.fiap.mikes.production.application.core.domain.valueobject.OrderId
import br.com.fiap.mikes.production.application.core.domain.valueobject.ProductionHistoryId
import br.com.fiap.mikes.production.application.core.domain.valueobject.ProductionStatus
import br.com.fiap.mikes.production.application.core.usecase.productionhistory.exception.NotAllowedStatusToProductionHistoryException
import br.com.fiap.mikes.production.application.mapper.productionhistory.ProductionHistoryMapper
import br.com.fiap.mikes.production.application.port.inbound.productionhistory.dto.CreateProductionHistoryInboundRequest
import br.com.fiap.mikes.production.application.port.inbound.productionhistorystatus.ValidateProductionHistoryStatusService
import br.com.fiap.mikes.production.application.port.outbound.productionhistory.ProductionHistoryRepository
import br.com.fiap.mikes.production.application.port.outbound.productionhistory.dto.ProductionHistoryOutboundResponse
import io.mockk.Called
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.amshove.kluent.`should be`
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.LocalDateTime
import java.util.UUID

@ExtendWith(MockKExtension::class)
class CreateProductionHistoryUseCaseTest {

    @InjectMockKs
    private lateinit var createProductionHistoryUseCase: CreateProductionHistoryUseCase

    @MockK
    private lateinit var productionHistoryMapper: ProductionHistoryMapper

    @MockK
    private lateinit var productionHistoryRepository: ProductionHistoryRepository

    @MockK
    private lateinit var validateProductionHistoryStatusService: ValidateProductionHistoryStatusService

    @Nested
    inner class NotCreateProductionHistoryWhenInvalidValues {

        @Test
        fun `should return failure when orderId is invalid`() {

            val productHistory = createProductionHistoryUseCase(
                buildCreateProductionHistoryInboundRequest()
            )

            productHistory.isFailure `should be` true

            verify { productionHistoryMapper wasNot Called }
            verify { productionHistoryRepository wasNot Called }
            verify { validateProductionHistoryStatusService wasNot Called }
        }

        @Test
        fun `should return failure when status is invalid`() {

            val productHistory = createProductionHistoryUseCase(
                buildCreateProductionHistoryInboundRequest(
                    orderId = UUID.randomUUID().toString(),
                    status = "INVALID_STATUS"
                )
            )

            productHistory.isFailure `should be` true

            verify { productionHistoryMapper wasNot Called }
            verify { productionHistoryRepository wasNot Called }
            verify { validateProductionHistoryStatusService wasNot Called }
        }
    }

    @Nested
    inner class NotCreateProductionHistoryWhenStatusIsNotAllowed {

        @Test
        fun `should return failure when the initial status is other than RECEIVED`() {
            val id = ProductionHistoryId.generate()
            val orderId = OrderId.new(UUID.randomUUID().toString()).getOrThrow()

            val request = buildCreateProductionHistoryInboundRequest(status = "FINISHED", orderId = orderId.value)

            every { productionHistoryMapper.new(any(), any(), any(), any(), any()) } returns buildProductionHistory(
                id = id,
                orderId = orderId,
                status = ProductionStatus.FINISHED
            )

            every { productionHistoryRepository.findLastByOrderId(any()) } returns null

            val productHistory = createProductionHistoryUseCase(request)

            productHistory.isFailure `should be` true
            productHistory.onFailure { it.message `should be` "status not allowed" }

            verify(exactly = 1) { productionHistoryMapper.new(any(), any(), any(), any(), any()) }
            verify(exactly = 1) { productionHistoryRepository.findLastByOrderId(any()) }
            verify { validateProductionHistoryStatusService wasNot Called }
        }

        @Test
        fun `should return failure when validate status fail`() {
            val id = ProductionHistoryId.generate()
            val orderId = OrderId.new(UUID.randomUUID().toString()).getOrThrow()

            val request = buildCreateProductionHistoryInboundRequest(status = "RECEIVED", orderId = orderId.value)

            every { productionHistoryMapper.new(any(), any(), any(), any(), any()) } returns buildProductionHistory(
                id = id,
                orderId = orderId,
                status = ProductionStatus.RECEIVED
            )

            every { productionHistoryRepository.findLastByOrderId(any()) } returns buildProductionHistoryOutboundResponse(
                id = id.value,
                orderId = orderId.value,
                status = ProductionStatus.RECEIVED.value
            )

            every { productionHistoryMapper.new(any()) } returns buildProductionHistory(
                id = id,
                orderId = orderId,
                status = ProductionStatus.RECEIVED
            )

            every { validateProductionHistoryStatusService(any(), any()) } returns Result.failure(
                NotAllowedStatusToProductionHistoryException("status not allowed")
            )

            val productHistory = createProductionHistoryUseCase(request)

            productHistory.isFailure `should be` true
            productHistory.onFailure { it.message `should be` "status not allowed" }

            verify(exactly = 1) { productionHistoryMapper.new(any()) }
            verify(exactly = 1) { productionHistoryMapper.new(any(), any(), any(), any(), any()) }
            verify(exactly = 1) { productionHistoryRepository.findLastByOrderId(any()) }
            verify(exactly = 1) { validateProductionHistoryStatusService(any(), any()) }
        }
    }

    @Nested
    inner class CreateProductionHistory {

        @Test
        fun `should return production history`() {
            val id = ProductionHistoryId.generate()
            val orderId = OrderId.new(UUID.randomUUID().toString()).getOrThrow()

            val request = buildCreateProductionHistoryInboundRequest(status = "RECEIVED", orderId = orderId.value)

            every { productionHistoryMapper.new(any(), any(), any(), any(), any()) } returns buildProductionHistory(
                id = id,
                orderId = orderId,
                status = ProductionStatus.RECEIVED
            )

            every { productionHistoryRepository.findLastByOrderId(any()) } returns buildProductionHistoryOutboundResponse(
                id = id.value,
                orderId = orderId.value,
                status = ProductionStatus.RECEIVED.value
            )

            every { productionHistoryMapper.new(any()) } returns buildProductionHistory(
                id = id,
                orderId = orderId,
                status = ProductionStatus.RECEIVED
            )

            every { validateProductionHistoryStatusService(any(), any()) } returns buildProductionHistory(
                id = id,
                orderId = orderId,
                status = ProductionStatus.RECEIVED
            )

            every { productionHistoryRepository.save(any()) } returns buildProductionHistoryOutboundResponse(
                id = id.value,
                orderId = orderId.value,
                status = ProductionStatus.RECEIVED.value
            )

            val productHistory = createProductionHistoryUseCase(request)

            productHistory.isSuccess `should be` true
            productHistory.onSuccess {
                it.id.value `should be` id.value
                it.orderId.value `should be` orderId.value
                it.status `should be` ProductionStatus.RECEIVED
            }

            verify(exactly = 2) { productionHistoryMapper.new(any()) }
            verify(exactly = 1) { productionHistoryMapper.new(any(), any(), any(), any(), any()) }
            verify(exactly = 1) { productionHistoryRepository.findLastByOrderId(any()) }
            verify(exactly = 1) { validateProductionHistoryStatusService(any(), any()) }
        }
    }

    private fun buildCreateProductionHistoryInboundRequest(
        orderId: String = "1",
        status: String = "RECEIVED"
    ) = CreateProductionHistoryInboundRequest(
        orderId = orderId,
        status = status
    )

    private fun buildProductionHistoryOutboundResponse(id: String, orderId: String, status: String) =
        ProductionHistoryOutboundResponse(
            id = id,
            orderId = orderId,
            status = status,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
        )

    private fun buildProductionHistory(id: ProductionHistoryId, orderId: OrderId, status: ProductionStatus) =
        ProductionHistory.new(
            id = id,
            orderId = orderId,
            status = status,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
        )
}