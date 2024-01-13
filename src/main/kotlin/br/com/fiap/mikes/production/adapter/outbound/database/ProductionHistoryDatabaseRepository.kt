package br.com.fiap.mikes.production.adapter.outbound.database

import br.com.fiap.mikes.production.adapter.outbound.database.entity.ProductHistoryEntity
import br.com.fiap.mikes.production.adapter.outbound.database.jpa.ProductHistoryJpaRepository
import br.com.fiap.mikes.production.application.core.domain.ProductionHistory
import br.com.fiap.mikes.production.application.core.domain.valueobject.OrderId
import br.com.fiap.mikes.production.application.port.outbound.productionhistory.ProductionHistoryRepository
import br.com.fiap.mikes.production.application.port.outbound.productionhistory.dto.ProductionHistoryOutboundResponse
import kotlin.jvm.optionals.getOrNull

open class ProductionHistoryDatabaseRepository(private val productionHistoryJpaRepository: ProductHistoryJpaRepository) :
    ProductionHistoryRepository {

    override fun save(productionHistory: ProductionHistory): ProductionHistoryOutboundResponse {
        return productionHistoryJpaRepository.save(ProductHistoryEntity.from(productionHistory)).toOutbound()
    }

    override fun findByOrderId(orderId: OrderId): List<ProductionHistoryOutboundResponse> {
        return productionHistoryJpaRepository.findByOrderId(orderId.value).map { it.toOutbound() }
    }

    override fun findLastByOrderId(orderId: OrderId): ProductionHistoryOutboundResponse? {
        return productionHistoryJpaRepository.findFirstByOrderIdOrderByCreatedAtDesc(orderId.value).map {
            it.toOutbound()
        }.getOrNull()
    }
}
