package dsl

import kotlin.reflect.KProperty

class Cozy<T> {
    private var value: T? = null
    operator fun getValue(ref: Any?, prop: KProperty<*>?) = value
    operator fun invoke(ref: T) { value = ref }

    abstract class Initializer<T>(private val initializer: (Cozy<T>) -> T) {
        fun cozy() = Cozy<T>().let { cozy -> initializer(cozy).also { cozy(it) } }
    }
}