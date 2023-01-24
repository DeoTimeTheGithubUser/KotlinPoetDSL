package me.deotime.kpoetdsl.metadata

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.STAR
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeVariableName
import kotlinx.metadata.KmClassifier
import kotlinx.metadata.KmType
import kotlinx.metadata.KmTypeProjection
import kotlinx.metadata.KmVariance
import me.deotime.kpoetdsl.ExperimentalKotlinPoetDSL

@ExperimentalKotlinPoetDSL
val KmType.rawName
    get() = when (val classifier = classifier) {
        is KmClassifier.Class -> classifier.name
        is KmClassifier.TypeAlias -> classifier.name
        is KmClassifier.TypeParameter -> error("Not entirely sure what to do with this")
    }

fun KmVariance?.toModifier() = when (this) {
    KmVariance.IN -> KModifier.IN
    KmVariance.OUT -> KModifier.OUT
    else -> null
}

@ExperimentalKotlinPoetDSL
fun KmType.asTypeName(): TypeName =
    ClassName.bestGuess(rawName).parameterizedBy(arguments.map { it.asTypeName() })

@ExperimentalKotlinPoetDSL
fun KmTypeProjection.asTypeName() = let { km ->
    km.type?.let {gi
        TypeVariableName(
            it.rawName,
            km.variance.toModifier()
        )
    } ?: STAR
}