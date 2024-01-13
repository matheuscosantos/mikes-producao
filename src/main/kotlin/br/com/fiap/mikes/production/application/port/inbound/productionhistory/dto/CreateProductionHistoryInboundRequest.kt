package br.com.fiap.mikes.production.application.port.inbound.productionhistory.dto

data class CreateProductionHistoryInboundRequest(
    val orderId: String,
    val status: String,
)