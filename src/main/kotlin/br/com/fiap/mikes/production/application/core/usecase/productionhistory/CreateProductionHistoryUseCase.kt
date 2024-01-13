package br.com.fiap.mikes.production.application.core.usecase.productionhistory

import br.com.fiap.mikes.production.application.core.domain.ProductionHistory
import br.com.fiap.mikes.production.application.core.domain.valueobject.OrderId
import br.com.fiap.mikes.production.application.core.domain.valueobject.ProductionHistoryId
import br.com.fiap.mikes.production.application.core.domain.valueobject.ProductionStatus
import br.com.fiap.mikes.production.application.core.usecase.productionhistory.exception.InvalidProductionHistoryStateException
import br.com.fiap.mikes.production.application.core.usecase.productionhistory.exception.NotAllowedStatusToProductionHistoryException
import br.com.fiap.mikes.production.application.mapper.productionhistory.ProductionHistoryMapper
import br.com.fiap.mikes.production.application.port.inbound.productionhistory.CreateProductionHistoryService
import br.com.fiap.mikes.production.application.port.inbound.productionhistory.dto.CreateProductionHistoryInboundRequest
import br.com.fiap.mikes.production.application.port.inbound.productionhistorystatus.ValidateProductionHistoryStatusService
import br.com.fiap.mikes.production.application.port.outbound.productionhistory.ProductionHistoryRepository
import br.com.fiap.mikes.production.application.port.outbound.productionhistory.dto.ProductionHistoryOutboundResponse
import br.com.fiap.mikes.production.util.flatMap
import br.com.fiap.mikes.production.util.mapFailure
import java.time.LocalDateTime

class CreateProductionHistoryUseCase(
    private val productionHistoryMapper: ProductionHistoryMapper,
    private val productionHistoryRepository: ProductionHistoryRepository,
    private val validateProductionHistoryStatusService: ValidateProductionHistoryStatusService
) : CreateProductionHistoryService {

    override fun invoke(createProductionHistoryInboundRequest: CreateProductionHistoryInboundRequest): Result<ProductionHistory> {
        return createProductionHistoryInboundRequest
            .toProductionHistory()
            .validateStatus()
            .saveProductionHistory()
    }

    private fun CreateProductionHistoryInboundRequest.toProductionHistory(): Result<ProductionHistory> {
        val orderId = OrderId.new(orderId).getOrElse { return Result.failure(it) }
        val status = ProductionStatus.findByValue(status).getOrElse { return Result.failure(it) }

        return productionHistoryMapper.new(
            id = ProductionHistoryId.generate(),
            orderId = orderId,
            status = status,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
        )
    }

    private fun Result<ProductionHistory>.validateStatus(): Result<ProductionHistory> =
        map { newProductionHistory ->
            productionHistoryRepository.findLastByOrderId(newProductionHistory.orderId)?.let {

                val lastProductionHistory = it.toProductionHistory().getOrElse {
                    return Result.failure(InvalidProductionHistoryStateException("product history in invalid state."))
                }

                return validateProductionHistoryStatusService(newProductionHistory, lastProductionHistory)
            }

            if (newProductionHistory.status != ProductionStatus.RECEIVED) {
                return Result.failure(NotAllowedStatusToProductionHistoryException("status not allowed"))
            }

            return Result.success(newProductionHistory)
        }

    private fun Result<ProductionHistory>.saveProductionHistory(): Result<ProductionHistory> {
        return flatMap {
            productionHistoryRepository.save(it)
                .toProductionHistory()
                .mapFailure { InvalidProductionHistoryStateException("production history in invalid state.") }
        }
    }

    private fun ProductionHistoryOutboundResponse?.toProductionHistory(): Result<ProductionHistory> {
        return if (this != null)
            productionHistoryMapper.new(this)
        else
            Result.failure(InvalidProductionHistoryStateException("production history in invalid state."))
    }
}
