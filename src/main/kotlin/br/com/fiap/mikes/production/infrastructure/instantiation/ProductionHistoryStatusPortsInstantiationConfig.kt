package br.com.fiap.mikes.production.infrastructure.instantiation

import br.com.fiap.mikes.production.adapter.outbound.database.ProductionHistoryStatusDatabaseRepository
import br.com.fiap.mikes.production.adapter.outbound.database.jpa.ProductionHistoryStatusJpaRepository
import br.com.fiap.mikes.production.application.core.usecase.productionhistorystatus.ValidateProductionHistoryStatusUseCase
import br.com.fiap.mikes.production.application.mapper.productionhistory.ProductionHistoryStatusMapper
import br.com.fiap.mikes.production.application.mapper.productionhistorystatus.DefaultProductionHistoryStatusDomainMapper
import br.com.fiap.mikes.production.application.port.inbound.productionhistorystatus.ValidateProductionHistoryStatusService
import br.com.fiap.mikes.production.application.port.outbound.productionhistorystatus.ProductionHistoryStatusRepository
import jakarta.transaction.Transactional
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ProductionHistoryStatusPortsInstantiationConfig {

    @Bean
    fun productionHistoryStatusDomainMapper(): ProductionHistoryStatusMapper {
        return DefaultProductionHistoryStatusDomainMapper()
    }

    @Bean
    fun productionHistoryStatusRepository(productHistoryStatusJpaRepository: ProductionHistoryStatusJpaRepository): ProductionHistoryStatusRepository {
        return (@Transactional object : ProductionHistoryStatusDatabaseRepository(productHistoryStatusJpaRepository) {})
    }

    @Bean
    fun validateProductionHistoryStatusService(
        productionHistoryStatusRepository: ProductionHistoryStatusRepository,
    ): ValidateProductionHistoryStatusService {
        return ValidateProductionHistoryStatusUseCase(productionHistoryStatusRepository)
    }
}