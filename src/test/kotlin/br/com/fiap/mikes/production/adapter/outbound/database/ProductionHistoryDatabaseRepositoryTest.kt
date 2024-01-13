package br.com.fiap.mikes.production.adapter.outbound.database

import br.com.fiap.mikes.production.adapter.outbound.database.entity.ProductHistoryEntity
import br.com.fiap.mikes.production.adapter.outbound.database.jpa.ProductHistoryJpaRepository
import br.com.fiap.mikes.production.application.core.domain.ProductionHistory
import br.com.fiap.mikes.production.application.core.domain.valueobject.OrderId
import br.com.fiap.mikes.production.application.core.domain.valueobject.ProductionHistoryId
import br.com.fiap.mikes.production.application.core.domain.valueobject.ProductionStatus
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should be null`
import org.amshove.kluent.`should not be null`
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import java.time.LocalDateTime
import java.util.Optional
import java.util.UUID

@ExtendWith(MockKExtension::class)
class ProductionHistoryDatabaseRepositoryTest {

    @InjectMockKs
    private lateinit var productionHistoryDatabaseRepository: ProductionHistoryDatabaseRepository

    @MockK
    private lateinit var productionHistoryJpaRepository: ProductHistoryJpaRepository

    @Nested
    inner class ProductionHistoryDatabaseRepositorySaveTest {
        @Test
        fun `should save production history`() {
            val id = ProductionHistoryId.generate()
            val orderId = OrderId.new(UUID.randomUUID().toString()).getOrElse { throw it }

            every { productionHistoryJpaRepository.save(any()) } returns buildProductionHistoryEntity(id, orderId)

            val productionHistory = productionHistoryDatabaseRepository.save(
                productionHistory = buildProductionHistory(id, orderId)
            )

            productionHistory.id shouldBeEqualTo id.value
            productionHistory.orderId shouldBeEqualTo orderId.value
            productionHistory.status shouldBeEqualTo ProductionStatus.RECEIVED.value
        }

        @Test
        fun `should not save the production history when there is a history with the same status and orderId `() {
            val id = ProductionHistoryId.generate()
            val orderId = OrderId.new(UUID.randomUUID().toString()).getOrElse { throw it }

            every { productionHistoryJpaRepository.save(any()) } throws Exception()

            assertThrows<Exception> {
                productionHistoryDatabaseRepository.save(
                    productionHistory = buildProductionHistory(id, orderId)
                )
            }
        }
    }


    @Nested
    inner class ProductionHistoryDatabaseRepositoryFindByOrderIdTest {
        @Test
        fun `should return the history by orderId when exists history to order id`() {
            val id = ProductionHistoryId.generate()
            val orderId = OrderId.new(UUID.randomUUID().toString()).getOrElse { throw it }

            every { productionHistoryJpaRepository.findByOrderId(any()) } returns listOf(
                buildProductionHistoryEntity(id, orderId)
            )

            val productionHistories = productionHistoryDatabaseRepository.findByOrderId(
                orderId = orderId
            )

            productionHistories.size shouldBeEqualTo 1
            productionHistories[0].id shouldBeEqualTo id.value
            productionHistories[0].orderId shouldBeEqualTo orderId.value
            productionHistories[0].status shouldBeEqualTo ProductionStatus.RECEIVED.value
        }

        @Test
        fun `should not return the history by orderId when non exists history to order id`() {
            val orderId = OrderId.new(UUID.randomUUID().toString()).getOrElse { throw it }

            every { productionHistoryJpaRepository.findByOrderId(any()) } returns listOf()

            val productionHistories = productionHistoryDatabaseRepository.findByOrderId(
                orderId = orderId
            )

            productionHistories.size shouldBeEqualTo 0
        }
    }

    @Nested
    inner class ProductionHistoryDatabaseRepositoryLastByOrderIdTest {
        @Test
        fun `should return the last history by orderId when exists history to order id`() {
            val id = ProductionHistoryId.generate()
            val orderId = OrderId.new(UUID.randomUUID().toString()).getOrElse { throw it }

            every { productionHistoryJpaRepository.findFirstByOrderIdOrderByCreatedAtDesc(any()) } returns Optional.of(
                buildProductionHistoryEntity(id, orderId)
            )

            val productionHistory = productionHistoryDatabaseRepository.findLastByOrderId(
                orderId = orderId
            )

            productionHistory.`should not be null`()
            productionHistory.id `should be equal to`  id.value
            productionHistory.orderId `should be equal to` orderId.value
            productionHistory.status `should be equal to` ProductionStatus.RECEIVED.value
        }

        @Test
        fun `should not return the last history by orderId when non exists history to order id`() {
            val orderId = OrderId.new(UUID.randomUUID().toString()).getOrElse { throw it }

            every { productionHistoryJpaRepository.findFirstByOrderIdOrderByCreatedAtDesc(any()) } returns Optional.empty()

            val productionHistory = productionHistoryDatabaseRepository.findLastByOrderId(
                orderId = orderId
            )

            productionHistory.`should be null`()
        }
    }

    private fun buildProductionHistoryEntity(id: ProductionHistoryId, orderId: OrderId) = ProductHistoryEntity(
        id = id.value,
        orderId = orderId.value,
        status = ProductionStatus.RECEIVED.value,
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now(),
    )

    private fun buildProductionHistory(id: ProductionHistoryId, orderId: OrderId) = ProductionHistory.new(
        id = id,
        orderId = orderId,
        status = ProductionStatus.RECEIVED,
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now(),
    ).getOrElse { throw it }
}