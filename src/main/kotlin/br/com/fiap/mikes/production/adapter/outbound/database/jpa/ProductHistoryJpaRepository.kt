package br.com.fiap.mikes.production.adapter.outbound.database.jpa

import br.com.fiap.mikes.production.adapter.outbound.database.entity.ProductHistoryEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface ProductHistoryJpaRepository : JpaRepository<ProductHistoryEntity, String> {
    fun findByOrderId(orderId: String): List<ProductHistoryEntity>
    fun findFirstByOrderIdOrderByCreatedAtDesc(orderId: String): Optional<ProductHistoryEntity>
}