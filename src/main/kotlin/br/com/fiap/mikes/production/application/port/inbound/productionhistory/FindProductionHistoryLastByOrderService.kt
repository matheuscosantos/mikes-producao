package br.com.fiap.mikes.production.application.port.inbound.productionhistory

import br.com.fiap.mikes.production.application.core.domain.ProductionHistory

fun interface FindProductionHistoryLastByOrderService {
    operator fun invoke(orderId: String): Result<ProductionHistory>
}