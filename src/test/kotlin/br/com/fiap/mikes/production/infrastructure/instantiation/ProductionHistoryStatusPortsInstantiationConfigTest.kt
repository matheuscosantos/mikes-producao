package br.com.fiap.mikes.production.infrastructure.instantiation

import br.com.fiap.mikes.production.adapter.outbound.database.ProductionHistoryStatusDatabaseRepository
import br.com.fiap.mikes.production.adapter.outbound.database.jpa.ProductionHistoryStatusJpaRepository
import br.com.fiap.mikes.production.application.core.usecase.productionhistorystatus.ValidateProductionHistoryStatusUseCase
import br.com.fiap.mikes.production.application.mapper.productionhistorystatus.DefaultProductionHistoryStatusDomainMapper
import br.com.fiap.mikes.production.application.port.outbound.productionhistorystatus.ProductionHistoryStatusRepository
import io.mockk.mockk
import org.amshove.kluent.shouldBeInstanceOf
import org.junit.jupiter.api.Test

class ProductionHistoryStatusPortsInstantiationConfigTest {

    @Test
    fun `productionHistoryStatusDomainMapper should return a DefaultProductionHistoryStatusDomainMapper`() {
        ProductionHistoryStatusPortsInstantiationConfig().productionHistoryStatusDomainMapper() shouldBeInstanceOf
                DefaultProductionHistoryStatusDomainMapper::class
    }

    @Test
    fun `productionHistoryStatusRepository should return a ProductionHistoryStatusDatabaseRepository`() {
        ProductionHistoryStatusPortsInstantiationConfig().productionHistoryStatusRepository(
            mockk<ProductionHistoryStatusJpaRepository>()
        ) shouldBeInstanceOf ProductionHistoryStatusDatabaseRepository::class
    }

    @Test
    fun `validateProductionHistoryStatusService should return a ValidateProductionHistoryStatusUseCase`() {
        ProductionHistoryStatusPortsInstantiationConfig().validateProductionHistoryStatusService(
            mockk<ProductionHistoryStatusRepository>()
        ) shouldBeInstanceOf ValidateProductionHistoryStatusUseCase::class
    }
}