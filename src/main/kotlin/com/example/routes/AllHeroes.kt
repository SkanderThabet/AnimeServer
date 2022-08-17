package com.example.routes

import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Route.getAllHeroes() {
    get("/boruto/heroes") {
        val page = call.request.queryParameters["page"]?.toInt() ?: 1
    }
}