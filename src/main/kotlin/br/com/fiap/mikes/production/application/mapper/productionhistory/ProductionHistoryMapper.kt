package br.com.fiap.mikes.production.application.mapper.productionhistory

import br.com.fiap.mikes.production.application.core.domain.ProductionHistory
import br.com.fiap.mikes.production.application.core.domain.valueobject.OrderId
import br.com.fiap.mikes.production.application.core.domain.valueobject.ProductionHistoryId
import br.com.fiap.mikes.production.application.core.domain.valueobject.ProductionStatus
import br.com.fiap.mikes.production.application.port.outbound.productionhistory.dto.ProductionHistoryOutboundResponse
import java.time.LocalDateTime

interface ProductionHistoryMapper {
    fun new(
        id: ProductionHistoryId,
        orderId: OrderId,
        status: ProductionStatus,
        createdAt: LocalDateTime,
        updatedAt: LocalDateTime,
    ): Result<ProductionHistory>

    fun new(productionHistoryOutboundResponse: ProductionHistoryOutboundResponse): Result<ProductionHistory>
}
