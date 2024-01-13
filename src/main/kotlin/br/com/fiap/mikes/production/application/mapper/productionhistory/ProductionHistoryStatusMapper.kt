package br.com.fiap.mikes.production.application.mapper.productionhistory

import br.com.fiap.mikes.production.application.core.domain.ProductionHistoryStatus
import br.com.fiap.mikes.production.application.core.domain.valueobject.ProductionStatus
import br.com.fiap.mikes.production.application.port.outbound.productionhistorystatus.dto.ProductionHistoryStatusOutboundResponse

interface ProductionHistoryStatusMapper {
    fun new(status: ProductionStatus, nextStatus: ProductionStatus): Result<ProductionHistoryStatus>
    fun new(productionHistoryStatusOutboundResponse: ProductionHistoryStatusOutboundResponse): Result<ProductionHistoryStatus>
}
