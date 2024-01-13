package br.com.fiap.mikes.production.application.core.domain.valueobject

import br.com.fiap.mikes.production.application.core.domain.exception.InvalidOrderIdException
import java.util.UUID

@JvmInline
value class ProductionHistoryId private constructor(val value: String) {
    companion object {
        fun new(value: String): Result<ProductionHistoryId> {
            val uuid = runCatching { UUID.fromString(value) }
                .getOrElse { return Result.failure(InvalidOrderIdException("invalid production history id.")) }

            return Result.success(ProductionHistoryId(uuid.toString()))
        }

        fun generate() = ProductionHistoryId(UUID.randomUUID().toString())
    }
}