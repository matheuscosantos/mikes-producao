package br.com.fiap.mikes.production.adapter.inbound.entrypoint.controller.dto

import br.com.fiap.mikes.production.application.port.inbound.productionhistory.dto.CreateProductionHistoryInboundRequest

data class ProductionHistoryRequestDto(
    val orderId: String,
    val status: String,
) {
    fun toInbound(): CreateProductionHistoryInboundRequest {
        return CreateProductionHistoryInboundRequest(
            orderId = orderId,
            status = status
        )
    }
}