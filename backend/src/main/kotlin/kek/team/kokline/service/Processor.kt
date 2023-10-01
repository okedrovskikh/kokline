package kek.team.kokline.service

interface Processor<T, S> {

    fun process(value: T): S

    fun <K> before(before: Processor<K, T>): Processor<K, S> = before.after(this)

    fun <K> after(after: Processor<S, K>): Processor<T, K> = object : Processor<T, K> {
        override fun process(value: T): K = after.process(this@Processor.process(value))
    }
}

fun <T, K> createProcessor(processingFunction: (T) -> K): Processor<T, K> = object : Processor<T, K> {
    override fun process(value: T): K = processingFunction.invoke(value)
}
