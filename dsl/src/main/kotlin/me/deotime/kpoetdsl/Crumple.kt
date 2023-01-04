package me.deotime.kpoetdsl

import me.deotime.kpoetdsl.Cozy.Initializer.Simple.Companion.cozy

fun interface Crumple<A, B> {
    operator fun A.invoke(closure: B.() -> Unit): A
}

@Suppress("UNCHECKED_CAST")
internal fun <B : Maybe<C>, A, C> unstableMaybeCozyCrumple(init: () -> Cozy.Initializer.Simple<B>, functor: (A) -> C): Crumple<A, B> =
    Crumple { closure ->
        (init().cozy().apply {
            value = functor(this@Crumple)
            closure()
        } as Attributes.Buildable<A>).build()
    }