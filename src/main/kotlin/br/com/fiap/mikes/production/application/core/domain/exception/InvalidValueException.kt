package br.com.fiap.mikes.production.application.core.domain.exception

open class InvalidValueException(val type: String, message: String) : Exception(message)
