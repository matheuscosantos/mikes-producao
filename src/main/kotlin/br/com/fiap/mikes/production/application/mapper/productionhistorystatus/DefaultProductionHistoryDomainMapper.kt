package br.com.fiap.mikes.production.application.mapper.productionhistorystatus

import br.com.fiap.mikes.production.application.core.domain.ProductionHistory
import br.com.fiap.mikes.production.application.core.domain.valueobject.OrderId
import br.com.fiap.mikes.production.application.core.domain.valueobject.ProductionHistoryId
import br.com.fiap.mikes.production.application.core.domain.valueobject.ProductionStatus
import br.com.fiap.mikes.production.application.mapper.productionhistory.ProductionHistoryMapper
import br.com.fiap.mikes.production.application.port.outbound.productionhistory.dto.ProductionHistoryOutboundResponse
import java.time.LocalDateTime

class DefaultProductionHistoryDomainMapper : ProductionHistoryMapper {

    override fun new(
        id: ProductionHistoryId,
        orderId: OrderId,
        status: ProductionStatus,
        createdAt: LocalDateTime,
        updatedAt: LocalDateTime
    ): Result<ProductionHistory> {
        return ProductionHistory.new(
            id = id,
            orderId = orderId,
            status = status,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
        )
    }

    override fun new(productionHistoryOutboundResponse: ProductionHistoryOutboundResponse): Result<ProductionHistory> =
        with(productionHistoryOutboundResponse) {
            val id = ProductionHistoryId.new(id).getOrElse { return Result.failure(it) }
            val orderId = OrderId.new(orderId).getOrElse { return Result.failure(it) }
            val status = ProductionStatus.findByValue(status).getOrElse { return Result.failure(it) }

            return ProductionHistory.new(
                id = id,
                orderId = orderId,
                status = status,
                createdAt = createdAt,
                updatedAt = updatedAt,
            )
        }
}