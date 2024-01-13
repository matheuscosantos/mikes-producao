package br.com.fiap.mikes.production.infrastructure.instantiation

import br.com.fiap.mikes.production.adapter.outbound.database.ProductionHistoryDatabaseRepository
import br.com.fiap.mikes.production.adapter.outbound.database.jpa.ProductHistoryJpaRepository
import br.com.fiap.mikes.production.application.core.usecase.productionhistory.FindProductionHistoryByOrderUseCase
import br.com.fiap.mikes.production.application.core.usecase.productionhistory.FindProductionHistoryLastByOrderUseCase
import br.com.fiap.mikes.production.application.mapper.productionhistory.ProductionHistoryMapper
import br.com.fiap.mikes.production.application.mapper.productionhistorystatus.DefaultProductionHistoryDomainMapper
import br.com.fiap.mikes.production.application.port.inbound.productionhistory.CreateProductionHistoryService
import br.com.fiap.mikes.production.application.port.inbound.productionhistorystatus.ValidateProductionHistoryStatusService
import br.com.fiap.mikes.production.application.port.outbound.productionhistory.ProductionHistoryRepository
import io.mockk.mockk
import org.amshove.kluent.shouldBeInstanceOf
import org.junit.jupiter.api.Test

class ProductionHistoryPortsInstantiationConfigTest {

    @Test
    fun `productionHistoryDomainMapper should return a DefaultProductionHistoryDomainMapper`() {
        ProductionHistoryPortsInstantiationConfig().productionHistoryDomainMapper() shouldBeInstanceOf
                DefaultProductionHistoryDomainMapper::class
    }

    @Test
    fun `productionHistoryRepository should return a ProductionHistoryDatabaseRepository`() {
        ProductionHistoryPortsInstantiationConfig().productionHistoryRepository(
            mockk<ProductHistoryJpaRepository>()
        ) shouldBeInstanceOf ProductionHistoryDatabaseRepository::class
    }

    @Test
    fun `createProductionHistoryService should return a ValidateProductionHistoryStatusService`() {
        ProductionHistoryPortsInstantiationConfig().createProductionHistoryService(
            mockk<ProductionHistoryMapper>(),
            mockk<ProductionHistoryRepository>(),
            mockk<ValidateProductionHistoryStatusService>()
        ) shouldBeInstanceOf CreateProductionHistoryService::class
    }

    @Test
    fun `findProductionHistoryByOrderService should return a FindProductionHistoryByOrderUseCase`() {
        ProductionHistoryPortsInstantiationConfig().findProductionHistoryByOrderService(
            mockk<ProductionHistoryMapper>(),
            mockk<ProductionHistoryRepository>()
        ) shouldBeInstanceOf FindProductionHistoryByOrderUseCase::class
    }

    @Test
    fun `findProductionHistoryLastByOrderService should return a FindProductionHistoryLastByOrderUseCase`() {
        ProductionHistoryPortsInstantiationConfig().findProductionHistoryLastByOrderService(
            mockk<ProductionHistoryMapper>(),
            mockk<ProductionHistoryRepository>()
        ) shouldBeInstanceOf FindProductionHistoryLastByOrderUseCase::class
    }
}