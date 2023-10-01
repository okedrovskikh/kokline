package kek.team.kokline

import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import kek.team.kokline.configurations.configureRouting
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest {

    @Test
    fun `should response 200`() = testApplication {
        application {
            configureRouting()
        }
        client.get("/health").apply {
            assertEquals(HttpStatusCode.OK, status)
        }
    }
}
