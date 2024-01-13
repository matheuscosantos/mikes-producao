package br.com.fiap.mikes.production.application.port.outbound.productionhistory

import br.com.fiap.mikes.production.application.port.outbound.productionhistory.dto.ProductionHistorySentMessage

fun interface ProductionHistorySentMessenger {
    fun send(productionHistorySentMessage: ProductionHistorySentMessage)
}