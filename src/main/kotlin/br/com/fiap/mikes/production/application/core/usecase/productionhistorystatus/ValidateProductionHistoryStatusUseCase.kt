package br.com.fiap.mikes.production.application.core.usecase.productionhistorystatus

import br.com.fiap.mikes.production.application.core.domain.ProductionHistory
import br.com.fiap.mikes.production.application.core.domain.valueobject.ProductionStatus
import br.com.fiap.mikes.production.application.core.usecase.productionhistory.exception.NotAllowedStatusToProductionHistoryException
import br.com.fiap.mikes.production.application.core.usecase.productionhistorystatus.exception.ProductionHistoryStatusNotFoundException
import br.com.fiap.mikes.production.application.port.inbound.productionhistorystatus.ValidateProductionHistoryStatusService
import br.com.fiap.mikes.production.application.port.outbound.productionhistorystatus.ProductionHistoryStatusRepository

class ValidateProductionHistoryStatusUseCase(
    private val productionHistoryStatusRepository: ProductionHistoryStatusRepository
) : ValidateProductionHistoryStatusService {

    override fun invoke(
        newProductionHistory: ProductionHistory,
        lastProductionHistory: ProductionHistory
    ): Result<ProductionHistory> =
        productionHistoryStatusRepository.findByStatus(lastProductionHistory.status)?.let {
            val nextStatus = ProductionStatus.findByValue(it.nextStatus).getOrNull()

            if (newProductionHistory.status == lastProductionHistory.status || newProductionHistory.status != nextStatus) {
                return Result.failure(NotAllowedStatusToProductionHistoryException("status not allowed"))
            }

            return Result.success(newProductionHistory)

        } ?: Result.failure(ProductionHistoryStatusNotFoundException("Production history status not found"))
}