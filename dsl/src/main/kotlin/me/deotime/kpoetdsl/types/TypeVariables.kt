package me.deotime.kpoetdsl.types

import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.asTypeName
import kotlin.reflect.KClass
import kotlin.reflect.typeOf

fun typeParameter(name: String) = TypeVariableName(name)

@JvmName("boundedTypeParameter")
inline fun <reified T> typeParameter(name: String) = TypeVariableName(name, typeOf<T>().asTypeName())

infix fun TypeVariableName.bounds(bounds: List<KClass<*>>) = copy(bounds = bounds.map { it.asTypeName() })
infix fun TypeVariableName.bounded(bounded: KClass<*>) = bounds(listOf(bounded))