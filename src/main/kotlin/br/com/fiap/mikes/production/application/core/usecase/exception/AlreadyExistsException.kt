package br.com.fiap.mikes.production.application.core.usecase.exception

open class AlreadyExistsException(val type: String, message: String) : Exception(message)
