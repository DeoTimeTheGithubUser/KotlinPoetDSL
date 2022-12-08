package dsl.utils

import dsl.Attributes

typealias Assembler<T> = T.() -> Unit

inline fun <T : Attributes.Buildable<B>, B> T.buildWith(assembler: Assembler<T>) =
    apply(assembler).build()


