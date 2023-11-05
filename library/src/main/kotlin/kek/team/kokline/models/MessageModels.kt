package kek.team.kokline.models

import kotlinx.serialization.Serializable

@Serializable
class MessagePayload(val text: String?, val binary: ByteArray?, val messageRefId: Long?) {

    override fun hashCode(): Int {
        var result = text?.hashCode() ?: 0
        result = 31 * result + (binary?.contentHashCode() ?: 0)
        result = 31 * result + (messageRefId?.hashCode() ?: 0)
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MessagePayload

        if (text != other.text) return false
        if (binary != null) {
            if (other.binary == null) return false
            if (!binary.contentEquals(other.binary)) return false
        } else if (other.binary != null) return false
        if (messageRefId != other.messageRefId) return false

        return true
    }
}

data class Message(val id: Long?, val payload: MessagePayload, val chatId: Long)

data class WebSocketMessageCreateRequest(val payload: MessagePayload)

data class MessageEditRequest(val id: Long, val payload: MessagePayload)
