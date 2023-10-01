package kek.team.kokline.factories

object Queue {
    private val queue: MutableList<String> = mutableListOf()

    val nextMessage: String? get() = synchronized(this) {
        val result = queue.firstOrNull()

        if (result != null) {
            queue.removeAt(0)
        }

        result
    }

    fun add(value: String) {
        synchronized(this) {
            queue.add(value)
        }
    }
}
