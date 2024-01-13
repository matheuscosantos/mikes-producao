package br.com.fiap.mikes.production.application.core.domain.exception

class InvalidOrderIdException(message: String) : InvalidValueException(TYPE, message) {
    companion object {
        private const val TYPE = "OrderId"
    }
}
