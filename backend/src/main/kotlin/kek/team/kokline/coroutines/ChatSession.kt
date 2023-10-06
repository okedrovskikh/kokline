package kek.team.kokline.coroutines

import kotlinx.coroutines.ThreadContextElement
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

class ChatSessionContext(session: ChatSession) : AbstractCoroutineContextElement(Key), ThreadContextElement<ChatSession> {
    companion object Key : CoroutineContext.Key<ChatSessionContext>

    val session: ChatSession get() = a.get()

    private val a = ThreadLocal.withInitial { session }

    override fun updateThreadContext(context: CoroutineContext): ChatSession {
        val oldState = a.get()
        a.set(session)
        return oldState
    }

    override fun restoreThreadContext(context: CoroutineContext, oldState: ChatSession) {
        a.set(oldState)
    }
}

data class ChatSession(val chatId: Long, val userId: Long)
