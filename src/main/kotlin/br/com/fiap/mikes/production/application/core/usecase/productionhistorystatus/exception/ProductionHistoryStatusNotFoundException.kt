package br.com.fiap.mikes.production.application.core.usecase.productionhistorystatus.exception

import br.com.fiap.mikes.production.application.core.usecase.exception.NotFoundException

class ProductionHistoryStatusNotFoundException(message: String) : NotFoundException(TYPE, message) {
    companion object {
        private const val TYPE = "ProductionStatusHistory"
    }
}
