package br.com.fiap.mikes.production.application.core.domain

import br.com.fiap.mikes.production.application.core.domain.valueobject.ProductionStatus

class ProductionHistoryStatus private constructor(
    val status: ProductionStatus,
    val nextStatus: ProductionStatus
) {
    companion object {
        fun new(
            status: ProductionStatus,
            nextStatus: ProductionStatus
        ): Result<ProductionHistoryStatus> = Result.success(ProductionHistoryStatus(status, nextStatus))
    }
}