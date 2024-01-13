package br.com.fiap.mikes.production.application.port.outbound.productionhistorystatus.dto

data class ProductionHistoryStatusOutboundResponse(
    val status: String,
    val nextStatus: String
)