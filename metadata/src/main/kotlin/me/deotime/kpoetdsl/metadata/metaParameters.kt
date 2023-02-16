package me.deotime.kpoetdsl.metadata

import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName
import kotlinx.metadata.KmValueParameter
import me.deotime.kpoetdsl.Cozy.Initializer.Simple.Companion.invoke
import me.deotime.kpoetdsl.ExperimentalKotlinPoetDSL
import me.deotime.kpoetdsl.ParameterBuilder

fun KmValueParameter.toSpec() = let { km ->
    ParameterBuilder {
        name(km.name)
        type(km.type.asTypeName())
        modifiers {
            +km.flags.toParameterModifiers()
            km.varargElementType?.let {
                +KModifier.VARARG
                // use the array type
                type((type as ParameterizedTypeName).typeArguments.first())
            }
        }
    }
}