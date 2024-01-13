package br.com.fiap.mikes.production.adapter.outbound.database.jpa

import br.com.fiap.mikes.production.adapter.outbound.database.entity.ProductionHistoryStatusEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface ProductionHistoryStatusJpaRepository : JpaRepository<ProductionHistoryStatusEntity, String> {
    fun findByStatus(status: String): Optional<ProductionHistoryStatusEntity>
}