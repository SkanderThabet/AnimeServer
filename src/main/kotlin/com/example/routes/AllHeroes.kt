package com.example.routes

import com.example.models.ApiResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.getAllHeroes() {
    get("/boruto/heroes") {
        try {
            val page = call.request.queryParameters["page"]?.toInt() ?: 1
            require(page in 1..5) { "Heroes not found" }
            call.respond(page)
        } catch (e: NumberFormatException) {
            call.respond(
                message = ApiResponse(
                    message = "Only numbers allowed",
                    success = false,
                ),
                status = HttpStatusCode.BadRequest
            )
        } catch (e: IllegalArgumentException) {
            call.respond(
                message = ApiResponse(
                    message = e.message ?: "Unknown error",
                    success = false,
                ),
                status = HttpStatusCode.NotFound
            )
        }
    }
}