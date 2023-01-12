package me.deotime.kpoetdsl.utils

import me.deotime.kpoetdsl.Cozy
import me.deotime.kpoetdsl.Maybe
import kotlin.properties.Delegates
import kotlin.reflect.KProperty


class Required<T>(private var holder: (() -> Holder)? = null) {

    private var prop: KProperty<*> by Delegates.notNull()
    private var value: T? = null

    operator fun provideDelegate(ref: Holder, prop: KProperty<*>): Required<T> {
        holder ?: run { holder = { ref } }
        this.prop = prop
        ref.required += this
        return this
    }

    operator fun setValue(ref: Holder, prop: KProperty<*>, value: T) {
        this.value = value
        ref.required -= this
    }

    operator fun getValue(ref: Any, prop: KProperty<*>) =
        value ?: error("The property ${this.prop.name} is required to be initialized before it can be accessed.")


    class Accessor<T>(
        private val closure: () -> T,
        private var value: () -> T? = { null }
    ) {
        operator fun getValue(ref: Holder, prop: KProperty<*>) =
            value() ?: ref.required.takeIf { it.isNotEmpty() }
                ?.let { error("The required properties ${it.map { req -> req.prop }} need to be initialized before use.") }
            ?: closure().also {
                value = { it }
            }
    }

    interface Holder {
        val required: MutableList<Required<*>>
    }
}

internal class RequiredHolderImpl : Required.Holder {
    override val required = mutableListOf<Required<*>>()
}

fun <T> required() = Required<T>()
fun <T> requiredByCozy(cozy: Cozy<out Required.Holder>) = Required<T> { cozy.getValue(null, null) }
fun <T> withRequired(closure: () -> T) = Required.Accessor(closure)

internal fun <S, T : Maybe<S>> T.withRequired(closure: () -> S) = Required.Accessor(closure, this::value)

internal fun requiredHolder(): Required.Holder = RequiredHolderImpl()