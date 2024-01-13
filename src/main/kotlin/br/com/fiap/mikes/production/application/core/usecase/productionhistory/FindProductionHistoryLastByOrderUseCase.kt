package br.com.fiap.mikes.production.application.core.usecase.productionhistory

import br.com.fiap.mikes.production.application.core.domain.ProductionHistory
import br.com.fiap.mikes.production.application.core.domain.valueobject.OrderId
import br.com.fiap.mikes.production.application.core.usecase.productionhistory.exception.ProductionHistoryNotFoundException
import br.com.fiap.mikes.production.application.mapper.productionhistory.ProductionHistoryMapper
import br.com.fiap.mikes.production.application.port.inbound.productionhistory.FindProductionHistoryLastByOrderService
import br.com.fiap.mikes.production.application.port.outbound.productionhistory.ProductionHistoryRepository

class FindProductionHistoryLastByOrderUseCase(
    private val productionHistoryMapper: ProductionHistoryMapper,
    private val productionHistoryRepository: ProductionHistoryRepository,
) : FindProductionHistoryLastByOrderService {

    override fun invoke(orderId: String): Result<ProductionHistory> {
        val newOrderId = OrderId.new(orderId).getOrElse { return Result.failure(it) }

        return productionHistoryRepository.findLastByOrderId(newOrderId)?.let { productionHistoryMapper.new(it) }
            ?: Result.failure(ProductionHistoryNotFoundException("product history not found."))
    }
}