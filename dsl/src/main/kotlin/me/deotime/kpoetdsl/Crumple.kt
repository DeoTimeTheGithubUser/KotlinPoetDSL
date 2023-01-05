package me.deotime.kpoetdsl

import me.deotime.kpoetdsl.Cozy.Initializer.Simple.Companion.cozy
import me.deotime.kpoetdsl.utils.buildWith

fun interface Crumple<A, B> {
    operator fun A.invoke(closure: B.() -> Unit): A
}

internal fun <A, B, C> unstableMaybeCozyCrumple(
    init: () -> Cozy.Initializer.Simple<B>,
    functor: (A) -> C
): Crumple<A, B>
        where B : Maybe<C>, B : Attributes.Buildable<A> =
    Crumple { closure ->
        init().cozy().apply {
            value = functor(this@Crumple)
        }.buildWith(closure)
    }