package br.com.fiap.mikes.production.application.core.domain.valueobject

import br.com.fiap.mikes.production.application.core.domain.exception.InvalidOrderIdException
import java.util.UUID

@JvmInline
value class OrderId private constructor(val value: String) {
    companion object {
        fun new(value: String): Result<OrderId> {
            val uuid = runCatching { UUID.fromString(value) }
                .getOrElse { return Result.failure(InvalidOrderIdException("invalid order id.")) }

            return Result.success(OrderId(uuid.toString()))
        }
    }
}
