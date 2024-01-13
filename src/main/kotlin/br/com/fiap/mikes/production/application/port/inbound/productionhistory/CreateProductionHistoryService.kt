package br.com.fiap.mikes.production.application.port.inbound.productionhistory

import br.com.fiap.mikes.production.application.core.domain.ProductionHistory
import br.com.fiap.mikes.production.application.port.inbound.productionhistory.dto.CreateProductionHistoryInboundRequest

fun interface CreateProductionHistoryService {
    operator fun invoke(createProductionHistoryInboundRequest: CreateProductionHistoryInboundRequest): Result<ProductionHistory>
}
