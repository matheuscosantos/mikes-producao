package br.com.fiap.mikes.production.adapter.outbound.database

import br.com.fiap.mikes.production.adapter.outbound.database.entity.ProductionHistoryStatusEntity
import br.com.fiap.mikes.production.adapter.outbound.database.jpa.ProductionHistoryStatusJpaRepository
import br.com.fiap.mikes.production.application.core.domain.valueobject.ProductionStatus
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should be null`
import org.amshove.kluent.`should not be null`
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.Optional

@ExtendWith(MockKExtension::class)
class ProductionHistoryStatusDatabaseRepositoryTest {

    @InjectMockKs
    private lateinit var productionHistoryStatusDatabaseRepository: ProductionHistoryStatusDatabaseRepository

    @MockK
    private lateinit var productionHistoryStatusJpaRepository: ProductionHistoryStatusJpaRepository

    @Nested
    inner class ProductionHistoryStatusDatabaseRepositoryFindByStatusTest {
        @Test
        fun `should return the history status by status when exists history status to status`() {
            val status = ProductionStatus.RECEIVED
            val nextStatus = ProductionStatus.PREPARING

            every { productionHistoryStatusJpaRepository.findByStatus(any()) } returns Optional.of(
                buildProductionHistoryStatusEntity(status, nextStatus)
            )

            val productionHistoryStatus = productionHistoryStatusDatabaseRepository.findByStatus(
                status = status
            )

            productionHistoryStatus.`should not be null`()
            productionHistoryStatus.status `should be equal to` ProductionStatus.RECEIVED.value
            productionHistoryStatus.nextStatus `should be equal to` ProductionStatus.PREPARING.value
        }

        @Test
        fun `should not return the last history by orderId when non exists history to order id`() {
            val status = ProductionStatus.RECEIVED

            every { productionHistoryStatusJpaRepository.findByStatus(any()) } returns Optional.empty()

            val productionHistory = productionHistoryStatusDatabaseRepository.findByStatus(
                status = status
            )

            productionHistory.`should be null`()
        }
    }

    private fun buildProductionHistoryStatusEntity(status: ProductionStatus, nextStatus: ProductionStatus) =
        ProductionHistoryStatusEntity(
            status = status.value,
            nextStatus = nextStatus.value
        )
}
