package kek.team.kokline.support.utils

import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.routing.Route
import io.ktor.server.websocket.DefaultWebSocketServerSession
import io.ktor.util.pipeline.PipelineContext
import kek.team.kokline.models.Connection
import kek.team.kokline.security.sessions.ConnectionContext
import kek.team.kokline.security.sessions.AuthSession
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import kotlin.reflect.KFunction2
import kotlin.reflect.KFunction3

/**
 * Stores auth data in coroutine context, so it can be accessed in any place in chain of calls
 */
fun Route.authAndCallMethod(
    method: KFunction2<String, suspend PipelineContext<Unit, ApplicationCall>.(Unit) -> Unit, Route>,
    path: String,
    body: suspend PipelineContext<Unit, ApplicationCall>.(Unit) -> Unit,
): Route {
    val extendedBody: suspend PipelineContext<Unit, ApplicationCall>.(Unit) -> Unit = {
        val defaultWebSocketServerSession = this
        val connection = Connection(call)
        // check if auth session in start of call
        connection.session<AuthSession>()
        withContext(ConnectionContext(connection)) { body(defaultWebSocketServerSession, Unit) }
    }
    return method.invoke(path, extendedBody)
}

/**
 * Stores auth data in coroutine context, so it can be accessed in any place in chain of calls
 */
fun Route.authAndCallMethod(
    method: KFunction3<String, String?, suspend DefaultWebSocketServerSession.() -> Unit, Unit>,
    path: String,
    protocol: String? = null,
    body: suspend DefaultWebSocketServerSession.() -> Unit,
) {
    val extendedBody: suspend DefaultWebSocketServerSession.() -> Unit = {
        val defaultWebSocketServerSession = this
        val connection = Connection(call)
        // check if auth session in start of call
        connection.session<AuthSession>()
        withContext(ConnectionContext(connection)) { body(ExtendedWebSocketServerSession(defaultWebSocketServerSession, coroutineContext)) }
    }
    method.invoke(path, protocol, extendedBody)
}

private class ExtendedWebSocketServerSession(
    private val session: DefaultWebSocketServerSession,
    extendingCoroutineContext: CoroutineContext,
) : DefaultWebSocketServerSession by session {
    override val coroutineContext: CoroutineContext = extendingCoroutineContext + session.coroutineContext
}
