package me.deotime.kpoetdsl.utils

import kotlin.reflect.KProperty

interface Population<T> {
    val All: List<T>
    operator fun T.provideDelegate(ref: Any?, prop: KProperty<*>)
}

internal class PopulationImpl<T> : Population<T> {
    override val All: MutableList<T> = mutableListOf()
    override fun T.provideDelegate(ref: Any?, prop: KProperty<*>) {
        All.add(this)
    }
}

fun <T> population(): Population<T> = PopulationImpl()