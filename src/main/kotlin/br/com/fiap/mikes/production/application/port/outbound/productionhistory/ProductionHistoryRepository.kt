package br.com.fiap.mikes.production.application.port.outbound.productionhistory

import br.com.fiap.mikes.production.application.core.domain.ProductionHistory
import br.com.fiap.mikes.production.application.core.domain.valueobject.OrderId
import br.com.fiap.mikes.production.application.port.outbound.productionhistory.dto.ProductionHistoryOutboundResponse

interface ProductionHistoryRepository {
    fun save(productionHistory: ProductionHistory): ProductionHistoryOutboundResponse
    fun findByOrderId(orderId: OrderId): List<ProductionHistoryOutboundResponse>
    fun findLastByOrderId(orderId: OrderId): ProductionHistoryOutboundResponse?
}
