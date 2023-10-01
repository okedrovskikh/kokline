package kek.team.kokline.routing.websocket

import io.ktor.server.routing.Route
import io.ktor.server.websocket.webSocket
import io.ktor.utils.io.CancellationException
import io.ktor.websocket.CloseReason
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import kek.team.kokline.factories.ChatProcessorFactory
import kek.team.kokline.factories.coroutinePool
import kek.team.kokline.service.IncomingMessageConsumer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

private val consumer = IncomingMessageConsumer()

fun Route.chatRouting() {
    webSocket("/joinChat") {
        val id = call.request.headers["x-id"] ?: close(CloseReason(CloseReason.Codes.CANNOT_ACCEPT, "No header x-id"))
        val processor = ChatProcessorFactory.createProcessor({ consumer.receiveEvent() }) { if (it == id) send(Frame.Text(it)) }

        val job = CoroutineScope(coroutineContext + coroutinePool).launch {
            while (true) processor.process()
        }

        closeReason.invokeOnCompletion { job.cancel(CancellationException("Connection closed")); println("Im here") }

        job.join()
    }
}
