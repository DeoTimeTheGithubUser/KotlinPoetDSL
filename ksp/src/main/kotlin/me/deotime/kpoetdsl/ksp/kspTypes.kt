package me.deotime.kpoetdsl.ksp

import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeArgument
import com.google.devtools.ksp.symbol.KSTypeReference
import com.google.devtools.ksp.symbol.Variance
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.STAR
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeVariableName

// todo implement caching for these

fun KSTypeReference.asTypeName() = resolve().asTypeName()

fun KSType.asTypeName(): TypeName = declaration.asClassName().let {
    if (arguments.isNotEmpty()) it.parameterizedBy(
        arguments.map { it.asTypeVariableName() }
    ) else it
}.copy(nullable = isMarkedNullable)

fun KSTypeArgument.asTypeVariableName(): TypeName =
    if (variance == Variance.STAR) STAR
    else TypeVariableName(
        "$type",
        bounds = listOfNotNull(type?.resolve()?.asTypeName()),
        variance = when (variance) {
            Variance.COVARIANT -> KModifier.OUT
            Variance.CONTRAVARIANT -> KModifier.IN
            else -> null
        },
    )