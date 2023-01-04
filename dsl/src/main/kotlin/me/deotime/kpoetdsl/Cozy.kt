package me.deotime.kpoetdsl

import me.deotime.kpoetdsl.utils.Empty
import kotlin.reflect.KProperty

class Cozy<T> {
    private var value: T? = null
    operator fun getValue(ref: Any?, prop: KProperty<*>?) = value!!
    operator fun invoke(ref: T) {
        value = ref
    }

    interface Initializer<T, C> {

        fun cozy(context: C): T

        interface Simple<T> : Initializer<T, Empty> {
            companion object {
                fun <T> Simple<T>.cozy() = cozy(Empty)
            }
        }
    }

}

fun <T> cozied(initializer: (Cozy<T>) -> T) = object : Cozy.Initializer.Simple<T> {
    override fun cozy(context: Empty) = Cozy<T>().let { cozy -> initializer(cozy).also { cozy(it) } }
}

fun <T, C> cozied(initializer: (Cozy<T>, C) -> T) = object : Cozy.Initializer<T, C> {
    override fun cozy(context: C) = Cozy<T>().let { cozy -> initializer(cozy, context).also { cozy(it) } }
}