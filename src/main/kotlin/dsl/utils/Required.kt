package dsl.utils

import dsl.Attributes
import java.util.IdentityHashMap
import kotlin.properties.Delegates
import kotlin.reflect.KProperty

private val requiredProperties = IdentityHashMap<Any, MutableList<Required<*>>>()
class Required<T>(private var holder: (() -> Any?)? = null) {

    private var prop: KProperty<*> by Delegates.notNull()
    private var value: T? = null

    operator fun provideDelegate(ref: Any, prop: KProperty<*>): Required<T> {
        holder ?: run { holder = { ref } }
        this.prop = prop
        requiredProperties.computeIfAbsent(ref) { mutableListOf() }.add(this)
        return this
    }

    operator fun setValue(ref: Any, prop: KProperty<*>, value: T) {
        this.value = value
        requiredProperties[ref]?.remove(this)
    }

    operator fun getValue(ref: Any, prop: KProperty<*>) =
        value ?: error("The property ${this.prop} is required to be initialized before it can be accessed.")


    class Accessor<T>(private val closure: () -> T) {
        private var value: T? = null
        operator fun getValue(ref: Any, prop: KProperty<*>) =
            value ?: requiredProperties[ref]?.takeIf { it.isNotEmpty() }?.let { error("The required properties ${it.map { it.prop }} need to be initialized before use.") }
                ?: closure().also {
                    requiredProperties.remove(ref)
                    value = it
                }
    }
}

fun <T> required() = Required<T>()
fun <T> requiredByCozy(cozy: Cozy<*>) = Required<T> { cozy.getValue(null, null) }
fun <T> withRequired(closure: () -> T) = Required.Accessor(closure)