package me.deotime.kpoetdsl.metadata

import com.squareup.kotlinpoet.ClassName
import kotlinx.metadata.Flag
import kotlinx.metadata.Flags
import kotlinx.metadata.KmAnnotation
import kotlinx.metadata.KmAnnotationArgument
import kotlinx.metadata.KmClass
import kotlinx.metadata.KmConstructor
import kotlinx.metadata.internal.writeAnnotationArgument
import me.deotime.kpoetdsl.AnnotationBuilder
import me.deotime.kpoetdsl.Attributes.Buildable.Companion.buildWith
import me.deotime.kpoetdsl.CodeBuilder
import me.deotime.kpoetdsl.Cozy.Initializer.Simple.Companion.invoke
import me.deotime.kpoetdsl.ExperimentalKotlinPoetDSL
import me.deotime.kpoetdsl.FunctionBuilder
import me.deotime.kpoetdsl.PropertyBuilder.Initializer.invoke
import me.deotime.kpoetdsl.TypeBuilder
import me.deotime.kpoetdsl.TypeKind

@ExperimentalKotlinPoetDSL
fun KmAnnotation.toSpec() = let { km ->
    AnnotationBuilder {
        type(ClassName.bestGuess(km.className))
        km.arguments.mapValues { it as KmAnnotationArgument.LiteralValue<Any> }.forEach { (name, value) ->
            member {
                +"$name = ${value.value}"
            }
        }
    }
}
