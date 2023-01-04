package me.deotime.kpoetdsl

import kotlin.reflect.KProperty

class Cozy<T> {
    private var value: T? = null
    operator fun getValue(ref: Any?, prop: KProperty<*>?) = value!!
    operator fun invoke(ref: T) {
        value = ref
    }

    interface Initializer<T> {
        fun cozy(): T
    }

}

inline fun <reified T> cozied(noinline initializer: (Cozy<T>) -> T) = object : Cozy.Initializer<T> {
    override fun cozy() = Cozy<T>().let { cozy -> initializer(cozy).also { cozy(it) } }
}