package br.com.fiap.mikes.production

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ProductionApplication

fun main(args: Array<String>) {
	runApplication<ProductionApplication>(*args)
}
