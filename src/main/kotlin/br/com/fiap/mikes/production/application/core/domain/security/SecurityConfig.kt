package br.com.fiap.mikes.production.application.core.domain.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class SecurityConfig {

    @Bean
    fun allowAllSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .authorizeHttpRequests { authorize ->
                authorize
                    .requestMatchers("/**").permitAll() // Permite acesso a todos os endpoints
            }
            // Configurações de cabeçalho adicionadas aqui
            .headers { headers ->
                headers
                    .contentTypeOptions { cto ->
                        cto.disable()
                    }
                    .httpStrictTransportSecurity { hsts ->
                        hsts.maxAgeInSeconds(31536000)
                        hsts.includeSubDomains(true)
                    }
                    .contentSecurityPolicy { contentSecurityPolicy ->
                        contentSecurityPolicy
                            .policyDirectives("script-src 'self'")
                    }
            }

        return http.build()
    }
}
