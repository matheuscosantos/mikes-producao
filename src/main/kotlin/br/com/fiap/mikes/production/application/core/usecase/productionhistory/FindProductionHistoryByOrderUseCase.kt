package br.com.fiap.mikes.production.application.core.usecase.productionhistory

import br.com.fiap.mikes.production.application.core.domain.ProductionHistory
import br.com.fiap.mikes.production.application.core.domain.valueobject.OrderId
import br.com.fiap.mikes.production.application.core.usecase.productionhistory.exception.InvalidProductionHistoryStateException
import br.com.fiap.mikes.production.application.mapper.productionhistory.ProductionHistoryMapper
import br.com.fiap.mikes.production.application.port.inbound.productionhistory.FindProductionHistoryByOrderService
import br.com.fiap.mikes.production.application.port.outbound.productionhistory.ProductionHistoryRepository
import br.com.fiap.mikes.production.application.port.outbound.productionhistory.dto.ProductionHistoryOutboundResponse

class FindProductionHistoryByOrderUseCase(
    private val productionHistoryMapper: ProductionHistoryMapper,
    private val productionHistoryRepository: ProductionHistoryRepository,
) : FindProductionHistoryByOrderService {

    override fun invoke(orderId: String): Result<List<ProductionHistory>> {
        val newOrderId = OrderId.new(orderId).getOrElse { return Result.failure(it) }

        return Result.success(
            productionHistoryRepository.findByOrderId(newOrderId).map {
                it.toProductionHistory().getOrElse {
                    return Result.failure(InvalidProductionHistoryStateException("product history in invalid state."))
                }
            }
        )
    }

    private fun ProductionHistoryOutboundResponse.toProductionHistory(): Result<ProductionHistory> {
        return productionHistoryMapper.new(this)
    }
}