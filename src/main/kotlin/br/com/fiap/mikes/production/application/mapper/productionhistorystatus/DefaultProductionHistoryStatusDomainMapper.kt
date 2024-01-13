package br.com.fiap.mikes.production.application.mapper.productionhistorystatus

import br.com.fiap.mikes.production.application.core.domain.ProductionHistoryStatus
import br.com.fiap.mikes.production.application.core.domain.valueobject.ProductionStatus
import br.com.fiap.mikes.production.application.mapper.productionhistory.ProductionHistoryStatusMapper
import br.com.fiap.mikes.production.application.port.outbound.productionhistorystatus.dto.ProductionHistoryStatusOutboundResponse

class DefaultProductionHistoryStatusDomainMapper : ProductionHistoryStatusMapper {
    override fun new(status: ProductionStatus, nextStatus: ProductionStatus): Result<ProductionHistoryStatus> {
        return ProductionHistoryStatus.new(status, nextStatus)
    }

    override fun new(productionHistoryStatusOutboundResponse: ProductionHistoryStatusOutboundResponse): Result<ProductionHistoryStatus> =
        with(productionHistoryStatusOutboundResponse) {
            val newStatus = ProductionStatus.findByValue(status).getOrElse { return Result.failure(it) }
            val newNextStatus = ProductionStatus.findByValue(nextStatus).getOrElse { return Result.failure(it) }

            return ProductionHistoryStatus.new(newStatus, newNextStatus)
        }
}