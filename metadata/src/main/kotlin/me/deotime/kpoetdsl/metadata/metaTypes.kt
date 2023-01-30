package me.deotime.kpoetdsl.metadata

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.STAR
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeVariableName
import kotlinx.metadata.Flag
import kotlinx.metadata.KmAnnotationArgument
import kotlinx.metadata.KmClassifier
import kotlinx.metadata.KmType
import kotlinx.metadata.KmTypeParameter
import kotlinx.metadata.KmTypeProjection
import kotlinx.metadata.KmVariance
import kotlinx.metadata.jvm.annotations
import me.deotime.kpoetdsl.ExperimentalKotlinPoetDSL
import me.deotime.kpoetdsl.types.lambdaType

val KmType.rawName
    get() = when (val classifier = classifier) {
        is KmClassifier.Class -> classifier.name
        is KmClassifier.TypeAlias -> classifier.name
        is KmClassifier.TypeParameter -> "TypeVar${classifier.id}" // Not really sure what to do with this
    }.replace("/", ".")

fun KmVariance?.toModifier() = when (this) {
    KmVariance.IN -> KModifier.IN
    KmVariance.OUT -> KModifier.OUT
    else -> null
}

private const val ExtensionLambdaAnnotation = "kotlin/ExtensionFunctionType"
private const val ContextLambdaAnnotation = "kotlin/ContextFunctionTypeParams"

/**
 * WARNING: This function wil fail if a suspending lambda type
 * with context receivers is put in as KotlinPoet renders it incorrectly.
 * https://github.com/square/kotlinpoet/issues/1452
 */
fun KmType.asTypeName(): TypeName =
    (if (rawName.startsWith("kotlin.Function") || rawName.startsWith("kotlin.reflect.KFunction"))
        lambdaType {
            suspend = Flag.Type.IS_SUSPEND(flags)
            arguments.map { it.asTypeName() }.toMutableList().apply {
                val annos = annotations.associateBy { it.className }
                if (suspend) {
                    removeLast()
                    (removeLast() as? ParameterizedTypeName)?.typeArguments?.firstOrNull()?.let {
                        returns = it
                    }
                } else returns = removeLast()
                annos[ContextLambdaAnnotation]?.let {
                    if (suspend)
                        error(
                            """
                            
                            
                            KotlinPoet currently renders suspending lambda types with context
                            receivers incorrectly, and as such cannot be used.
                            See: https://github.com/square/kotlinpoet/issues/1452
                            
                        """.trimIndent()
                        )
                    val count = (it.arguments["count"] as KmAnnotationArgument.IntValue).value
                    context {
                        repeat(count) { +removeFirst() }
                    }
                }
                if (ExtensionLambdaAnnotation in annos.keys) {
                    receiver = removeFirst()
                }
                parameters {
                    +this@apply
                }
            }

        }
    else if (classifier is KmClassifier.TypeParameter) TypeVariableName(rawName)
    else ClassName.bestGuess(rawName).let {
        if (arguments.isNotEmpty()) it.parameterizedBy(arguments.map { it.asTypeName() })
        else it
    }).copy(Flag.Type.IS_NULLABLE(flags))

fun KmTypeParameter.asTypeName(): TypeVariableName =
    TypeVariableName.invoke(
        name,
        upperBounds.map { it.asTypeName() },
        variance = variance.toModifier()
    )

fun KmTypeProjection.asTypeName() = let { km ->
    km.type?.asTypeName() ?: STAR
}