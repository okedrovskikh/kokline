package kek.team.kokline.security.sessions

import kek.team.kokline.exceptions.NoSessionInContextException
import kek.team.kokline.models.Connection
import kotlinx.coroutines.ThreadContextElement
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

class ConnectionContext(val connection: Connection) : AbstractCoroutineContextElement(Key), ThreadContextElement<Connection> {
    companion object Key : CoroutineContext.Key<ConnectionContext>

    private val authSessionThreadLocal = ThreadLocal.withInitial { connection }

    override fun restoreThreadContext(context: CoroutineContext, oldState: Connection) {
        authSessionThreadLocal.set(oldState)
    }

    override fun updateThreadContext(context: CoroutineContext): Connection {
        val oldState = authSessionThreadLocal.get()
        authSessionThreadLocal.set(connection)
        return oldState
    }

    override fun toString(): String = "ConnectionContext(${connection})"
}

/**
 * Return stored in coroutine context auth data, throw exception if no auth context
 */
suspend fun authSession(): AuthSession = coroutineContext[ConnectionContext.Key]?.connection?.session()
    ?: throw NoSessionInContextException()
