package me.deotime.kpoetdsl.metadata

import com.squareup.kotlinpoet.KModifier
import kotlinx.metadata.KmValueParameter
import me.deotime.kpoetdsl.Cozy.Initializer.Simple.Companion.invoke
import me.deotime.kpoetdsl.ExperimentalKotlinPoetDSL
import me.deotime.kpoetdsl.ParameterBuilder

@ExperimentalKotlinPoetDSL
fun KmValueParameter.toSpec() = let { km ->
    ParameterBuilder {
        name(km.name)
        type(km.type.asTypeName())
        modifiers{
            +km.flags.toModifiers()
            km.varargElementType?.let { +KModifier.VARARG }
        }
    }
}