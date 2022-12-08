package dsl

import kotlin.reflect.KProperty

class Cozy<T> {
    private var value: Any? = null
    operator fun getValue(ref: Any?, prop: KProperty<*>?) = value!! as T
    operator fun invoke(ref: Any?) {
        value = ref
    }

    abstract class Initializer<T>(private val initializer: (Cozy<T>) -> T) {
        fun cozy() = Cozy<T>().let { cozy -> initializer(cozy).also { cozy(it) } }
    }
}