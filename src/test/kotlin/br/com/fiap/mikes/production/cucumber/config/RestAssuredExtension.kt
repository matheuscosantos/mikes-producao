package br.com.fiap.mikes.production.cucumber.config

import io.restassured.RestAssured
import io.restassured.builder.RequestSpecBuilder
import io.restassured.http.ContentType
import io.restassured.response.Response
import io.restassured.response.ResponseOptions
import io.restassured.specification.RequestSpecification


object RestAssuredExtension {

    private var request: RequestSpecification = RestAssured.given().spec(
        RequestSpecBuilder()
            .setContentType(ContentType.JSON)
            .build()
    )

    fun post(url: String, body: Map<String, Any>): ResponseOptions<Response> {
        request.body(body)
        return request.post(url)
    }
}