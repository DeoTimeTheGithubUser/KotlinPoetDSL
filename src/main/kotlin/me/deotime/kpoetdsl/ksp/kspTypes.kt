package me.deotime.kpoetdsl.ksp

import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeArgument
import com.google.devtools.ksp.symbol.Variance
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeVariableName

fun KSType.asTypeName() = declaration.className.parameterizedBy(
    this.arguments.mapNotNull {
        it.type?.resolve()?.let {
            it.
        }
    }
)

fun KSTypeArgument.asTypeVariableName() = TypeVariableName(
    this.type?.toString()!!,
    when(variance) {
        Variance.INVARIANT -> KModifier.IN
        Variance.COVARIANT -> KModifier.OUT
    }
)