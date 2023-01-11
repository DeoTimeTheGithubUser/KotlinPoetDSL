package me.deotime.kpoetdsl

import me.deotime.kpoetdsl.utils.Empty
import me.deotime.kpoetdsl.Attributes.Buildable.Companion.buildWith
import kotlin.reflect.KProperty

class Cozy<T> {
    private var value: T? = null
    operator fun getValue(ref: Any?, prop: KProperty<*>?) = value!!
    operator fun invoke(ref: T) {
        value = ref
    }

    fun interface Initializer<T, C> {

        fun cozy(context: C): T

        fun interface Simple<T> : Initializer<T, Empty> {
            companion object {
                fun <T> Simple<T>.cozy() = cozy(Empty)

                operator fun <A, B, C> A.invoke(closure: B.() -> Unit)
                        where A : Simple<B>, B : Attributes.Buildable<C> = cozy().buildWith(closure)

                operator fun <A : Simple<B>, B> A.invoke() = cozy()
            }
        }
    }

}

fun <T> cozied(initializer: (Cozy<T>) -> T) = Cozy.Initializer.Simple { Cozy<T>().let { cozy -> initializer(cozy).also { cozy(it) } } }

fun <T, C> cozied(initializer: (Cozy<T>, C) -> T) =
    Cozy.Initializer<T, C> { context -> Cozy<T>().let { cozy -> initializer(cozy, context).also { cozy(it) } } }