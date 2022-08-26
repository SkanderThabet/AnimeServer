package com.example

import com.example.di.koinModule
import com.example.models.ApiResponse
import com.example.repository.HeroRepository
import com.example.repository.NEXT_PAGE_KEY
import com.example.repository.PREVIOUS_PAGE_KEY
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.java.KoinJavaComponent.inject
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest {
    private val heroRepo: HeroRepository by inject(HeroRepository::class.java)

    @Test
    fun `access root endpoint, assert correct information`() = testApplication {
        startKoin {
            modules(koinModule)
        }
        stopKoin()
        client.get("/").apply {
            assertEquals(
                expected = HttpStatusCode.OK,
                actual = status
            )
            assertEquals(
                expected = "Welcome to Boruto API",
                actual = bodyAsText()
            )

        }
    }

    @Test
    fun `access all heroes endpoint, query all pages, assert correct information`() = testApplication {
        startKoin {
            modules(koinModule)
        }

        val pages = 1..5
        val heroes = listOf(
            heroRepo.page1,
            heroRepo.page2,
            heroRepo.page3,
            heroRepo.page4,
            heroRepo.page5
        )
        environment {
            developmentMode = false
        }
        stopKoin()
        pages.forEach { page ->
            client.get("/boruto/heroes?page=$page").apply {
                assertEquals(
                    expected = HttpStatusCode.OK,
                    actual = status
                )
                stopKoin()
                val expected = ApiResponse(
                    success = true,
                    message = "ok",
                    prevPage = calculatePage(page)[PREVIOUS_PAGE_KEY],
                    nextPage = calculatePage(page)[NEXT_PAGE_KEY],
                    heroes = heroes[page - 1]
                )
                val actual = Json.decodeFromString<ApiResponse>(bodyAsText())
                println("Acutal : $actual")
                stopKoin()
                assertEquals(
                    expected = expected,
                    actual = actual
                )
                stopKoin()
            }
            stopKoin()
        }
    }

    private fun calculatePage(page: Int): Map<String, Int?> {
        return mapOf(
            PREVIOUS_PAGE_KEY to if (page in 2..5) page.minus(1) else null,
            NEXT_PAGE_KEY to if (page in 1..4) page.plus(1) else null
        )
    }


}