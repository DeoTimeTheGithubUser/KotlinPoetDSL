package dsl.utils

import dsl.Assembler

interface CollectionBuilder<T> {
    operator fun T.unaryPlus()
    operator fun T.unaryMinus()
}

typealias CollectionAssembler<T> = Assembler<CollectionBuilder<T>>

internal inline fun <T, C : MutableCollection<T>> buildCollectionTo(col: C, assembler: Assembler<CollectionBuilder<T>>): C {
    object : CollectionBuilder<T> {
        override fun T.unaryPlus() {
            col += this
        }
        override fun T.unaryMinus() {
            col -= this
        }
    }.assembler()
    return col
}