package dsl

import kotlin.reflect.KProperty

class Cozy<T> {
    private var value: Any? = null
    operator fun getValue(ref: Any?, prop: KProperty<*>?) = value!! as T
    operator fun invoke(ref: Any) { value = ref }
}