package br.com.fiap.mikes.production.adapter.outbound.database.entity

import br.com.fiap.mikes.production.application.core.domain.ProductionHistory
import br.com.fiap.mikes.production.application.port.outbound.productionhistory.dto.ProductionHistoryOutboundResponse
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import java.time.LocalDateTime

@Entity(name = "historico_producao")
data class ProductHistoryEntity(
    @Id
    @Column(name = "id", length = 36)
    val id: String,

    @Column(name = "id_pedido", length = 36)
    val orderId: String,

    @Column(name = "status", length = 50)
    val status: String,

    @Column(name = "criado_em")
    val createdAt: LocalDateTime,

    @Column(name = "atualizado_em")
    val updatedAt: LocalDateTime,
) {

    fun toOutbound(): ProductionHistoryOutboundResponse {
        return ProductionHistoryOutboundResponse(
            id = id,
            orderId = orderId,
            status = status,
            createdAt = createdAt,
            updatedAt = updatedAt,
        )
    }

    companion object {
        fun from(productionHistory: ProductionHistory): ProductHistoryEntity = productionHistory.run {
            ProductHistoryEntity(
                id = id.value,
                orderId = orderId.value,
                status = status.value,
                createdAt = createdAt,
                updatedAt = updatedAt
            )
        }
    }
}