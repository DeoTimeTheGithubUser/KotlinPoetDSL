package me.deotime.kpoetdsl.utils

interface CollectionBuilder<T> {
    operator fun T.unaryPlus()
    operator fun T.unaryMinus()
}

@PublishedApi
internal class CollectionBuilderImpl<A, T>(
    private val collection: MutableCollection<A>,
    private val transform: (T) -> A
) : CollectionBuilder<T> {
    override fun T.unaryPlus() {
        collection += transform(this)
    }

    override fun T.unaryMinus() {
        collection -= transform(this)
    }
}

typealias CollectionAssembler<T> = Assembler<CollectionBuilder<T>>

inline fun <T, C : MutableCollection<T>> buildCollectionTo(col: C, assembler: Assembler<CollectionBuilder<T>>) =
    buildCollectionTo(col, ::identity, assembler)

inline fun <A, T, C : MutableCollection<A>> buildCollectionTo(
    col: C,
    noinline transform: (T) -> A,
    assembler: Assembler<CollectionBuilder<T>>
) {
    CollectionBuilderImpl(col, transform).assembler()
}