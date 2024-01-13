package br.com.fiap.mikes.production.application.core.usecase.productionhistory.exception

import br.com.fiap.mikes.production.application.core.usecase.exception.InvalidDomainStateException

class InvalidProductionHistoryStateException(message: String) : InvalidDomainStateException(TYPE, message) {
    companion object {
        private const val TYPE = "ProductionHistory"
    }
}
