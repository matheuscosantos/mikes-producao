package br.com.fiap.mikes.production.adapter.inbound.entrypoint.controller

import br.com.fiap.mikes.production.adapter.inbound.entrypoint.controller.dto.ProductionHistoryDto
import br.com.fiap.mikes.production.adapter.inbound.entrypoint.controller.dto.ProductionHistoryRequestDto
import br.com.fiap.mikes.production.application.core.domain.ProductionHistory
import br.com.fiap.mikes.production.application.port.inbound.productionhistory.CreateProductionHistoryService
import br.com.fiap.mikes.production.application.port.inbound.productionhistory.FindProductionHistoryByOrderService
import br.com.fiap.mikes.production.application.port.outbound.productionhistory.ProductionHistorySentMessenger
import br.com.fiap.mikes.production.application.port.outbound.productionhistory.dto.ProductionHistorySentMessage
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/production-history")
class ProductionHistoryController(
    private val createProductionHistoryService: CreateProductionHistoryService,
    private val findProductionHistoryByOrderService: FindProductionHistoryByOrderService,
    private val productionHistorySentMessenger: ProductionHistorySentMessenger
) {

    @PostMapping
    fun createProductionHistory(@RequestBody productionHistoryRequestDto: ProductionHistoryRequestDto): ResponseEntity<ProductionHistoryDto> {
        return createProductionHistoryService(productionHistoryRequestDto.toInbound())
            .notification(productionHistoryRequestDto.orderId)
            .map { ProductionHistoryDto.from(it) }
            .map { ResponseEntity.ok(it) }
            .getOrThrow()
    }

    @GetMapping("/order/{orderId}")
    fun findByOrder(@PathVariable orderId: String): ResponseEntity<List<ProductionHistoryDto>> {
        return findProductionHistoryByOrderService(orderId)
            .map { it.map { order -> ProductionHistoryDto.from(order) } }
            .map { ResponseEntity.ok(it) }
            .getOrThrow()
    }

    private fun Result<ProductionHistory>.notification(orderId: String): Result<ProductionHistory> = onSuccess {
        productionHistorySentMessenger.send(ProductionHistorySentMessage(orderId = orderId, status = it.status.value))
        logger.info("Success to create production history: $orderId")
    }.onFailure {
        logger.error("Failed to create production history: ${it.message}")
    }


    companion object {
        private val logger: Logger = LoggerFactory.getLogger(ProductionHistoryController::class.java)
    }

}
