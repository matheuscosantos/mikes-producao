package br.com.fiap.mikes.production.application.core.domain.valueobject

import br.com.fiap.mikes.production.application.core.domain.exception.InvalidProductionStatusException

enum class ProductionStatus(val value: String) {
    RECEIVED("received"),
    PREPARING("preparing"),
    READY("ready"),
    FINISHED("finished"),
    ;

    companion object {
        fun findByValue(value: String): Result<ProductionStatus> {
            val orderStatus = entries.firstOrNull { it.value == value }
                ?: return Result.failure(InvalidProductionStatusException("Invalid production status: '$value'."))

            return Result.success(orderStatus)
        }
    }
}