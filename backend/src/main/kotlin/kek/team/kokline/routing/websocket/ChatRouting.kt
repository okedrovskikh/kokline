package kek.team.kokline.routing.websocket

import com.fasterxml.jackson.databind.ObjectMapper
import io.ktor.server.routing.Route
import io.ktor.server.websocket.webSocket
import io.ktor.utils.io.CancellationException
import io.ktor.websocket.CloseReason
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import kek.team.kokline.factories.ChatProcessorFactory
import kek.team.kokline.factories.coroutinePool
import kek.team.kokline.models.Message
import kek.team.kokline.persistence.repositories.IncomingMessageRepository
import kek.team.kokline.service.IncomingMessageConsumer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.ktor.ext.inject

private val consumer = IncomingMessageConsumer()

fun Route.chatRouting() {

    val repository: IncomingMessageRepository by inject<IncomingMessageRepository>()
    val objectMapper: ObjectMapper by inject<ObjectMapper>()

    webSocket("/joinChat") {
        val id = call.request.queryParameters["id"]?.toLongOrNull() ?: close(
            CloseReason(
                code = CloseReason.Codes.CANNOT_ACCEPT,
                message = "Parameter chatId empty ot null"
            )
        )

        fun processEvent(event: String): Message? = transaction {
            repository.findById(event.toLong())
                ?.let { Message(it.id.value, it.message.payload, it.message.chat.id.value) }
                ?.takeIf { it.chatId == id }
        }

        val processor = ChatProcessorFactory.createProcessor(
            supplier = { consumer.receiveEvent() },
            mapper = { processEvent(it) },
            consumer = { if (it != null) send(Frame.Text(objectMapper.writeValueAsString(it))) }
        )

        val job = CoroutineScope(coroutineContext + coroutinePool).launch {
            while (true) processor.process()
        }

        closeReason.invokeOnCompletion { job.cancel(CancellationException("Connection closed")); println("Im here") }

        job.join()
    }
}
