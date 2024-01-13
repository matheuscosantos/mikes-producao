package br.com.fiap.mikes.production.application.port.outbound.productionhistorystatus

import br.com.fiap.mikes.production.application.core.domain.valueobject.ProductionStatus
import br.com.fiap.mikes.production.application.port.outbound.productionhistorystatus.dto.ProductionHistoryStatusOutboundResponse

fun interface ProductionHistoryStatusRepository {
    fun findByStatus(status: ProductionStatus): ProductionHistoryStatusOutboundResponse?
}