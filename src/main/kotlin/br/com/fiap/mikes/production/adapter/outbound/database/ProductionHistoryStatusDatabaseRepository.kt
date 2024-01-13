package br.com.fiap.mikes.production.adapter.outbound.database

import br.com.fiap.mikes.production.adapter.outbound.database.jpa.ProductionHistoryStatusJpaRepository
import br.com.fiap.mikes.production.application.core.domain.valueobject.ProductionStatus
import br.com.fiap.mikes.production.application.port.outbound.productionhistorystatus.ProductionHistoryStatusRepository
import br.com.fiap.mikes.production.application.port.outbound.productionhistorystatus.dto.ProductionHistoryStatusOutboundResponse
import kotlin.jvm.optionals.getOrNull

open class ProductionHistoryStatusDatabaseRepository(
    private val productionHistoryStatusJpaRepository: ProductionHistoryStatusJpaRepository
) : ProductionHistoryStatusRepository {

    override fun findByStatus(status: ProductionStatus): ProductionHistoryStatusOutboundResponse? {
        return productionHistoryStatusJpaRepository.findByStatus(status.value).map { it.toOutbound() }.getOrNull()
    }
}