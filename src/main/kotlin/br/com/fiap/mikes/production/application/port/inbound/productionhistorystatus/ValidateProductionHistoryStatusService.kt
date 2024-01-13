package br.com.fiap.mikes.production.application.port.inbound.productionhistorystatus

import br.com.fiap.mikes.production.application.core.domain.ProductionHistory

fun interface ValidateProductionHistoryStatusService {
    operator fun invoke(
        newProductionHistory: ProductionHistory,
        lastProductionHistory: ProductionHistory
    ): Result<ProductionHistory>
}