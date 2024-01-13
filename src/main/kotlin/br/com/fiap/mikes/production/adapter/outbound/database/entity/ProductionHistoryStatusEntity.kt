package br.com.fiap.mikes.production.adapter.outbound.database.entity

import br.com.fiap.mikes.production.application.port.outbound.productionhistorystatus.dto.ProductionHistoryStatusOutboundResponse
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id

@Entity(name = "historico_producao_status")
data class ProductionHistoryStatusEntity(

    @Id
    @Column(name = "status", length = 50)
    val status: String,

    @Column(name = "proximo_status", length = 50)
    val nextStatus: String
) {

    fun toOutbound(): ProductionHistoryStatusOutboundResponse {
        return ProductionHistoryStatusOutboundResponse(
            status = status,
            nextStatus = nextStatus
        )
    }
}