package dsl

fun interface Buildable<T> {
    fun build(): T
}

typealias Assembler<T> = T.() -> Unit

inline fun <T : Buildable<B>, B> T.buildWith(assembler: Assembler<T>) =
    apply(assembler).build()

