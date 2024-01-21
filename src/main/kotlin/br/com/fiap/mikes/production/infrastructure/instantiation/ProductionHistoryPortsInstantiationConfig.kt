package br.com.fiap.mikes.production.infrastructure.instantiation

import br.com.fiap.mikes.production.adapter.outbound.database.ProductionHistoryDatabaseRepository
import br.com.fiap.mikes.production.adapter.outbound.database.jpa.ProductHistoryJpaRepository
import br.com.fiap.mikes.production.application.core.usecase.productionhistory.CreateProductionHistoryUseCase
import br.com.fiap.mikes.production.application.core.usecase.productionhistory.FindProductionHistoryLastByOrderUseCase
import br.com.fiap.mikes.production.application.mapper.productionhistory.ProductionHistoryMapper
import br.com.fiap.mikes.production.application.mapper.productionhistorystatus.DefaultProductionHistoryDomainMapper
import br.com.fiap.mikes.production.application.port.inbound.productionhistory.CreateProductionHistoryService
import br.com.fiap.mikes.production.application.port.inbound.productionhistory.FindProductionHistoryLastByOrderService
import br.com.fiap.mikes.production.application.port.inbound.productionhistorystatus.ValidateProductionHistoryStatusService
import br.com.fiap.mikes.production.application.port.outbound.productionhistory.ProductionHistoryRepository
import jakarta.transaction.Transactional
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ProductionHistoryPortsInstantiationConfig {

    @Bean
    fun productionHistoryDomainMapper(): ProductionHistoryMapper {
        return DefaultProductionHistoryDomainMapper()
    }

    @Bean
    fun productionHistoryRepository(productHistoryJpaRepository: ProductHistoryJpaRepository): ProductionHistoryRepository {
        return (@Transactional object : ProductionHistoryDatabaseRepository(productHistoryJpaRepository) {})
    }

    @Bean
    fun createProductionHistoryService(
        productionHistoryMapper: ProductionHistoryMapper,
        productionHistoryRepository: ProductionHistoryRepository,
        validateProductionHistoryStatusService: ValidateProductionHistoryStatusService
    ): CreateProductionHistoryService {
        return CreateProductionHistoryUseCase(
            productionHistoryMapper,
            productionHistoryRepository,
            validateProductionHistoryStatusService
        )
    }

    @Bean
    fun findProductionHistoryLastByOrderService(
        productionHistoryMapper: ProductionHistoryMapper,
        productionHistoryRepository: ProductionHistoryRepository,
    ): FindProductionHistoryLastByOrderService {
        return FindProductionHistoryLastByOrderUseCase(productionHistoryMapper, productionHistoryRepository)
    }
}
