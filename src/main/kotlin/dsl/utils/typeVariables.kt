package dsl.utils

import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.asTypeName
import kotlin.reflect.KClass

fun typeParameter(name: String) = TypeVariableName(name)
@JvmName("boundedTypeParameter")
inline fun <reified T> typeParameter(name: String) = TypeVariableName(name, T::class.java)

infix fun TypeVariableName.bounds(bounds: List<KClass<*>>) = copy(bounds = bounds.map { it.asTypeName() })
infix fun TypeVariableName.bounded(bounded: KClass<*>) = bounds(listOf(bounded))