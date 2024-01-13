package br.com.fiap.mikes.production.application.core.domain

import br.com.fiap.mikes.production.application.core.domain.valueobject.OrderId
import br.com.fiap.mikes.production.application.core.domain.valueobject.ProductionHistoryId
import br.com.fiap.mikes.production.application.core.domain.valueobject.ProductionStatus
import java.time.LocalDateTime

class ProductionHistory private constructor(
    val id: ProductionHistoryId,
    val orderId: OrderId,
    val status: ProductionStatus,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
) {
    companion object {
        fun new(
            id: ProductionHistoryId,
            orderId: OrderId,
            status: ProductionStatus,
            createdAt: LocalDateTime,
            updatedAt: LocalDateTime,
        ): Result<ProductionHistory> = Result.success(ProductionHistory(id, orderId, status, createdAt, updatedAt))
    }
}
