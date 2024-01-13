package br.com.fiap.mikes.production.application.port.outbound.productionhistory.dto

import java.time.LocalDateTime

data class ProductionHistoryOutboundResponse(
    val id: String,
    val orderId: String,
    val status: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
)