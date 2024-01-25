package br.com.fiap.mikes.production.adapter.inbound.controller.exceptionhandler

import br.com.fiap.mikes.production.adapter.inbound.entrypoint.controller.exceptionhandler.ControllerExceptionHandler
import br.com.fiap.mikes.production.application.core.domain.exception.InvalidValueException
import br.com.fiap.mikes.production.application.core.usecase.exception.AlreadyExistsException
import br.com.fiap.mikes.production.application.core.usecase.exception.InvalidDomainStateException
import br.com.fiap.mikes.production.application.core.usecase.exception.NotFoundException
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.context.request.WebRequest

class ControllerExceptionHandlerTest {

    private val webRequest: WebRequest = mockk(relaxed = true)
    private val controllerExceptionHandler = ControllerExceptionHandler()

    @Test
    fun testInvalidValueExceptionHandler() {
        val invalidValueException = InvalidValueException("Invalid value", "fieldName")

        val responseEntity: ResponseEntity<ControllerExceptionHandler.Error> =
            controllerExceptionHandler.invalidValueExceptionHandler(invalidValueException, webRequest)

        assertErrorResponse(
            responseEntity,
            HttpStatus.BAD_REQUEST,
            ControllerExceptionHandler.TYPE_INVALID_VALUE_EXCEPTION
        )
    }

    @Test
    fun testNotFoundExceptionHandler() {
        val notFoundException = NotFoundException("Entity", "123")

        val responseEntity: ResponseEntity<ControllerExceptionHandler.Error> =
            controllerExceptionHandler.notFoundExceptionHandler(notFoundException, webRequest)

        assertErrorResponse(
            responseEntity,
            HttpStatus.NOT_FOUND,
            ControllerExceptionHandler.TYPE_NOT_FOUND_EXCEPTION
        )
    }

    @Test
    fun testAlreadyExistsExceptionHandler() {
        val alreadyExistsException = AlreadyExistsException("Entity", "123")

        val responseEntity: ResponseEntity<ControllerExceptionHandler.Error> =
            controllerExceptionHandler.alreadyExistsExceptionHandler(alreadyExistsException, webRequest)

        assertErrorResponse(
            responseEntity,
            HttpStatus.CONFLICT,
            ControllerExceptionHandler.TYPE_ALREADY_EXISTS_EXCEPTION
        )
    }

    @Test
    fun testInvalidDomainStateExceptionHandler() {
        val invalidDomainStateException = InvalidDomainStateException("Entity", "Invalid state")

        val responseEntity: ResponseEntity<ControllerExceptionHandler.Error> =
            controllerExceptionHandler.invalidDomainStateExceptionHandler(invalidDomainStateException, webRequest)

        assertErrorResponse(
            responseEntity,
            HttpStatus.PRECONDITION_FAILED,
            ControllerExceptionHandler.TYPE_INVALID_DOMAIN_STATE_EXCEPTION
        )
    }

    private fun assertErrorResponse(
        responseEntity: ResponseEntity<ControllerExceptionHandler.Error>,
        expectedStatus: HttpStatus,
        expectedErrorType: String
    ) {
        assertEquals(expectedStatus, responseEntity.statusCode)
        assertEquals(expectedErrorType, responseEntity.body?.error)
    }
}
