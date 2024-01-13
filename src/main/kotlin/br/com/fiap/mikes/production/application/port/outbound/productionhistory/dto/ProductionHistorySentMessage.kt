package br.com.fiap.mikes.production.application.port.outbound.productionhistory.dto

data class ProductionHistorySentMessage(
    val orderId: String,
    val status: String,
)