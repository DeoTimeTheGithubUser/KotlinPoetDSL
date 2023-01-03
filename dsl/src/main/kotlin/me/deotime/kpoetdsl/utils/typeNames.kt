package me.deotime.kpoetdsl.utils

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.asTypeName
import kotlin.reflect.typeOf


inline fun <reified T : Any> className() = T::class.asClassName()

operator fun ClassName.plus(child: String) = ClassName(packageName, simpleNames + child)

inline operator fun <reified T> ClassName.invoke() =
    parameterizedBy(typeOf<T>().asTypeName())

inline operator fun <reified A, reified B> ClassName.invoke(overload0: Nothing? = null) =
    parameterizedBy(
        typeOf<A>().asTypeName(),
        typeOf<B>().asTypeName()
    )

inline operator fun <reified A, reified B, reified C> ClassName.invoke(
    overload0: Nothing? = null,
    overload1: Nothing? = null
) =
    parameterizedBy(
        typeOf<A>().asTypeName(),
        typeOf<B>().asTypeName(),
        typeOf<C>().asTypeName()
    )