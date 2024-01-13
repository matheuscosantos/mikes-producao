package br.com.fiap.mikes.production.application.core.usecase.productionhistory.exception

import br.com.fiap.mikes.production.application.core.usecase.exception.NotFoundException

class ProductionHistoryNotFoundException(message: String) : NotFoundException(TYPE, message) {
    companion object {
        private const val TYPE = "ProductionHistory"
    }
}
