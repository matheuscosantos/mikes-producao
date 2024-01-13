package br.com.fiap.mikes.production.adapter.inbound.entrypoint.controller.dto

import br.com.fiap.mikes.production.application.core.domain.ProductionHistory
import java.time.LocalDateTime

data class ProductionHistoryDto(
    val id: String,
    val orderId: String,
    val status: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
) {

    companion object {
        fun from(productionHistory: ProductionHistory): ProductionHistoryDto = with(productionHistory) {
            ProductionHistoryDto(
                id.value,
                orderId.value,
                status.value,
                createdAt,
                updatedAt,
            )
        }
    }
}